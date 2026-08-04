# Spring Ecosystem

## O que é / Para que serve
- **Spring Framework:** Framework Java para construir aplicações enterprise com injeção de dependência, AOP e gerenciamento de transações.
- **Spring Boot:** Abstração que simplifica criação de aplicações Spring com configuração automática e embedded servers.
- **Spring Data:** Abstração para acesso a dados (SQL, NoSQL, search engines) com repositórios genéricos.
- **Spring Cloud:** Ferramentas para construir sistemas distribuídos (service discovery, config server, circuit breaker, load balancing).
- **Spring Security:** Framework para autenticação, autorização e proteção contra ataques comuns (CSRF, XSS, SQL injection).

## Quando usar / Quando NÃO usar
- **Usar:** Aplicações enterprise, microsserviços, APIs REST, sistemas distribuídos, quando precisa de maturidade e comunidade.
- **NÃO usar:** Projetos muito simples (overhead), aplicações que exigem performance extrema (overhead de reflexão), quando precisa de controle total de baixo nível.

## Como funciona
- **Spring Framework:** Usa injeção de dependência (IoC container) para gerenciar ciclo de vida de beans, AOP para cross-cutting concerns.
- **Spring Boot:** Auto-configuration baseada em classpath, starter dependencies, embedded Tomcat/Netty, application.properties/yml.
- **Spring Data:** Gera implementações de repositórios em tempo de execução, suporta queries derivadas de nomes de métodos.
- **Spring Cloud:** Integra Eureka (service discovery), Config Server, Hystrix (circuit breaker), Ribbon (load balancing), Zuul (API gateway).
- **Spring Security:** Filtros de servlet, authentication providers, authorization managers, token-based (JWT) ou session-based.

## Conceitos Importantes
- **IoC Container:** Gerencia criação, configuração e ciclo de vida de beans.
- **Dependency Injection:** Injeção de dependências via constructor, setter ou field.
- **AOP (Aspect-Oriented Programming):** Separação de cross-cutting concerns (logging, transações, segurança).
- **Starter Dependencies:** Pré-configurações de bibliotecas comuns (spring-boot-starter-web, spring-boot-starter-data-jpa).
- **Auto-configuration:** Configuração automática baseada em classpath e propriedades.
- **Profiles:** Diferentes configurações por ambiente (dev, test, prod).
- **Actuator:** Endpoints para monitoramento e gerenciamento da aplicação.

## Exemplo de Código

```java
// Spring Boot Application
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// Spring Data Repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmailContaining(String email);
}

// Spring Security Configuration
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/public/**").permitAll()
            .anyRequest().authenticated()
            .and().formLogin();
    }
}

// Spring Cloud Service Discovery (Eureka Client)
@SpringBootApplication
@EnableEurekaClient
public class MicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroserviceApplication.class, args);
    }
}
```

## Principais Erros e Melhores Práticas
- **Erro:** Usar field injection (@Autowired em fields) — dificulta testes.
- **Prática:** Usar constructor injection para dependências obrigatórias.
- **Erro:** Configurar tudo em application.properties — difícil de manter.
- **Prática:** Usar profiles (application-dev.yml, application-prod.yml) e variáveis de ambiente.
- **Erro:** Não usar @Transactional corretamente — pode causar problemas de consistência.
- **Prática:** Aplicar @Transactional em métodos de serviço, não em controllers.
- **Erro:** Expor entidades JPA diretamente em APIs — acoplamento e problemas de serialização.
- **Prática:** Usar DTOs (Data Transfer Objects) para separar modelo interno de API.

## Perguntas Comuns em Entrevistas
1. Qual é a diferença entre Spring Framework e Spring Boot?
2. Como funciona a injeção de dependência no Spring?
3. O que é AOP e como é usado no Spring?
4. Como configurar autenticação e autorização com Spring Security?
5. Qual é a diferença entre @Component, @Service, @Repository e @Controller?
6. Como usar Spring Data para acessar bancos de dados?
7. O que é um starter dependency e como funciona?
8. Como implementar circuit breaker com Spring Cloud?
9. Como usar profiles para diferentes ambientes?
10. Como testar aplicações Spring com JUnit e Mockito?

## Relação com Outras Tecnologias
- **Java:** Linguagem base do Spring.
- **REST APIs:** Spring Boot é usado para criar APIs REST.
- **Docker:** Aplicações Spring são containerizadas com Docker.
- **Kubernetes:** Microsserviços Spring são orquestrados com Kubernetes.
- **Mensageria:** Spring integra com RabbitMQ, Kafka, ActiveMQ.
- **Bancos de Dados:** Spring Data integra com SQL (JPA/Hibernate) e NoSQL (MongoDB, Redis).
- **Cloud:** Spring Cloud integra com AWS, Azure, GCP.
- **Testes:** JUnit, Mockito, TestContainers para testar aplicações Spring.

## Material de Estudo
- https://spring.io/projects/spring-framework
- https://spring.io/projects/spring-boot
- https://spring.io/projects/spring-data
- https://spring.io/projects/spring-cloud
- https://spring.io/projects/spring-security
- https://www.udemy.com/course/spring-boot-microservices-complete-course/
- https://www.youtube.com/playlist?list=PLqq-6Pq4lWTa8AUUSDZTVrVqYJBNyWaZe
- https://www.baeldung.com/spring-tutorial
- https://github.com/spring-projects/spring-boot/tree/main/spring-boot-samples
- https://spring.io/guides

## Certificações
- **Spring Professional Certification:** Valida conhecimento em Spring Framework e Spring Boot. Nível intermediário. Custo: ~$200 USD. Vale a pena para plenos e sêniors.
