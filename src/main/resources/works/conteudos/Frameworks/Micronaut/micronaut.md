# Micronaut

## O que é?
Micronaut é um framework moderno para construir aplicações JVM (Java, Kotlin, Groovy) otimizado para microserviços e serverless. Desenvolvido pela OCI (Oracle Cloud Infrastructure), oferece inicialização rápida, baixo consumo de memória e suporte nativo a GraalVM.

## Para que serve?
Micronaut serve para:
- Desenvolver microserviços leves e rápidos
- Criar aplicações serverless
- Construir APIs REST de alta performance
- Integrar com cloud providers (AWS, GCP, Azure)

## Onde é usado?
- Microserviços em produção
- Aplicações serverless (AWS Lambda, Google Cloud Functions)
- APIs de baixa latência
- Empresas que precisam de inicialização rápida

## Quando usar?
- Quando você precisa de inicialização rápida (< 1s)
- Para microserviços com muitas instâncias
- Em ambientes serverless
- Quando memória é limitada (containers, Lambda)

## Quando NÃO usar?
- Aplicações monolíticas grandes (Spring Boot é melhor)
- Quando você precisa de muitos frameworks prontos
- Projetos com equipe inexperiente em Micronaut

## Como funciona?

### Arquitetura
```
Código Micronaut → Compilação (Annotation Processing) → Bytecode otimizado → JVM/GraalVM
```

Micronaut usa **compile-time dependency injection** em vez de reflection, resultando em:
- Inicialização mais rápida
- Menor consumo de memória
- Melhor performance

### Principais Características

#### 1. **Dependency Injection Compile-Time**
```kotlin
@Singleton
class UsuarioService {
    fun listar(): List<Usuario> = emptyList()
}

@Controller("/usuarios")
class UsuarioController(private val service: UsuarioService) {
    @Get
    fun listar() = service.listar()
}
```

#### 2. **HTTP Server Embutido**
```kotlin
@Controller("/hello")
class HelloController {
    @Get
    fun hello(): String = "Hello, World!"
}
```

#### 3. **Suporte a Kotlin**
```kotlin
@Singleton
data class UsuarioService(val repository: UsuarioRepository)

@Controller("/usuarios")
class UsuarioController(private val service: UsuarioService) {
    @Get
    fun listar() = service.listar()
}
```

#### 4. **Integração com GraalVM**
Compile para imagem nativa com inicialização em milissegundos:
```bash
./gradlew nativeImage
./build/native-image/app
```

#### 5. **Suporte a Reatividade**
```kotlin
@Controller("/usuarios")
class UsuarioController(private val service: UsuarioService) {
    @Get
    fun listar(): Mono<List<Usuario>> = service.listarAsync()
}
```

## Conceitos Importantes

### Bean Scopes
```kotlin
@Singleton          // Uma instância para toda aplicação
@Prototype          // Nova instância a cada injeção
@RequestScope       // Uma instância por requisição
@RefreshScope       // Pode ser recarregado
```

### Qualifiers
```kotlin
@Singleton
@Named("mysql")
class MysqlDataSource : DataSource

@Singleton
@Named("postgres")
class PostgresDataSource : DataSource

@Controller
class UsuarioController(
    @Named("mysql") private val ds: DataSource
)
```

### Interceptors
```kotlin
@Singleton
class LoggingInterceptor : HttpServerFilter {
    override fun doFilter(request: HttpRequest<*>, chain: HttpServerFilterChain): Publisher<MutableHttpResponse<*>> {
        println("Request: ${request.method} ${request.path}")
        return chain.proceed(request)
    }
}
```

### Configuration
```yaml
# application.yml
micronaut:
  application:
    name: meu-app
  server:
    port: 8080
  
datasources:
  default:
    url: jdbc:mysql://localhost:3306/db
    username: root
    password: secret
```

## Exemplos Reais

