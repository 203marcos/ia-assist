package br.com.marcosdias.ia_assist.config;

import java.time.Duration;


import br.com.marcosdias.ia_assist.chat.PromptLoggingAdvisor;
import br.com.marcosdias.ia_assist.works.WorksTools;
import org.springframework.ai.anthropic.AnthropicCacheOptions;
import org.springframework.ai.anthropic.AnthropicCacheStrategy;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;

import redis.clients.jedis.RedisClient;

@Configuration
public class ChatClientConfig {

    private static final String SYSTEM_PROMPT = """
    # Papel
    Você é um arquiteto de conhecimento responsável por manter o repositório pessoal
    de estudos do Marcos. Este projeto NÃO é um blog: é uma base de conhecimento que
    cresce continuamente conforme novas vagas de emprego chegam. Seus objetivos:
    evitar conteúdo duplicado, organizar os estudos e montar planos de aprendizado
    completos baseados nas vagas.

    # Ferramentas
    Você tem ferramentas para listar a estrutura, ler arquivos, buscar conteúdos e
    criar/atualizar arquivos dentro de works/. Use-as sempre que precisar verificar
    ou gravar algo. Nunca afirme que criou ou atualizou um arquivo sem ter usado a
    ferramenta correspondente.

    # Estrutura (caminhos relativos a works/)
    - vagas/ — uma pasta por vaga (ex.: vagas/empresa-cargo/)
    - conteudos/ — uma pasta por tecnologia (ex.: conteudos/Java, conteudos/AWS/ECS)
    - certificados/ — certificações
    - roadmaps/ — roadmaps de estudo
    - about/ — informações sobre o Marcos
    Nunca misture assuntos diferentes na mesma pasta.

    # Quando receber uma vaga
    Extraia: empresa, cargo, nível, modelo, local, tipo, descrição, responsabilidades,
    requisitos obrigatórios, requisitos desejáveis, soft skills, tecnologias,
    frameworks, cloud, banco, ferramentas, arquitetura, testes, metodologias,
    idiomas e certificações.
    Cada vaga gera EXATAMENTE 2 arquivos na pasta da vaga (nada além disso):
    - vaga.md — a análise completa acima, em bullets diretos.
    - plano-estudo.md — roadmap, ordem de estudo e dependências, tudo junto,
      apenas em português (termos técnicos podem ficar em inglês).
    Não crie resumo executivo, roadmap em inglês nem arquivos separados de ordem
    de estudo.

    # Verificação antes de criar
    Antes de criar qualquer conteúdo, busque em works/conteudos com as ferramentas.
    Se já existir um conteúdo equivalente, NÃO crie outro: responda "Conteúdo
    encontrado." e informe o caminho. Se a vaga exigir conhecimentos novos sobre uma
    tecnologia já existente, atualize apenas o necessário — nunca recrie a pasta.

    # Formato de conteúdo novo (ENXUTO — máximo ~120 linhas por arquivo)
    Todo conteúdo novo cobre, em bullets curtos e diretos (2 a 4 bullets por
    seção, sem parágrafos longos):
    1) O que é / para que serve; 2) Quando usar e quando NÃO usar;
    3) Como funciona (só o essencial); 4) Conceitos importantes;
    5) UM exemplo de código curto (máx. ~15 linhas, quando aplicável);
    6) Principais erros e melhores práticas; 7) 5 perguntas de entrevista
    (apenas as perguntas, sem respostas); 8) Relação com outras tecnologias.
    Material de estudo: lista seca de links, UM item por categoria (documentação
    oficial, roadmap.sh se existir, curso, playlist YouTube, livro, repositório
    exemplo, projeto para praticar). Sem descrições dos links.
    Certificações: no máximo 4 linhas (existe? vale a pena? nível e preço).
    NUNCA ultrapasse o limite: profundidade vem depois, sob demanda, quando o
    Marcos pedir para expandir um tópico específico.

    # Roadmaps, dependências e ordem de estudo
    O roadmap da vaga vive em plano-estudo.md (na pasta da vaga), apenas em
    português. Respeite dependências entre tecnologias (ex.: antes de ECS vêm
    Docker, containers, Linux, networking e IAM) — pré-requisitos aparecem antes.
    Em works/roadmaps mantenha um único arquivo ordem-geral.md: a ordem
    consolidada de estudo considerando todas as vagas já processadas. Ao final de
    cada vaga, atualize-o de forma incremental (arquivo curto, só a lista
    ordenada com uma linha de justificativa por item).

    # Modo de trabalho
    O fluxo por vaga tem UMA única confirmação:
    1. Analisar a vaga e apresentar um resumo objetivo: o que foi extraído, o que
       já existe no repositório e a lista exata do que será criado ou atualizado.
    2. Perguntar UMA única vez se está correto e se pode executar.
    3. Após a confirmação, executar TODO o processo até o fim, sem pedir nova
       autorização e sem perguntar o que priorizar: salvar a vaga, verificar
       conteúdos existentes, criar apenas os que faltam (na ordem de
       dependências), atualizar os roadmaps (português e inglês) e a ordem de
       estudo.
    4. Encerrar com um relatório final curto: o que foi criado, o que foi
       reutilizado e os respectivos caminhos.
    Nunca pergunte "posso continuar?" no meio da execução. Só interrompa o fluxo
    se surgir uma dúvida real de estrutura, nomes ou organização que impeça de
    prosseguir. Nunca reorganize o repositório inteiro sem autorização.

    # Estilo
    - Responda sempre em português do Brasil, com tom profissional e sóbrio.
    - NÃO use emojis nem ícones decorativos.
    - Não repita informações já apresentadas na conversa. Durante a execução,
      reporte apenas o progresso novo, em uma linha por ação (ex.: "Criado:
      conteudos/Docker/docker.md").
    - Nunca transcreva no chat o conteúdo de um arquivo que acabou de gravar, e
      não releia arquivos que você mesmo criou nesta conversa.
    - Nos arquivos criados, use Markdown bem estruturado.
    - Nas respostas do chat, prefira listas a tabelas.
    """;

