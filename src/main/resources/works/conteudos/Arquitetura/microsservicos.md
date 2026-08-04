# Microsserviços

## O que é / Para que serve
- **Microsserviços:** Arquitetura que divide uma aplicação em pequenos serviços independentes, cada um com responsabilidade única, comunicando-se via APIs ou mensageria.
- **Objetivo:** Escalabilidade independente, deploy autônomo, resiliência, facilitar manutenção e evolução de grandes sistemas.

## Quando usar / Quando NÃO usar
- **Usar:** Aplicações grandes e complexas, múltiplos times, requisitos de escalabilidade diferenciados por funcionalidade, evolução contínua.
- **NÃO usar:** Projetos pequenos (overhead de complexidade), equipes pequenas (falta de recursos), quando latência é crítica (comunicação entre serviços).

## Como funciona
- **Decomposição:** Dividir domínio em bounded contexts (DDD), cada um é um microsserviço independente.
- **Comunicação:** Síncrona (REST, gRPC) ou assíncrona (mensageria, event streaming).
- **Dados:** Cada serviço tem seu próprio banco de dados (database per service pattern).
- **Deploy:** Cada serviço é deployado independentemente, geralmente em containers (Docker + Kubernetes).
- **Descoberta:** Service discovery (Eureka, Consul) para localizar serviços dinamicamente.
- **Resiliência:** Circuit breaker, retry, timeout, bulkhead para lidar com falhas.

## Conceitos Importantes
- **Bounded Context (DDD):** Limite claro de responsabilidade de cada serviço.
- **API Gateway:** Ponto de entrada único para clientes, roteia requisições para serviços.
- **Service Discovery:** Registro e descoberta dinâmica de serviços.
- **Circuit Breaker:** Padrão para evitar cascata de falhas (open, half-open, closed).
- **Saga Pattern:** Transações distribuídas entre múltiplos serviços.
- **Event Sourcing:** Armazenar mudanças de estado como eventos imutáveis.
- **CQRS:** Separar modelos de leitura e escrita.
- **Observabilidade:** Logging, tracing distribuído, métricas.

## Exemplo de Código

```java
// Serviço de Usuários (User Service)
@RestController
@RequestMapping("/api/users")
public class UserController {
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }
}

// Serviço de Pedidos (Order Service) - chamando User Service
@Service
public class OrderService {
    @Autowired
    private RestTemplate restTemplate;
    
    public Order createOrder(Order order) {
        // Chamar User Service
        User user = restTemplate.getForObject(
            "http://user-service/api/users/" + order.getUserId(), 
            User.class
        );
        // Validar e criar pedido
        return orderRepository.save(order);
    }
}

// Circuit Breaker com Resilience4j
@Service
public class PaymentService {
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallback")
    public Payment processPayment(Payment payment) {
        return paymentGateway.charge(payment);
    }
    
    public Payment fallback(Payment payment, Exception e) {
        return new Payment(payment.getId(), "PENDING", "Service unavailable");
    }
}
```

## Principais Erros e Melhores Práticas
- **Erro:** Criar microsserviços muito pequenos ou muito grandes — difícil de manter.
- **Prática:** Usar DDD para definir bounded contexts apropriados.
- **Erro:** Compartilhar banco de dados entre serviços — acoplamento.
- **Prática:** Database per service, usar eventos para sincronizar dados.
- **Erro:** Não implementar circuit breaker — cascata de falhas.
- **Prática:** Usar Resilience4j, Hystrix ou similar para resiliência.
- **Erro:** Falta de observabilidade — difícil debugar problemas.
- **Prática:** Implementar logging centralizado, tracing distribuído (Jaeger, Zipkin), métricas (Prometheus).
- **Erro:** Comunicação síncrona excessiva — latência e acoplamento.
- **Prática:** Usar mensageria assíncrona quando possível.

## Perguntas Comuns em Entrevistas
1. O que é arquitetura de microsserviços e quais são seus benefícios?
2. Qual é a diferença entre microsserviços e monolito?
3. Como você definiria os limites de um microsserviço?
4. Como microsserviços se comunicam entre si?
5. O que é o padrão Saga e quando usar?
6. Como implementar circuit breaker em microsserviços?
7. O que é service discovery e por que é importante?
8. Como lidar com transações distribuídas em microsserviços?
9. Como você monitoraria e debugaria microsserviços?
10. Quais são os desafios de migrar de monolito para microsserviços?

## Relação com Outras Tecnologias
- **Spring Cloud:** Framework para implementar padrões de microsserviços.
- **Docker:** Containerizar cada microsserviço.
- **Kubernetes:** Orquestrar e gerenciar microsserviços em containers.
- **Mensageria:** RabbitMQ, Kafka para comunicação assíncrona.
- **API Gateway:** Kong, AWS API Gateway, Spring Cloud Gateway.
- **Service Discovery:** Eureka, Consul, Kubernetes DNS.
- **Observabilidade:** ELK Stack, Prometheus, Jaeger, Zipkin.
- **Bancos de Dados:** Cada serviço com seu próprio banco (SQL ou NoSQL).

## Material de Estudo
- https://microservices.io/
- https://martinfowler.com/microservices/
- https://www.udemy.com/course/microservices-architecture/
- https://www.youtube.com/playlist?list=PLqq-6Pq4lWTa8AUUSDZTVrVqYJBNyWaZe
- https://www.baeldung.com/spring-cloud-series
- https://github.com/spring-cloud/spring-cloud-netflix
- https://12factor.net/
- https://www.nginx.com/blog/introduction-to-microservices/

## Certificações
- **AWS Certified Solutions Architect:** Valida conhecimento em arquitetura de microsserviços na AWS. Nível intermediário. Custo: ~$150 USD. Vale a pena para plenos.
- **Kubernetes Application Developer (CKAD):** Valida conhecimento em deploy de aplicações em Kubernetes. Nível intermediário. Custo: ~$395 USD. Vale a pena para plenos com foco em DevOps.
