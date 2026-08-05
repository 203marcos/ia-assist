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

## Why token usage can spike

Simple Q&A over the chat is cheap: one RAG lookup, one model call, prompt caching covers the repeated system prompt/tools/history on every follow-up. Cost spikes when the model is asked to process something new (e.g. "analyze this job posting and update my study plan"), for a few compounding reasons:

- **Every tool round-trip resends the whole context.** Claude's API is stateless: each tool call/result is a new request carrying the full system prompt, tool schemas and conversation so far. Prompt caching discounts the *repeated prefix* (~90% off on a hit), but each new turn still adds fresh, uncached tokens and triggers another cache write — so a task that chains many tool calls (list → search → read → write, repeated per topic) multiplies cost with every step. This is inherent to the stateless request/response model and isn't something the app can fully avoid, only amortize via caching.
- **Generation is output-heavy, and output is the expensive side.** When the model creates new content, the system prompt caps each file at ~120 lines but still asks for a fairly complete write-up (concepts, example, pitfalls, interview questions, links) — and a single job-posting analysis can produce several such files (`vaga.md`, `plano-estudo.md`, plus any missing `conteudos/*` entries) in one go. Output tokens are priced several times higher than input tokens on Claude, so this is where a lot of the spend concentrates.

Two related gaps used to make this worse, and have since been fixed:

- **Duplicate-checking wasn't the same search as RAG.** `QuestionAnswerAdvisor` does a real vector similarity search, but only once, against the user's original message. Mid-task, the model was checking for existing content via `buscarConteudos`, which used to be a plain substring match over file names/contents — so notes phrased differently than the search term were invisible to it, and the model would write a duplicate instead of reusing what already existed. `buscarConteudos` now also runs a semantic similarity search against the vector store and reports matches by subject, not just by literal keyword.
- **Freshly written files weren't retrievable until re-ingested.** `criarOuAtualizarArquivo` used to just write to disk, so content created earlier in the same session wasn't in the vector store yet and couldn't ground a later lookup (or a later `buscarConteudos` semantic hit) until `/ingest/works` ran separately. It now re-ingests the file immediately after writing it, so it's searchable right away.

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
