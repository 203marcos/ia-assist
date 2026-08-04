# Java 17+

## O que é?
Java 17+ refere-se às versões modernas da linguagem Java a partir da versão 17 (lançada em setembro de 2021). Java 17 é uma versão LTS (Long Term Support) com suporte estendido até setembro de 2026. As versões posteriores (18, 19, 20, 21, etc.) trazem melhorias incrementais, sendo Java 21 também uma versão LTS.

## Para que serve?
Java é uma linguagem de programação orientada a objetos, compilada para bytecode e executada na JVM (Java Virtual Machine). Serve para desenvolver aplicações robustas, escaláveis e multiplataforma, desde aplicações web até sistemas distribuídos.

## Onde é usado?
- Aplicações web e APIs REST
- Microserviços
- Sistemas distribuídos
- Aplicações empresariais
- Processamento de dados em larga escala
- Plataformas de streaming e fintech

## Quando usar?
- Quando você precisa de uma linguagem madura e estável
- Para projetos que exigem alta performance e escalabilidade
- Quando há necessidade de suporte de longo prazo
- Para equipes que já dominam o ecossistema Java

## Quando NÃO usar?
- Desenvolvimento frontend (use JavaScript/TypeScript)
- Scripts simples de automação (Python é mais prático)
- Aplicações que exigem tempo de inicialização muito rápido (considere Rust ou Go)

## Como funciona?

### Compilação e Execução
1. **Código-fonte (.java)** → Compilador Java → **Bytecode (.class)**
2. **Bytecode** → JVM → **Código de máquina nativa**

A JVM abstrai a plataforma, permitindo "write once, run anywhere" (WORA).

### Principais Características do Java 17+

#### 1. **Records** (Java 16+)
Simplificam a criação de classes imutáveis:
```java
public record Pessoa(String nome, int idade) {}
```

#### 2. **Sealed Classes** (Java 17)
Controlam quais classes podem estender uma classe:
```java
public sealed class Animal permits Cachorro, Gato {}
```

#### 3. **Pattern Matching** (Java 17+)
Simplificam verificações de tipo:
```java
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

#### 4. **Text Blocks** (Java 15+)
Strings multilinhas mais legíveis:
```java
String json = """
    {
        "nome": "João",
        "idade": 30
    }
    """;
```

#### 5. **Virtual Threads** (Java 19+, preview em 21)
Threads leves para programação assíncrona:
```java
Thread.ofVirtual().start(() -> {
    System.out.println("Executando em virtual thread");
});
```

## Conceitos Importantes

### JVM (Java Virtual Machine)
- Máquina virtual que executa bytecode Java
- Gerencia memória, garbage collection e otimizações
- Permite portabilidade entre plataformas

### Garbage Collection (GC)
- Gerenciamento automático de memória
- Remove objetos não referenciados
- Diferentes algoritmos: G1GC, ZGC, Shenandoah

### ClassLoader
- Carrega classes em tempo de execução
- Permite carregamento dinâmico de código

### Reflection
- Permite inspecionar e modificar classes em tempo de execução
- Usado por frameworks como Spring

### Módulos (Java 9+)
- Organizam código em módulos com dependências explícitas
- Melhoram encapsulamento e performance

## Exemplos Reais

### Exemplo 1: Classe com Record
```java
public record Usuario(String email, String senha) {
    public Usuario {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email inválido");
        }
    }
}

// Uso
Usuario user = new Usuario("marcos@email.com", "senha123");
System.out.println(user.email()); // marcos@email.com
```

### Exemplo 2: Pattern Matching
```java
Object obj = "Hello";

String resultado = switch (obj) {
    case String s -> "String: " + s;
    case Integer i -> "Integer: " + i;
    case null -> "Nulo";
    default -> "Desconhecido";
};

System.out.println(resultado); // String: Hello
```

### Exemplo 3: Virtual Threads
```java
// Criar 10.000 virtual threads (leve)
for (int i = 0; i < 10000; i++) {
    Thread.ofVirtual().start(() -> {
        // Trabalho assíncrono
        System.out.println("Task " + i);
    });
}
```

## Principais Erros

### 1. **NullPointerException**
```java
String s = null;
System.out.println(s.length()); // ❌ NPE
```
**Solução:** Use Optional ou verificações nulas.

### 2. **ClassCastException**
```java
Object obj = "texto";
Integer num = (Integer) obj; // ❌ ClassCastException
```
**Solução:** Use instanceof antes de fazer cast.

### 3. **OutOfMemoryError**
```java
List<byte[]> lista = new ArrayList<>();
while (true) {
    lista.add(new byte[1024 * 1024]); // ❌ OOM
}
```
**Solução:** Monitore uso de memória e configure heap adequadamente.

### 4. **ConcurrentModificationException**
```java
List<String> lista = new ArrayList<>(Arrays.asList("a", "b", "c"));
for (String s : lista) {
    lista.remove(s); // ❌ CME
}
```
**Solução:** Use Iterator ou streams.

## Melhores Práticas

### 1. **Use Records para DTOs**
```java
// ✅ Bom
public record PessoaDTO(String nome, int idade) {}

// ❌ Evitar
public class PessoaDTO {
    private String nome;
    private int idade;
    // getters, setters, equals, hashCode, toString...
}
```

### 2. **Prefira Immutabilidade**
```java
// ✅ Bom
public final class Usuario {
    private final String email;
    private final String nome;
    
