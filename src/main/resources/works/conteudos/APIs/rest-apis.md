# REST APIs

## O que é?
REST (Representational State Transfer) é um estilo arquitetural para projetar APIs web. Uma REST API usa HTTP para comunicação e segue princípios de simplicidade, escalabilidade e padronização. Cada recurso é identificado por uma URL e operações são realizadas via métodos HTTP (GET, POST, PUT, DELETE).

## Para que serve?
REST APIs servem para:
- Permitir comunicação entre cliente e servidor
- Expor funcionalidades de um sistema para consumo externo
- Integrar diferentes aplicações
- Criar interfaces padronizadas e previsíveis

## Onde é usado?
- Aplicações web e mobile
- Microserviços
- Integrações entre sistemas
- Plataformas SaaS
- IoT e sistemas distribuídos

## Quando usar?
- Quando você precisa expor dados/funcionalidades via HTTP
- Para comunicação entre microserviços
- Quando clientes diversos precisam acessar o mesmo backend
- Para APIs públicas ou internas

## Quando NÃO usar?
- Comunicação em tempo real (use WebSockets)
- Transferência de arquivos muito grandes (use FTP ou S3)
- Quando latência é crítica (considere gRPC)

## Como funciona?

### Princípios REST

#### 1. **Client-Server**
Cliente e servidor são independentes. Cliente faz requisições, servidor responde.

#### 2. **Stateless**
Cada requisição contém todas as informações necessárias. Servidor não armazena contexto do cliente.

#### 3. **Cacheable**
Respostas podem ser cacheadas para melhorar performance.

#### 4. **Uniform Interface**
- Identificação de recursos (URIs)
- Manipulação via representações (JSON, XML)
- Mensagens auto-descritivas
- HATEOAS (opcional)

#### 5. **Layered System**
Arquitetura em camadas (cliente → API Gateway → Serviços)

### Métodos HTTP

| Método | Operação | Idempotente | Seguro |
|--------|----------|-------------|--------|
| GET | Ler | Sim | Sim |
| POST | Criar | Não | Não |
| PUT | Atualizar (completo) | Sim | Não |
| PATCH | Atualizar (parcial) | Não | Não |
| DELETE | Deletar | Sim | Não |
| HEAD | Como GET, sem body | Sim | Sim |
| OPTIONS | Descrever opções | Sim | Sim |

### Códigos de Status HTTP

| Código | Significado | Exemplo |
|--------|-------------|---------|
| 200 | OK | Requisição bem-sucedida |
| 201 | Created | Recurso criado |
| 204 | No Content | Sucesso, sem conteúdo |
| 400 | Bad Request | Requisição inválida |
| 401 | Unauthorized | Autenticação necessária |
| 403 | Forbidden | Acesso negado |
| 404 | Not Found | Recurso não encontrado |
| 500 | Internal Server Error | Erro no servidor |
| 503 | Service Unavailable | Serviço indisponível |

## Conceitos Importantes

### Recursos
Tudo é um recurso identificado por URI:
```
/usuarios          # Coleção de usuários
/usuarios/123      # Usuário específico
/usuarios/123/posts # Posts do usuário 123
```

### Representações
Recursos são representados em diferentes formatos:
```json
{
  "id": 123,
  "nome": "João",
  "email": "joao@email.com"
}
```

### Versionamento
Diferentes estratégias:
```
/api/v1/usuarios       # URL path
/usuarios?version=1    # Query parameter
Accept: application/vnd.api+json;version=1  # Header
```

### Paginação
```
GET /usuarios?page=1&limit=10
GET /usuarios?offset=0&limit=10
```

### Filtros e Busca
```
GET /usuarios?nome=João&idade=25
GET /usuarios?search=João
```

## Exemplos Reais

### Exemplo 1: API de Usuários em Java/Spring
```java
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = usuarioService.listar();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obter(@PathVariable Long id) {
        return usuarioService.obterPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@RequestBody UsuarioDTO dto) {
        UsuarioDTO criado = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(
        @PathVariable Long id,
        @RequestBody UsuarioDTO dto
    ) {
        UsuarioDTO atualizado = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Exemplo 2: Requisição e Resposta
```
POST /api/v1/usuarios HTTP/1.1
Host: api.exemplo.com
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "idade": 30
}

---

HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/v1/usuarios/123

{
  "id": 123,
  "nome": "João Silva",
  "email": "joao@email.com",
  "idade": 30,
  "criadoEm": "2024-01-15T10:30:00Z"
}
```

### Exemplo 3: Tratamento de Erros
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            404,
            "Recurso não encontrado",
            ex.getMessage()
        );
        return ResponseEntity.status(404).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        ValidationException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            400,
            "Erro de validação",
            ex.getMessage()
        );
        return ResponseEntity.status(400).body(error);
    }
}
```

## Principais Erros

### 1. **Usar GET para modificar dados**
```java
// ❌ Errado
@GetMapping("/deletar/{id}")
public void deletar(@PathVariable Long id) { ... }

// ✅ Correto
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletar(@PathVariable Long id) { ... }
```

### 2. **Retornar status HTTP incorreto**
```java
// ❌ Errado
@PostMapping
public UsuarioDTO criar(@RequestBody UsuarioDTO dto) {
    return usuarioService.criar(dto); // Retorna 200 OK
}

// ✅ Correto
@PostMapping
public ResponseEntity<UsuarioDTO> criar(@RequestBody UsuarioDTO dto) {
    UsuarioDTO criado = usuarioService.criar(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(criado);
}
```

