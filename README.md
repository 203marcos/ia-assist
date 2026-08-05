# IA Assist

A personal knowledge-base assistant built with Spring AI. It uses RAG (Retrieval-Augmented Generation) to answer questions grounded in a growing set of study notes, and exposes tool-calling so the model can read, search, and write new notes into that knowledge base by itself.

This project started from the architecture described in [Spring AI em ação: assistente de RH com RAG e Claude](https://devsuperior.com.br/blog/spring-ai-em-acao-assistente-de-rh-com-rag-e-claude) (DevSuperior, by Rafael Sotero) and evolved from there into a personal study/career assistant with tool-calling support.

## Stack

- **Java 25** + **Spring Boot 4**
- **Spring AI** — chat client, advisors, RAG and tool calling
- **Anthropic Claude** (`claude-haiku-4-5`) — chat model
- **Ollama** (`bge-m3`) — embeddings (1024 dimensions, multilingual)
- **PostgreSQL + pgvector** — vector store (HNSW index, cosine distance)
- **Redis Stack** — chat memory, with per-conversation TTL
- **Apache Tika** — document parsing (PDF, Markdown, plain text)
- **Docker Compose** — local Postgres, Redis and Ollama

## Architecture

Each request goes through a small advisor pipeline:

1. **`MessageChatMemoryAdvisor`** — injects the conversation history from Redis.
2. **`QuestionAnswerAdvisor`** — retrieves the top-K most relevant chunks from the vector store (configurable similarity threshold) and injects them into the prompt via a dedicated template.
3. **`PromptLoggingAdvisor`** — logs the fully-assembled prompt (system + context + history) for observability, without duplicating the framework's own error logging.

On top of that, the chat model has access to **tools** (`WorksTools`) that let it browse, read, search and create/update files inside the knowledge base directly — all paths are resolved and sandboxed relative to `works/`. When running with the `anthropic` profile, Anthropic prompt caching is enabled for the system prompt, tools and conversation history to cut latency and cost on multi-turn conversations.

## Prerequisites

- JDK 25 on your `PATH`
- Docker Desktop running (for Postgres, Redis and Ollama)
- An Anthropic API key

## Running locally

1. Start the local infrastructure:
   ```bash
   docker compose up -d
   ```
   First boot takes a couple of minutes while Ollama pulls `bge-m3` (~1.2 GB); check progress with:
   ```bash
   docker compose logs -f ollama
   ```
2. Export your Anthropic API key:
   ```bash
   export ANTHROPIC_API_KEY=your-key-here      # PowerShell: $env:ANTHROPIC_API_KEY="your-key-here"
   ```
3. Run the application with the `anthropic` profile:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=anthropic
   ```
4. Open `http://localhost:8080` for the browser chat UI.

## Endpoints

| Method | Route           | Description                                                       |
|--------|-----------------|---------------------------------------------------------------------|
| POST   | `/chat/stream`  | Sends a message and streams the reply token-by-token (SSE)          |
| POST   | `/ingest`       | Ingests a single uploaded file into the vector store                |
| POST   | `/ingest/works` | Re-scans `works/` and (re)ingests every `.md`, `.txt` and `.pdf` file |

### Examples

```bash
# Ingest a single file
curl -X POST -F "file=@some-document.pdf" http://localhost:8080/ingest

# Re-index the whole knowledge base
curl -X POST http://localhost:8080/ingest/works

# Chat (streamed as SSE)
curl -N -X POST http://localhost:8080/chat/stream \
  -H "Content-Type: application/json" \
  -H "X-Conversation-Id: session-1" \
  -d '{"message": "what do we already have about Docker?"}'
```

`X-Conversation-Id` is required and scopes the conversation history in Redis — reuse the same id across requests to keep context, use a new one to start fresh. Unlike `/ingest/works`, plain `/ingest` doesn't dedupe by source, so re-uploading the same file appends duplicate chunks.

## Project layout

```
src/main/java/.../chat/       # chat controller, service and prompt-logging advisor
src/main/java/.../config/     # ChatClient wiring (advisors, memory, RAG, caching)
src/main/java/.../ingestion/  # document reading/chunking into the vector store
src/main/java/.../works/      # tools that give the model read/write access to works/
src/main/resources/prompts/   # prompt templates
src/main/resources/works/     # the knowledge base used for RAG
```

## Configuration

Settings live in `application.yml` (shared defaults) and `application-anthropic.yml` (chat model profile). No credentials are stored in code — the Anthropic API key is read from the `ANTHROPIC_API_KEY` environment variable.

| Key                             | Default | Meaning                                        |
|----------------------------------|---------|-------------------------------------------------|
| `app.rag.top-k`                  | `4`     | Chunks retrieved per query                       |
| `app.rag.similarity-threshold`   | `0.5`   | Minimum similarity for a chunk to be retrieved   |
| `app.memory.max-messages`        | `20`    | Size of the conversation history window          |
| `spring.ai.chat.memory.redis.time-to-live` | `PT30M` | Redis session expiry after inactivity |
| `spring.ai.vectorstore.pgvector.dimensions` | `1024` | Must match the embedding model's output size |

You can inspect stored vectors with `psql` against the `vector_store` table in `ragdb`, and conversation history with RedisInsight at `http://localhost:8001`.

## Troubleshooting

- **401 from Anthropic** — `ANTHROPIC_API_KEY` isn't set in the same shell that runs `mvnw`, or the account has no credits.
- **Chat replies as if the knowledge base is empty** — make sure the app was started with `-Dspring-boot.run.profiles=anthropic` and that `/ingest/works` has run at least once.
- **pgvector insert/search errors** — usually a dimension mismatch; confirm `spring.ai.vectorstore.pgvector.dimensions` matches the embedding model's output (1024 for `bge-m3`).
- **Ollama connection timeout** — the container may still be pulling `bge-m3`; wait until `docker compose ps` shows it as `healthy`.