    public Usuario(String email, String nome) {
        this.email = email;
        this.nome = nome;
    }
}
```

### 3. **Use Optional em vez de null**
```java
// ✅ Bom
Optional<Usuario> usuario = buscarUsuario(id);
usuario.ifPresent(u -> System.out.println(u.nome()));

// ❌ Evitar
Usuario usuario = buscarUsuario(id);
if (usuario != null) {
    System.out.println(usuario.nome());
}
```

### 4. **Streams para Processamento de Coleções**
```java
// ✅ Bom
List<String> nomes = usuarios.stream()
    .filter(u -> u.idade() > 18)
    .map(Usuario::nome)
    .collect(Collectors.toList());

// ❌ Evitar
List<String> nomes = new ArrayList<>();
for (Usuario u : usuarios) {
    if (u.idade() > 18) {
        nomes.add(u.nome());
    }
}
```

### 5. **Virtual Threads para I/O Bound**
```java
// ✅ Bom para I/O
Thread.ofVirtual().start(() -> {
    var resposta = fazerRequisicaoHTTP();
    processar(resposta);
});

// ❌ Evitar threads tradicionais para muitas operações I/O
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre Java 8 e Java 17?**
- Java 8: Introduziu lambdas e streams
- Java 17: Records, sealed classes, pattern matching, virtual threads (preview)

### 2. **O que é a JVM e por que é importante?**
A JVM abstrai a plataforma, permitindo que o mesmo bytecode rode em Windows, Linux, macOS, etc.

### 3. **Como funciona o Garbage Collection?**
O GC identifica objetos não referenciados e libera sua memória automaticamente. Diferentes algoritmos (G1GC, ZGC) otimizam para latência ou throughput.

### 4. **O que são Virtual Threads?**
Threads leves gerenciadas pela JVM, permitindo criar milhares delas sem overhead. Ideais para operações I/O.

### 5. **Qual a diferença entre Record e Class?**
Records são imutáveis por padrão, geram automaticamente equals, hashCode e toString. Classes são mais flexíveis.

### 6. **Como lidar com null em Java moderno?**
Use Optional, pattern matching com null checks, ou annotations como @Nullable/@NonNull.

## Relação com Outras Tecnologias

- **Spring Boot:** Framework web que roda em Java
- **Kotlin:** Linguagem que roda na JVM, mais concisa que Java
- **Quarkus/Micronaut:** Frameworks otimizados para microserviços em Java
- **Docker:** Containeriza aplicações Java
- **AWS:** Hospeda aplicações Java em EC2, ECS, EKS
- **MySQL:** Banco de dados relacional acessado via JDBC/JPA

---

## Material de Estudo

### Documentação Oficial
- [Java 17 Documentation](https://docs.oracle.com/en/java/javase/17/)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)

### Roadmap
- [roadmap.sh - Java](https://roadmap.sh/java)

### Cursos
- **Português:** [Java Completo 2024 - Udemy](https://www.udemy.com/course/java-completo/)
- **Inglês:** [The Complete Java Development Bootcamp - Udemy](https://www.udemy.com/course/the-complete-java-development-bootcamp/)

### Playlists YouTube
- **Português:** [Curso Java - Gustavo Guanabara](https://www.youtube.com/playlist?list=PLHz_AreHm4dkqe2aR0tQpdeP3ropjv4CS)
- **Inglês:** [Java Programming - Bro Code](https://www.youtube.com/playlist?list=PLZPZq0r_RZOMhCAyywfnYLlrX-hZJJqX7)

### Livros
- **"Effective Java" (3ª edição)** - Joshua Bloch
- **"Clean Code"** - Robert C. Martin
- **"Java Concurrency in Practice"** - Brian Goetz

### Artigos e Blogs
- [Oracle Java Blog](https://blogs.oracle.com/java/)
- [Baeldung - Java Tutorials](https://www.baeldung.com/)
- [DZone Java](https://dzone.com/java-jvm-a-z)

### GitHub Relevante
- [OpenJDK](https://github.com/openjdk/jdk)
- [Awesome Java](https://github.com/akullpp/awesome-java)

### Repositórios Exemplo
- [Spring Boot Examples](https://github.com/spring-projects/spring-boot/tree/main/spring-boot-samples)
- [Quarkus Examples](https://github.com/quarkusio/quarkus-quickstarts)

### Projetos para Praticar
1. **API REST simples** com Spring Boot
2. **Aplicação de TODO** com persistência em banco
3. **Sistema de chat** com WebSockets
4. **Processador de arquivos** com streams
5. **Microserviço** com Quarkus ou Micronaut

---

## Certificações

### Oracle Certified Associate Java Programmer (OCAJP)
- **Nível:** Associate (Iniciante)
- **Preço:** ~$245 USD
- **Vale a pena?** Sim, especialmente para iniciantes. Valida conhecimentos fundamentais.
- **Ordem ideal:** Primeira certificação Java

### Oracle Certified Professional Java Programmer (OCPJP)
- **Nível:** Professional (Intermediário/Avançado)
- **Preço:** ~$245 USD
- **Vale a pena?** Sim, para consolidar conhecimentos avançados.
- **Ordem ideal:** Após OCAJP ou com experiência prática

### Observação
Certificações Oracle não são obrigatórias no mercado, mas agregam valor ao currículo. Experiência prática é mais valorizada.
