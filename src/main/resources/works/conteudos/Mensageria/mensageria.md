# Mensageria

## O que é / Para que serve
- **Mensageria:** Sistema de comunicação assíncrona entre aplicações, desacoplando produtores e consumidores de mensagens.
- **Objetivo:** Escalabilidade, resiliência, desacoplamento, processamento assíncrono, garantia de entrega.

## Quando usar / Quando NÃO usar
- **Usar:** Comunicação assíncrona, processamento em background, sistemas distribuídos, quando produtor e consumidor não precisam estar sincronizados.
- **NÃO usar:** Comunicação síncrona (use REST/gRPC), quando latência é crítica, operações que exigem resposta imediata.

## Como funciona
- **Produtor:** Envia mensagens para um broker (RabbitMQ, Kafka, etc.).
- **Broker:** Armazena e roteia mensagens para consumidores.
- **Consumidor:** Recebe e processa mensagens.
- **Fila:** Armazena mensagens até serem consumidas (FIFO).
- **Tópico:** Padrão pub/sub, múltiplos consumidores recebem a mesma mensagem.
- **Garantia de Entrega:** At-most-once, at-least-once, exactly-once.

## Conceitos Importantes
- **RabbitMQ:** Message broker baseado em AMQP, fila tradicional, garantia de entrega.
- **Kafka:** Event streaming platform, tópicos, partições, retenção de eventos, alta throughput.
- **ActiveMQ:** Message broker Java, suporta múltiplos protocolos.
- **AWS SQS/SNS:** Serviços gerenciados na AWS (fila e pub/sub).
- **Azure Service Bus:** Serviço gerenciado na Azure.
- **Dead Letter Queue (DLQ):** Fila para mensagens que falharam no processamento.
- **Acknowledgment:** Confirmação de processamento de mensagem.
- **Retry Policy:** Reprocessar mensagens que falharam.

## Exemplo de Código

```java
// RabbitMQ - Produtor
@Service
public class OrderProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void sendOrder(Order order) {
        rabbitTemplate.convertAndSend("order-exchange", "order.created", order);
    }
}

// RabbitMQ - Consumidor
@Service
public class OrderConsumer {
    @RabbitListener(queues = "order-queue")
    public void processOrder(Order order) {
        System.out.println("Processing order: " + order.getId());
        // Processar pedido
    }
}

// Kafka - Produtor
@Service
public class EventProducer {
    @Autowired
    private KafkaTemplate<String, Event> kafkaTemplate;
    
    public void sendEvent(Event event) {
        kafkaTemplate.send("events-topic", event.getId(), event);
    }
}

// Kafka - Consumidor
@Service
public class EventConsumer {
    @KafkaListener(topics = "events-topic", groupId = "my-group")
    public void consumeEvent(Event event) {
        System.out.println("Received event: " + event.getId());
        // Processar evento
    }
}

// Spring Cloud Stream (abstração)
@Service
public class MessageService {
    @Autowired
    private Source source;
    
    public void sendMessage(String message) {
        source.output().send(MessageBuilder.withPayload(message).build());
    }
}
```

## Principais Erros e Melhores Práticas
- **Erro:** Não implementar retry/DLQ — mensagens perdidas.
- **Prática:** Configurar retry policy e dead letter queue.
- **Erro:** Não idempotência — reprocessar mensagem causa duplicação.
- **Prática:** Implementar idempotência (usar ID único, verificar duplicatas).
- **Erro:** Não monitorar filas — backlog invisível.
- **Prática:** Monitorar tamanho de fila, latência, taxa de erro.
- **Erro:** Usar RabbitMQ para event streaming — não é seu caso de uso.
- **Prática:** Usar Kafka para event streaming, RabbitMQ para filas tradicionais.
- **Erro:** Não serializar corretamente — problemas de desserialização.
- **Prática:** Usar JSON com versionamento, considerar Avro ou Protobuf.

## Perguntas Comuns em Entrevistas
1. O que é mensageria e quando usar?
2. Qual é a diferença entre RabbitMQ e Kafka?
3. O que é uma fila e um tópico?
4. Como garantir entrega de mensagens?
5. O que é idempotência em mensageria?
6. Como implementar retry e dead letter queue?
7. O que é consumer group em Kafka?
8. Como monitorar filas de mensagens?
9. Qual é a diferença entre at-most-once e exactly-once?
10. Como escolher entre RabbitMQ, Kafka e SQS?

## Relação com Outras Tecnologias
- **Spring Cloud Stream:** Abstração para mensageria em Spring.
- **Microsserviços:** Comunicação assíncrona entre serviços.
- **Docker:** Containerizar RabbitMQ, Kafka.
- **Kubernetes:** Orquestrar brokers de mensageria.
- **Cloud:** AWS SQS/SNS, Azure Service Bus, GCP Pub/Sub.
- **Observabilidade:** Monitorar filas com Prometheus, ELK.
- **Arquitetura:** Event-driven architecture, CQRS, Event Sourcing.

## Material de Estudo
- https://www.rabbitmq.com/documentation.html
- https://kafka.apache.org/documentation/
- https://spring.io/projects/spring-cloud-stream
- https://www.udemy.com/course/apache-kafka-series-kafka-ecosystem/
- https://www.youtube.com/playlist?list=PLqq-6Pq4lWTa8AUUSDZTVrVqYJBNyWaZe
- https://www.baeldung.com/spring-cloud-stream
- https://github.com/spring-cloud/spring-cloud-stream-samples
- https://www.nginx.com/blog/what-is-message-queuing/

## Certificações
- **Confluent Certified Developer for Apache Kafka:** Valida conhecimento em Kafka. Nível intermediário. Custo: ~$150 USD. Vale a pena para plenos com foco em event streaming.
- **RabbitMQ Certified Developer:** Valida conhecimento em RabbitMQ. Nível intermediário. Custo: ~$200 USD. Vale a pena para plenos com foco em mensageria.
