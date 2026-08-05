package br.com.marcosdias.ia_assist.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class IngestionService {

    private static final Logger logger = LoggerFactory.getLogger(IngestionService.class);

    private static final List<String> INGESTABLE_EXTENSIONS = List.of(".md", ".txt", ".pdf");

    private final VectorStore vectorStore;
    private final Path worksDir;

    public IngestionService(VectorStore vectorStore, @Value("${app.works.dir}") String worksDir) {
        this.vectorStore = vectorStore;
        this.worksDir = Path.of(worksDir).toAbsolutePath().normalize();
    }

    /** Ingestao avulsa de um unico arquivo enviado por upload. */
    public int ingest(Resource file) {
        List<Document> documents = new TikaDocumentReader(file).read();
        List<Document> chunks = TokenTextSplitter.builder().build().apply(documents);
        vectorStore.add(chunks);
        return chunks.size();
    }

    /**
     * Varre works/ recursivamente e ingere todos os arquivos .md, .txt e .pdf.
     * Antes de gravar cada arquivo, remove os chunks anteriores com o mesmo
     * metadado "source", permitindo re-ingestao sem duplicar.
     */
    public Map<String, Integer> ingestWorks() throws IOException {
        Map<String, Integer> result = new LinkedHashMap<>();
        try (Stream<Path> paths = Files.walk(worksDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(this::isIngestable)
                    .sorted()
                    .forEach(file -> result.put(relativeSource(file), ingestWorksFile(file)));
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
        logger.info("Ingestao de works/ concluida: {} arquivos processados", result.size());
        return result;
    }

    /** Ingestao de um unico arquivo de works/, usada tanto pelo scan completo quanto por gravacoes pontuais. */
    public int ingestWorksFile(Path file) {
        String source = relativeSource(file);
        String tipo = source.contains("/") ? source.substring(0, source.indexOf('/')) : "raiz";

        // remove a versao anterior deste arquivo no vetor (re-ingestao idempotente)
        vectorStore.delete("source == '" + source + "'");

        List<Document> documents = new TikaDocumentReader(new FileSystemResource(file)).read();
        documents.forEach(doc -> {
            doc.getMetadata().put("source", source);
            doc.getMetadata().put("tipo", tipo);
        });
        List<Document> chunks = TokenTextSplitter.builder().build().apply(documents);
        vectorStore.add(chunks);
        logger.info("Ingerido works/{} ({} chunks, tipo={})", source, chunks.size(), tipo);
        return chunks.size();
    }

    private boolean isIngestable(Path file) {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        return INGESTABLE_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    private String relativeSource(Path file) {
        return worksDir.relativize(file).toString().replace('\\', '/');
    }
}
