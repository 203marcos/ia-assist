# NoSQL

## O que é / Para que serve
- **NoSQL:** Bancos de dados não-relacionais, sem schema fixo, otimizados para escalabilidade horizontal e acesso rápido.
- **Tipos:** Document (MongoDB), Key-Value (Redis), Column Family (Cassandra), Graph (Neo4j), Search (Elasticsearch).
- **Objetivo:** Escalabilidade, flexibilidade de schema, performance em leitura/escrita, suportar dados não-estruturados.

## Quando usar / Quando NÃO usar
- **Usar:** Dados não-estruturados, escalabilidade horizontal, requisitos de performance extrema, schema flexível.
- **NÃO usar:** Transações ACID complexas, dados altamente relacionados (use SQL), quando consistência é crítica.

## Como funciona
- **Document (MongoDB):** Armazena documentos JSON/BSON, sem schema fixo, queries flexíveis.
- **Key-Value (Redis):** Armazena pares chave-valor em memória, muito rápido, suporta estruturas (strings, lists, sets, hashes).
- **Column Family (Cassandra):** Armazena dados em colunas, distribuído, tolerante a falhas.
- **Graph (Neo4j):** Armazena nós e relacionamentos, otimizado para queries de relacionamentos.
- **Search (Elasticsearch):** Índice invertido, busca full-text, análise de dados.

## Conceitos Importantes
- **Schema Flexível:** Documentos podem ter estruturas diferentes.
- **Replicação:** Dados replicados em múltiplos nós para alta disponibilidade.
- **Sharding:** Dados distribuídos em múltiplos servidores para escalabilidade.
- **Consistência Eventual:** Dados podem não estar imediatamente consistentes em todos os nós.
- **CAP Theorem:** Escolher entre Consistência, Disponibilidade, Tolerância a Partições.
- **Índices:** Melhoram performance de queries, mas consomem espaço.
- **Aggregation Pipeline:** Processamento de dados em múltiplos estágios (MongoDB).

## Exemplo de Código

```java
// MongoDB - Inserir documento
MongoCollection<Document> collection = database.getCollection("users");
Document user = new Document("name", "John")
    .append("email", "john@example.com")
    .append("age", 30);
collection.insertOne(user);

// MongoDB - Buscar documentos
Bson filter = Filters.eq("email", "john@example.com");
Document result = collection.find(filter).first();

// Redis - Armazenar e recuperar
Jedis jedis = new Jedis("localhost", 6379);
jedis.set("user:1:name", "John");
String name = jedis.get("user:1:name");

// Redis - Usar estruturas
jedis.lpush("tasks", "task1", "task2", "task3");
List<String> tasks = jedis.lrange("tasks", 0, -1);

// Cassandra - Inserir dados
Session session = cluster.connect("mykeyspace");
session.execute("INSERT INTO users (id, name, email) VALUES (?, ?, ?)",
    UUID.randomUUID(), "John", "john@example.com");

// Neo4j - Criar nós e relacionamentos
try (Driver driver = GraphDatabase.driver("bolt://localhost:7687")) {
    try (Session session = driver.session()) {
        session.run("CREATE (a:Person {name: 'John'}) " +
                    "CREATE (b:Person {name: 'Jane'}) " +
                    "CREATE (a)-[:KNOWS]->(b)");
    }
}
```

## Principais Erros e Melhores Práticas
- **Erro:** Usar NoSQL para dados altamente relacionados — queries complexas.
- **Prática:** Usar SQL para dados relacionados, NoSQL para dados não-estruturados.
- **Erro:** Não indexar — queries lentas.
- **Prática:** Criar índices nas colunas mais consultadas.
- **Erro:** Não considerar consistência — dados inconsistentes.
- **Prática:** Entender CAP theorem, escolher apropriadamente.
- **Erro:** Não monitorar replicação — perda de dados.
- **Prática:** Configurar replicação, testar failover.
- **Erro:** Não fazer backup — perda de dados.
- **Prática:** Backup regular, testar restauração.
- **Erro:** Usar NoSQL para tudo — overhead desnecessário.
- **Prática:** Usar ferramenta certa para o trabalho.

## Perguntas Comuns em Entrevistas
1. O que é NoSQL e quando usar?
2. Qual é a diferença entre SQL e NoSQL?
3. Quais são os tipos principais de NoSQL?
4. O que é o CAP theorem?
5. Como funciona sharding em NoSQL?
6. O que é consistência eventual?
7. Como você escolheria entre MongoDB, Redis e Cassandra?
8. Como indexar em NoSQL?
9. Como fazer transações em NoSQL?
10. Como migrar de SQL para NoSQL?

## Relação com Outras Tecnologias
- **MongoDB:** Document store, popular em aplicações Node.js e Python.
- **Redis:** Cache, sessões, filas, muito usado em microsserviços.
- **Cassandra:** Banco distribuído, usado em aplicações de alta escala.
- **Neo4j:** Graph database, usado em recomendações e análise de relacionamentos.
- **Elasticsearch:** Search engine, usado em logs e análise de dados.
- **Spring Data:** Spring suporta múltiplos NoSQL (MongoDB, Redis, Cassandra).
- **Docker:** Containerizar bancos NoSQL.
- **Kubernetes:** Orquestrar bancos NoSQL em produção.

## Material de Estudo
- https://www.mongodb.com/docs/
- https://redis.io/documentation
- https://cassandra.apache.org/doc/latest/
- https://neo4j.com/docs/
- https://www.elastic.co/guide/
- https://www.udemy.com/course/mongodb-the-complete-developers-guide/
- https://www.youtube.com/playlist?list=PLqq-6Pq4lWTa8AUUSDZTVrVqYJBNyWaZe
- https://www.baeldung.com/spring-data-mongodb
- https://github.com/mongodb/mongo-java-driver
- https://www.nosql-database.org/

## Certificações
- **MongoDB Certified Developer:** Valida conhecimento em MongoDB. Nível intermediário. Custo: ~$150 USD. Vale a pena para plenos com foco em MongoDB.
- **Redis Certified Developer:** Valida conhecimento em Redis. Nível intermediário. Custo: ~$200 USD. Vale a pena para plenos com foco em cache/sessões.
- **Cassandra Certified Associate:** Valida conhecimento em Cassandra. Nível intermediário. Custo: ~$295 USD. Vale a pena para plenos com foco em bancos distribuídos.