### 3. **Expor detalhes internos em erros**
```java
// ❌ Errado
{
  "erro": "NullPointerException em linha 42 de UsuarioService.java"
}

// ✅ Correto
{
  "codigo": "USUARIO_NAO_ENCONTRADO",
  "mensagem": "Usuário com ID 123 não existe",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. **Não validar entrada**
```java
// ❌ Errado
@PostMapping
public ResponseEntity<UsuarioDTO> criar(@RequestBody UsuarioDTO dto) {
    return ResponseEntity.ok(usuarioService.criar(dto));
}

// ✅ Correto
@PostMapping
public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(usuarioService.criar(dto));
}
```

### 5. **Não documentar a API**
Use Swagger/OpenAPI para documentar endpoints.

## Melhores Práticas

### 1. **Use nomes descritivos e consistentes**
```
✅ /api/v1/usuarios
✅ /api/v1/usuarios/{id}/posts
✅ /api/v1/posts/{id}/comentarios

❌ /api/getUser
❌ /api/user_list
❌ /api/deletePost
```

### 2. **Versionamento de API**
```
✅ /api/v1/usuarios
✅ /api/v2/usuarios

❌ /usuarios (sem versão)
```

### 3. **Use status HTTP apropriados**
```java
// ✅ Bom
POST /usuarios → 201 Created
GET /usuarios/999 → 404 Not Found
DELETE /usuarios/123 → 204 No Content
POST /usuarios (dados inválidos) → 400 Bad Request

// ❌ Evitar
POST /usuarios → 200 OK
GET /usuarios/999 → 200 OK com {"erro": "não encontrado"}
```

### 4. **Implemente paginação**
```
GET /usuarios?page=1&limit=10
GET /usuarios?offset=0&limit=10

Resposta:
{
  "data": [...],
  "page": 1,
  "limit": 10,
  "total": 150,
  "totalPages": 15
}
```

### 5. **Use HATEOAS (opcional, mas recomendado)**
```json
{
  "id": 123,
  "nome": "João",
  "_links": {
    "self": { "href": "/usuarios/123" },
    "all": { "href": "/usuarios" },
    "posts": { "href": "/usuarios/123/posts" }
  }
}
```

### 6. **Documente com Swagger/OpenAPI**
```java
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {
    
    @GetMapping
    @Operation(summary = "Listar usuários")
    public ResponseEntity<List<UsuarioDTO>> listar() { ... }
    
    @PostMapping
    @Operation(summary = "Criar novo usuário")
    public ResponseEntity<UsuarioDTO> criar(@RequestBody UsuarioDTO dto) { ... }
}
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre REST e SOAP?**
REST usa HTTP e é mais simples. SOAP é mais complexo e usa XML. REST é mais popular hoje.

### 2. **O que é idempotência?**
Uma operação é idempotente se pode ser executada múltiplas vezes com o mesmo resultado. GET, PUT, DELETE são idempotentes.

### 3. **Como você versionaria uma API?**
Opções: URL path (`/v1/`), query parameter (`?version=1`), header (`Accept: application/vnd.api+json;version=1`).

### 4. **Como implementar paginação?**
Use query parameters: `?page=1&limit=10` ou `?offset=0&limit=10`. Retorne metadados (total, página atual, etc).

### 5. **Como tratar erros em REST?**
Use status HTTP apropriados (4xx para cliente, 5xx para servidor) e retorne JSON com detalhes do erro.

### 6. **O que é HATEOAS?**
Hypermedia As The Engine Of Application State. Respostas incluem links para ações relacionadas.

## Relação com Outras Tecnologias

- **Spring Boot:** Framework para criar REST APIs em Java
- **Quarkus/Micronaut:** Frameworks leves para REST APIs
- **OAuth/JWT:** Autenticação e autorização em REST APIs
- **Docker:** Containeriza aplicações REST
- **AWS:** Hospeda REST APIs em EC2, ECS, Lambda
- **MySQL:** Banco de dados para persistência

---

## Material de Estudo

### Documentação Oficial
- [REST API Best Practices - MDN](https://developer.mozilla.org/en-US/docs/Glossary/REST)
- [HTTP Status Codes - MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)

### Roadmap
- [roadmap.sh - REST API](https://roadmap.sh/rest-api)

### Cursos
- **Português:** [REST API com Spring Boot - Udemy](https://www.udemy.com/course/rest-api-spring-boot/)
- **Inglês:** [REST API Design Rulebook - Udemy](https://www.udemy.com/course/rest-api-design-rulebook/)

### Playlists YouTube
- **Português:** [REST API - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO9jLDbjWIUXIunfZiM5aAl9)
- **Inglês:** [REST API Tutorial - Traversy Media](https://www.youtube.com/watch?v=SLwpqD8n3d0)

### Livros
- **"RESTful Web Services"** - Leonard Richardson, Sam Ruby
- **"Building Web APIs with Flask"** - Miguel Grinberg

### Artigos e Blogs
- [REST API Best Practices - Baeldung](https://www.baeldung.com/rest-api-best-practices)
- [REST API Design - Medium](https://medium.com/tag/rest-api)

### GitHub Relevante
- [Awesome REST](https://github.com/marmelab/awesome-rest)

### Repositórios Exemplo
- [Spring Boot REST Examples](https://github.com/spring-projects/spring-boot/tree/main/spring-boot-samples)

### Projetos para Praticar
1. **API de Blog** (CRUD de posts e comentários)
2. **API de E-commerce** (produtos, pedidos, clientes)
3. **API de Tarefas** (TODO list com categorias)
4. **API de Rede Social** (usuários, posts, likes)
5. **API de Biblioteca** (livros, empréstimos, usuários)

---

## Certificações

Não há certificações específicas para REST APIs, mas conhecimento é essencial para:
- Oracle Certified Associate Java Programmer
- AWS Certified Developer Associate
- Kubernetes Application Developer (CKA)
