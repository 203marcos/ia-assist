# Quarkus

## O que é?
Quarkus é um framework Java nativo para Kubernetes, desenvolvido pela Red Hat. Otimizado para containers e serverless, oferece inicialização rápida, baixo consumo de memória e suporte a GraalVM para compilação nativa.

## Para que serve?
Quarkus serve para:
- Desenvolver microserviços otimizados para containers
- Criar aplicações serverless
- Construir APIs REST de alta performance
- Integrar com Kubernetes e cloud providers

## Onde é usado?
- Microserviços em Kubernetes
- Aplicações serverless (AWS Lambda, Google Cloud Functions)
- APIs de baixa latência
- Empresas que usam containers e cloud nativo

## Quando usar?
- Quando você trabalha com Kubernetes
- Para microserviços containerizados
- Em ambientes serverless
- Quando memória e startup são críticos

## Quando NÃO usar?
- Aplicações monolíticas grandes (Spring Boot é melhor)
- Quando você precisa de muitos frameworks prontos
- Projetos com equipe inexperiente em Quarkus

## Como funciona?

### Arquitetura
```
Código Quarkus → Compilação (Build Time) → Bytecode otimizado → JVM/GraalVM
```

Quarkus processa dependências em **build time** em vez de runtime, resultando em:
- Inicialização em < 1s
- Consumo de memória reduzido (50-100MB)
- Melhor performance em containers

### Principais Características

#### 1. **Build Time Processing**
```java
@Path("/hello")
public class HelloResource {
    @GET
    public String hello() {
        return "Hello, World!";
    }
}
```

#### 2. **Suporte a Reatividade**
```java
@Path("/usuarios")
public class UsuarioResource {
    @GET
    public Uni<List<Usuario>> listar() {
        return usuarioService.listarAsync();
    }
}
```

#### 3. **Integração com GraalVM**
```bash
./mvnw clean package -Pnative
./target/app-1.0-runner
# Inicia em ~50ms com ~50MB de RAM
```

#### 4. **Dev Mode com Hot Reload**
```bash
./mvnw quarkus:dev
# Muda código, salva, e vê mudanças instantaneamente
```

#### 5. **Extensões Prontas**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-mysql</artifactId>
</dependency>
```

## Conceitos Importantes

### Extensões
Quarkus usa extensões para adicionar funcionalidades:
```bash
./mvnw quarkus:list-extensions
./mvnw quarkus:add-extension -Dextensions="rest,hibernate-orm,jdbc-mysql"
```

### Panache (ORM Simplificado)
```java
@Entity
public class Usuario extends PanacheEntity {
    public String nome;
    public String email;
    
    public static List<Usuario> findByNome(String nome) {
        return find("nome", nome).list();
    }
}

// Uso
List<Usuario> usuarios = Usuario.listAll();
Usuario usuario = Usuario.findById(1L);
usuario.delete();
```

### Reatividade com Mutiny
```java
@Path("/usuarios")
public class UsuarioResource {
    @GET
    public Uni<List<Usuario>> listar() {
        return Usuario.listAll();
    }
    
    @GET
    @Path("/{id}")
    public Uni<Usuario> obter(@PathParam Long id) {
        return Usuario.findById(id)
            .onItem().ifNull().failWith(() -> 
                new WebApplicationException(404)
            );
    }
}
```

### Configuration
```properties
# application.properties
quarkus.application.name=meu-app
quarkus.http.port=8080

quarkus.datasource.db-kind=mysql
quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3306/db
quarkus.datasource.username=root
quarkus.datasource.password=secret

quarkus.hibernate-orm.database.generation=update
```

## Exemplos Reais

### Exemplo 1: API REST Simples
```java
// Entidade
@Entity
public class Usuario extends PanacheEntity {
    public String nome;
    public String email;
}

// Resource (Controller)
@Path("/api/v1/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    
    @GET
    public List<Usuario> listar() {
        return Usuario.listAll();
    }
    
    @GET
    @Path("/{id}")
    public Usuario obter(@PathParam Long id) {
        return Usuario.findById(id);
    }
    
    @POST
    @Status(201)
    public Usuario criar(Usuario usuario) {
        usuario.persist();
        return usuario;
    }
    
    @PUT
    @Path("/{id}")
    public Usuario atualizar(@PathParam Long id, Usuario usuario) {
        Usuario existing = Usuario.findById(id);
        existing.nome = usuario.nome;
        existing.email = usuario.email;
        existing.persist();
        return existing;
    }
    
    @DELETE
    @Path("/{id}")
    @Status(204)
    public void deletar(@PathParam Long id) {
        Usuario.deleteById(id);
    }
}
```

### Exemplo 2: Reatividade com Mutiny
```java
@Path("/api/v1/usuarios")
public class UsuarioResource {
    
    @GET
    public Uni<List<Usuario>> listar() {
        return Usuario.listAll();
    }
    
    @GET
    @Path("/{id}")
    public Uni<Usuario> obter(@PathParam Long id) {
        return Usuario.findById(id)
            .onItem().ifNull().failWith(() -> 
                new WebApplicationException("Não encontrado", 404)
            );
    }
    
    @POST
    public Uni<Usuario> criar(Usuario usuario) {
        return usuario.persistAndFlush()
            .replaceWith(usuario);
    }
}
```

### Exemplo 3: Integração com REST Client
```java
@RegisterRestClient
@Path("/api/external")
public interface ExternalService {
    @GET
    @Path("/data")
    Uni<String> getData();
}

@Path("/api/v1/data")
public class DataResource {
    @RestClient
    ExternalService externalService;
    
