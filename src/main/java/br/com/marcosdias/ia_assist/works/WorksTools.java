package br.com.marcosdias.ia_assist.works;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Ferramentas (tool calling) que permitem ao modelo manter o repositorio de
 * estudos em works/: listar a estrutura, ler, buscar e criar/atualizar arquivos.
 * Todos os caminhos sao relativos ao diretorio base (app.works.dir) e qualquer
 * tentativa de escapar dele e rejeitada.
 */
@Component
public class WorksTools {

    private static final Logger logger = LoggerFactory.getLogger(WorksTools.class);

    private static final long MAX_FILE_BYTES = 512 * 1024; // limite de leitura por arquivo
    private static final List<String> TEXT_EXTENSIONS = List.of(".md", ".txt");

    private final Path baseDir;

    public WorksTools(@Value("${app.works.dir}") String worksDir) throws IOException {
        this.baseDir = Path.of(worksDir).toAbsolutePath().normalize();
        Files.createDirectories(baseDir);
    }

    @Tool(description = """
            Lista a estrutura completa de pastas e arquivos do repositorio de estudos (works/).
            Use sempre antes de criar qualquer pasta ou conteudo, para verificar o que ja existe.""")
    public String listarEstrutura() {
        try (Stream<Path> paths = Files.walk(baseDir)) {
            List<String> lines = paths
                    .filter(p -> !p.equals(baseDir))
                    .map(this::toRelative)
                    .sorted()
                    .toList();
            if (lines.isEmpty()) {
                return "O repositorio works/ esta vazio.";
            }
            return String.join("\n", lines);
        } catch (IOException e) {
            logger.error("Falha ao listar a estrutura de works/", e);
            return "Erro ao listar a estrutura: " + e.getMessage();
        }
    }

    @Tool(description = """
            Le o conteudo de um arquivo de texto (.md ou .txt) do repositorio de estudos.
            O caminho e relativo a works/, ex.: conteudos/Java/java.md""")
    public String lerArquivo(
            @ToolParam(description = "Caminho relativo a works/, ex.: vagas/empresa-x/vaga.md") String caminho) {
        try {
            Path file = resolve(caminho);
            if (!Files.isRegularFile(file)) {
                return "Arquivo nao encontrado: " + caminho;
            }
            if (!hasTextExtension(file)) {
                return "So consigo ler arquivos .md ou .txt por esta ferramenta. Arquivo: " + caminho;
            }
            if (Files.size(file) > MAX_FILE_BYTES) {
                return "Arquivo muito grande para leitura: " + caminho;
            }
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (IOException e) {
            logger.error("Falha ao ler arquivo {}", caminho, e);
            return "Erro ao ler o arquivo: " + e.getMessage();
        }
    }

    @Tool(description = """
            Busca um termo (sem diferenciar maiusculas) nos nomes de pastas/arquivos e no conteudo
            dos arquivos .md/.txt de works/. Use para verificar se um conteudo equivalente ja existe
            antes de criar um novo. Retorna os caminhos encontrados.""")
    public String buscarConteudos(
            @ToolParam(description = "Termo a buscar, ex.: 'Kafka', 'ECS', 'OAuth2'") String termo) {
        String needle = termo.toLowerCase(Locale.ROOT).trim();
        if (needle.isEmpty()) {
            return "Informe um termo de busca.";
        }
        List<String> nameMatches = new ArrayList<>();
        List<String> contentMatches = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(p -> !p.equals(baseDir)).forEach(p -> {
                String relative = toRelative(p);
                if (relative.toLowerCase(Locale.ROOT).contains(needle)) {
                    nameMatches.add(relative);
                } else if (Files.isRegularFile(p) && hasTextExtension(p)) {
                    try {
                        if (Files.size(p) <= MAX_FILE_BYTES
                                && Files.readString(p, StandardCharsets.UTF_8)
                                        .toLowerCase(Locale.ROOT).contains(needle)) {
                            contentMatches.add(relative);
                        }
                    } catch (IOException ignored) {
                        // arquivo ilegivel nao interrompe a busca
                    }
                }
            });
        } catch (IOException e) {
            logger.error("Falha ao buscar '{}' em works/", termo, e);
            return "Erro na busca: " + e.getMessage();
        }
        if (nameMatches.isEmpty() && contentMatches.isEmpty()) {
            return "Nenhum resultado para '" + termo + "' em works/.";
        }
        StringBuilder sb = new StringBuilder();
        if (!nameMatches.isEmpty()) {
            sb.append("Encontrado no nome de pasta/arquivo:\n")
                    .append(String.join("\n", nameMatches));
        }
        if (!contentMatches.isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append("\n\n");
            }
            sb.append("Encontrado dentro do conteudo de:\n")
                    .append(String.join("\n", contentMatches));
        }
        return sb.toString();
    }

    @Tool(description = """
            Cria ou atualiza um arquivo .md ou .txt dentro do repositorio de estudos, criando as
            pastas intermediarias se necessario. O caminho e relativo a works/,
            ex.: conteudos/Docker/docker.md. Antes de criar conteudo novo, sempre verifique
            duplicidade com buscarConteudos.""")
    public String criarOuAtualizarArquivo(
            @ToolParam(description = "Caminho relativo a works/, ex.: vagas/empresa-x/analise.md") String caminho,
            @ToolParam(description = "Conteudo completo do arquivo, em Markdown") String conteudo) {
        try {
            Path file = resolve(caminho);
            if (!hasTextExtension(file)) {
                return "So e permitido criar arquivos .md ou .txt. Caminho recebido: " + caminho;
            }
            boolean existed = Files.exists(file);
            Files.createDirectories(file.getParent());
            Files.writeString(file, conteudo, StandardCharsets.UTF_8);
            String action = existed ? "atualizado" : "criado";
            logger.info("Arquivo {} em works/: {}", action, caminho);
            return "Arquivo " + action + ": " + toRelative(file);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (IOException e) {
            logger.error("Falha ao gravar arquivo {}", caminho, e);
            return "Erro ao gravar o arquivo: " + e.getMessage();
        }
    }

    /** Resolve um caminho relativo garantindo que ele permaneca dentro de works/. */
    private Path resolve(String caminho) {
        if (caminho == null || caminho.isBlank()) {
            throw new IllegalArgumentException("Caminho vazio. Informe um caminho relativo a works/.");
        }
        Path resolved = baseDir.resolve(caminho.replace('\\', '/')).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new IllegalArgumentException("Caminho invalido (fora de works/): " + caminho);
        }
        return resolved;
    }

    private String toRelative(Path path) {
        String relative = baseDir.relativize(path).toString().replace('\\', '/');
        return Files.isDirectory(path) ? relative + "/" : relative;
    }

    private boolean hasTextExtension(Path file) {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        return TEXT_EXTENSIONS.stream().anyMatch(name::endsWith);
    }
}