### Exemplo 1: API REST Simples
```kotlin
// Entidade
data class Usuario(
    val id: Long,
    val nome: String,
    val email: String
)

// Service
@Singleton
class UsuarioService {
    private val usuarios = mutableListOf(
        Usuario(1, "João", "joao@email.com"),
        Usuario(2, "Maria", "maria@email.com")
    )
    
    fun listar() = usuarios
    fun obter(id: Long) = usuarios.find { it.id == id }
    fun criar(usuario: Usuario) = usuario.also { usuarios.add(it) }
}

// Controller
@Controller("/api/v1/usuarios")
class UsuarioController(private val service: UsuarioService) {
    
    @Get
    fun listar() = service.listar()
    
    @Get("/{id}")
    fun obter(@PathVariable id: Long) = service.obter(id)
    
    @Post
    fun criar(@Body usuario: Usuario) = service.criar(usuario)
}
```

### Exemplo 2: Integração com Banco de Dados
```kotlin
// Repository
@Repository
interface UsuarioRepository : CrudRepository<Usuario, Long>

// Service
@Singleton
class UsuarioService(private val repository: UsuarioRepository) {
    fun listar() = repository.findAll()
    fun obter(id: Long) = repository.findById(id)
    fun criar(usuario: Usuario) = repository.save(usuario)
}

// Controller
@Controller("/api/v1/usuarios")
class UsuarioController(private val service: UsuarioService) {
    @Get
    fun listar() = service.listar()
    
    @Get("/{id}")
    fun obter(@PathVariable id: Long) = service.obter(id)
    
    @Post
    fun criar(@Body usuario: Usuario) = service.criar(usuario)
}
```

### Exemplo 3: Reatividade com Project Reactor
```kotlin
@Controller("/api/v1/usuarios")
class UsuarioController(private val service: UsuarioService) {
    
    @Get
    fun listar(): Mono<List<Usuario>> = 
        Mono.fromCallable { service.listar() }
    
    @Get("/{id}")
    fun obter(@PathVariable id: Long): Mono<Usuario> =
        Mono.fromCallable { service.obter(id) }
            .switchIfEmpty(Mono.error(HttpStatusException(HttpStatus.NOT_FOUND, "Não encontrado")))
}
```

### Exemplo 4: Imagem Nativa com GraalVM
```bash
# build.gradle.kts
plugins {
    id("io.micronaut.graalvm") version "4.0.0"
}

# Compilar para imagem nativa
./gradlew nativeImage

# Executar
./build/native-image/app
# Inicia em ~50ms com ~50MB de RAM
```

## Principais Erros

### 1. **Esquecer de anotar beans**
```kotlin
// ❌ Errado
class UsuarioService {
    fun listar() = emptyList<Usuario>()
}

// ✅ Correto
@Singleton
class UsuarioService {
    fun listar() = emptyList<Usuario>()
}
```

### 2. **Injetar sem declarar no construtor**
```kotlin
// ❌ Errado
@Controller
class UsuarioController {
    @Inject
    private lateinit var service: UsuarioService
}

// ✅ Correto
@Controller
class UsuarioController(private val service: UsuarioService)
```

### 3. **Não configurar datasource**
```yaml
# ❌ Errado
# Sem configuração de banco

# ✅ Correto
datasources:
  default:
    url: jdbc:mysql://localhost:3306/db
    username: root
    password: secret
```

### 4. **Usar reflection desnecessariamente**
Micronaut otimiza compile-time, reflection quebra isso.

## Melhores Práticas

### 1. **Use Kotlin com Micronaut**
```kotlin
// ✅ Bom
@Singleton
data class UsuarioService(val repository: UsuarioRepository)

// ❌ Evitar
@Singleton
class UsuarioService {
    @Inject
    private lateinit var repository: UsuarioRepository
}
```

### 2. **Declare beans no construtor**
```kotlin
// ✅ Bom
@Controller
class UsuarioController(
    private val service: UsuarioService,
    private val logger: Logger
)

// ❌ Evitar
@Controller
class UsuarioController {
    @Inject
    private lateinit var service: UsuarioService
}
```

