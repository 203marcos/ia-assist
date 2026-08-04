# IA Assist

Assistente de estudos com RAG (Retrieval-Augmented Generation) construído com Spring AI. Ele mantém uma base de conhecimento própria (`works/`) que pode ler, buscar e atualizar sozinho via tool calling, além de responder perguntas com contexto recuperado de um vector store.

## Stack

- **Java 25** + **Spring Boot 4**
- **Spring AI** — orquestração de chat, tools e RAG
- **Anthropic Claude** — modelo de chat
- **Ollama** (`bge-m3`) — geração de embeddings
- **PostgreSQL + pgvector** — vector store
- **Redis** — memória de conversa (janela deslizante)
- **Docker Compose** — sobe banco, Redis e Ollama localmente

## Como rodar

1. Suba a infraestrutura local:
   ```bash
   docker compose up -d
   ```
2. Exporte a chave da Anthropic:
   ```bash
   export ANTHROPIC_API_KEY=sua-chave-aqui
   ```
3. Rode a aplicação com o profile `anthropic`:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=anthropic
   ```
4. Acesse `http://localhost:8080` para o chat via navegador.

## Endpoints

| Método | Rota            | Descrição                                              |
|--------|-----------------|----------------------------------------------------------|
| POST   | `/chat/stream`  | Envia uma mensagem e recebe a resposta em streaming (SSE) |
| POST   | `/ingest`       | Ingere um arquivo avulso (upload) no vector store          |
| POST   | `/ingest/works` | Reingere todo o conteúdo de `works/` no vector store      |

## Estrutura

```
src/main/java/.../chat/       # controller, service e advisor de logging do prompt
src/main/java/.../config/     # configuração do ChatClient (advisors, cache, RAG)
src/main/java/.../ingestion/  # leitura e chunking de documentos para o vector store
src/main/java/.../works/      # tools que dão ao modelo acesso de leitura/escrita a works/
src/main/resources/prompts/   # templates de prompt
src/main/resources/works/     # base de conhecimento usada no RAG
```

## Configuração

As configurações ficam em `application.yml` (padrões comuns) e `application-anthropic.yml` (profile do modelo de chat). Nenhuma credencial fica no código — a chave da Anthropic é lida da variável de ambiente `ANTHROPIC_API_KEY`.