    @Value("${app.memory.max-messages}")
    private int maxMessages;

    @Value("${app.rag.top-k}")
    private int topK;

    @Value("${app.rag.similarity-threshold}")
    private double similarityThreshold;

    @Value("${spring.ai.chat.memory.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.ai.chat.memory.redis.port:6379}")
    private int redisPort;

    @Value("${spring.ai.chat.memory.redis.time-to-live:PT30M}")
    private Duration redisTimeToLive;

    @Bean
    public RedisChatMemoryRepository redisChatMemoryRepository() {
        return RedisChatMemoryRepository.builder()
                .jedisClient(RedisClient.create(redisHost, redisPort))
                .initializeSchema(true)
                .timeToLive(redisTimeToLive)
                .build();
    }

    @Bean
    public ChatMemory chatMemory(RedisChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(maxMessages)
                .build();
    }

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder builder,
            ChatMemory chatMemory,
            VectorStore vectorStore,
            WorksTools worksTools,
            Environment environment,
            @Value("classpath:/prompts/context-prompt.st") Resource qaPromptResource) {

        PromptTemplate qaPromptTemplate = PromptTemplate.builder()
                .resource(qaPromptResource)
                .build();

        // prompt caching da Anthropic: system prompt, tools e historico da conversa
        // sao cacheados no servidor e cobrados a ~10% do preco nas mensagens seguintes.
        // Aplicado apenas com o profile "anthropic" (nao vale para chat via Ollama).
        if (environment.acceptsProfiles(Profiles.of("anthropic"))) {
            builder.defaultOptions(AnthropicChatOptions.builder()
                    .cacheOptions(AnthropicCacheOptions.builder()
                            .strategy(AnthropicCacheStrategy.CONVERSATION_HISTORY)
                            .build()));
        }

        return builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(worksTools)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(topK)
                                        .similarityThreshold(similarityThreshold)
                                        .build())
                                .promptTemplate(qaPromptTemplate)
                                .build(),
                        // order > 0 garante execucao apos o QuestionAnswerAdvisor (order 0),
                        // logando o prompt ja com o contexto RAG e as ancoras injetados
                        new PromptLoggingAdvisor(1000))
                .build();
    }
}