### 3. **Use Reatividade para I/O**
```kotlin
// ✅ Bom
@Get
fun listar(): Mono<List<Usuario>> = 
    Mono.fromCallable { service.listar() }

// ❌ Evitar
@Get
fun listar(): List<Usuario> = service.listar() // Bloqueante
```

### 4. **Configure propriedades externamente**
```yaml
# application.yml
app:
  database:
    url: jdbc:mysql://localhost:3306/db
  cache:
    ttl: 3600
```

```kotlin
@ConfigurationProperties("app.database")
data class DatabaseConfig(
    val url: String,
    val username: String,
    val password: String
)
```

### 5. **Use GraalVM para produção**
```bash
./gradlew nativeImage
# Imagem nativa: inicialização rápida, baixa memória
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre Micronaut e Spring Boot?**
Micronaut usa compile-time DI (mais rápido), Spring usa reflection (mais flexível). Micronaut é melhor para microserviços e serverless.

### 2. **Como Micronaut consegue inicializar tão rápido?**
Usa compile-time dependency injection em vez de reflection, eliminando overhead de startup.

### 3. **O que é GraalVM e por que usar com Micronaut?**
GraalVM compila Java para imagem nativa, resultando em inicialização em milissegundos e baixo consumo de memória.

### 4. **Como fazer reatividade em Micronaut?**
Use Project Reactor (Mono, Flux) ou RxJava. Retorne tipos reativos nos controllers.

### 5. **Micronaut é bom para aplicações grandes?**
Sim, mas Spring Boot é mais maduro. Micronaut é ideal para microserviços e serverless.

## Relação com Outras Tecnologias

- **Java/Kotlin:** Linguagens suportadas
- **GraalVM:** Compila para imagem nativa
- **Project Reactor:** Reatividade
- **Docker:** Containeriza aplicações Micronaut
- **AWS Lambda:** Serverless com Micronaut
- **MySQL:** Banco de dados relacional
- **OAuth/JWT:** Autenticação

---

## Material de Estudo

### Documentação Oficial
- [Micronaut Official Documentation](https://micronaut.io/documentation.html)
- [Micronaut Guides](https://guides.micronaut.io/)

### Roadmap
- Não há roadmap específico, mas [roadmap.sh - Backend](https://roadmap.sh/backend) inclui Micronaut

### Cursos
- **Português:** [Micronaut - Udemy](https://www.udemy.com/course/micronaut/) (limitado)
- **Inglês:** [Micronaut Masterclass - Udemy](https://www.udemy.com/course/micronaut-masterclass/)

### Playlists YouTube
- **Português:** [Micronaut - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO8xWLn2KZZr6q6DmSRR_nAJ)
- **Inglês:** [Micronaut Tutorial - OCI](https://www.youtube.com/playlist?list=PLPIzp-E1msrYicmUytnmgMwvCDuVXFZzJ)

### Livros
- **"Micronaut in Action"** - Dean Wampler (em desenvolvimento)

### Artigos e Blogs
- [Micronaut Blog Official](https://micronaut.io/blog/)
- [Baeldung - Micronaut](https://www.baeldung.com/micronaut)

### GitHub Relevante
- [Micronaut Official](https://github.com/micronaut-projects)
- [Awesome Micronaut](https://github.com/rvanderwerf/awesome-micronaut)

### Repositórios Exemplo
- [Micronaut Examples](https://github.com/micronaut-projects/micronaut-examples)
- [Micronaut Guides](https://github.com/micronaut-projects/micronaut-guides)

### Projetos para Praticar
1. **API REST** com Micronaut e Kotlin
2. **Microserviço** com Micronaut e MySQL
3. **Função Lambda** com Micronaut
4. **Imagem nativa** com GraalVM
5. **API reativa** com Project Reactor

---

## Certificações

Não há certificações específicas para Micronaut. Conhecimento é validado através de:
- Experiência prática
- Contribuições open source
- Projetos em produção