    @GET
    public Uni<String> getData() {
        return externalService.getData();
    }
}
```

### Exemplo 4: Imagem Nativa
```bash
# Compilar para nativo
./mvnw clean package -Pnative

# Executar
./target/app-1.0-runner

# Resultado: inicialização em ~50ms, ~50MB RAM
```

## Principais Erros

### 1. **Esquecer de anotar entidades**
```java
// ❌ Errado
public class Usuario {
    public String nome;
}

// ✅ Correto
@Entity
public class Usuario extends PanacheEntity {
    public String nome;
}
```

### 2. **Usar blocking em contexto reativo**
```java
// ❌ Errado
@GET
public Uni<String> getData() {
    String data = fazerRequisicaoSincrona(); // Bloqueia
    return Uni.createFrom().item(data);
}

// ✅ Correto
@GET
public Uni<String> getData() {
    return fazerRequisicaoAssincrona();
}
```

### 3. **Não configurar datasource**
```properties
# ❌ Errado
# Sem configuração

# ✅ Correto
quarkus.datasource.db-kind=mysql
quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3306/db
quarkus.datasource.username=root
quarkus.datasource.password=secret
```

### 4. **Usar reflection desnecessariamente**
Quarkus otimiza build-time, reflection quebra isso.

## Melhores Práticas

### 1. **Use Panache para ORM**
```java
// ✅ Bom
@Entity
public class Usuario extends PanacheEntity {
    public String nome;
    
    public static List<Usuario> findByNome(String nome) {
        return find("nome", nome).list();
    }
}

// ❌ Evitar
@Entity
public class Usuario {
    @Id
    private Long id;
    private String nome;
    // getters, setters, etc
}
```

### 2. **Use Reatividade para I/O**
```java
// ✅ Bom
@GET
public Uni<List<Usuario>> listar() {
    return Usuario.listAll();
}

// ❌ Evitar
@GET
public List<Usuario> listar() {
    return Usuario.listAll(); // Bloqueante
}
```

### 3. **Configure externamente**
```properties
# application.properties
app.database.url=jdbc:mysql://localhost:3306/db
app.cache.ttl=3600
```

```java
@ConfigProperties(prefix = "app.database")
public class DatabaseConfig {
    public String url;
    public String username;
    public String password;
}
```

### 4. **Use Dev Mode para desenvolvimento**
```bash
./mvnw quarkus:dev
# Hot reload automático
```

### 5. **Compile para nativo em produção**
```bash
./mvnw clean package -Pnative
# Imagem nativa: inicialização rápida, baixa memória
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre Quarkus e Spring Boot?**
Quarkus processa dependências em build-time (mais rápido), Spring em runtime (mais flexível). Quarkus é melhor para containers e serverless.

### 2. **Como Quarkus consegue inicializar tão rápido?**
Processa dependências e configurações em build-time, não em runtime. Elimina overhead de reflection.

### 3. **O que é Panache?**
Simplificação de Hibernate ORM para Quarkus. Oferece métodos estáticos para CRUD sem boilerplate.

### 4. **Como fazer reatividade em Quarkus?**
Use Mutiny (Uni, Multi) ou Reactive Streams. Retorne tipos reativos nos resources.

### 5. **Quarkus é bom para aplicações grandes?**
Sim, especialmente em Kubernetes. Spring Boot é mais maduro, mas Quarkus é ideal para cloud nativo.

## Relação com Outras Tecnologias

- **Java/Kotlin:** Linguagens suportadas
- **GraalVM:** Compila para imagem nativa
- **Mutiny:** Reatividade
- **Kubernetes:** Orquestração de containers
- **Docker:** Containeriza aplicações Quarkus
- **AWS Lambda:** Serverless com Quarkus
- **MySQL:** Banco de dados relacional
- **OAuth/JWT:** Autenticação

---

## Material de Estudo

### Documentação Oficial
- [Quarkus Official Documentation](https://quarkus.io/guides/)
- [Quarkus Getting Started](https://quarkus.io/get-started/)

### Roadmap
- Não há roadmap específico, mas [roadmap.sh - Backend](https://roadmap.sh/backend) inclui Quarkus

### Cursos
- **Português:** [Quarkus - Udemy](https://www.udemy.com/course/quarkus/) (limitado)
- **Inglês:** [Quarkus Masterclass - Udemy](https://www.udemy.com/course/quarkus-masterclass/)

### Playlists YouTube
- **Português:** [Quarkus - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO8xWLn2KZZr6q6DmSRR_nAJ)
- **Inglês:** [Quarkus Tutorial - Red Hat](https://www.youtube.com/playlist?list=PLf3vm0UK6HKu3-Oy-yt3-5LBWnqIFVkKt)

### Livros
- **"Quarkus in Action"** - Antonio Goncalves (em desenvolvimento)

### Artigos e Blogs
- [Quarkus Blog Official](https://quarkus.io/blog/)
- [Baeldung - Quarkus](https://www.baeldung.com/quarkus)

### GitHub Relevante
- [Quarkus Official](https://github.com/quarkusio/quarkus)
- [Awesome Quarkus](https://github.com/quarkusio/awesome-quarkus)

### Repositórios Exemplo
- [Quarkus Quickstarts](https://github.com/quarkusio/quarkus-quickstarts)
- [Quarkus Examples](https://github.com/quarkusio/quarkus/tree/main/integration-tests)

### Projetos para Praticar
1. **API REST** com Quarkus e Panache
2. **Microserviço** com Quarkus e MySQL
3. **Função Lambda** com Quarkus
4. **Imagem nativa** com GraalVM
5. **API reativa** com Mutiny

---

## Certificações

Não há certificações específicas para Quarkus. Conhecimento é validado através de:
- Experiência prática
- Contribuições open source
- Projetos em produção
