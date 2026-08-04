# MySQL

## O que é?
MySQL é um sistema de gerenciamento de banco de dados relacional (RDBMS) de código aberto. Armazena dados em tabelas com linhas e colunas, usando SQL para consultas.

## Para que serve?
- Armazenar dados estruturados
- Consultar dados com SQL
- Garantir integridade de dados
- Suportar múltiplos usuários simultâneos
- Transações ACID

## Onde é usado?
- Aplicações web
- APIs REST
- Microserviços
- Plataformas SaaS
- Qualquer aplicação que precisa persistir dados

## Quando usar?
- Quando dados são estruturados
- Quando você precisa de relacionamentos entre tabelas
- Para aplicações que exigem ACID
- Quando você quer SQL padrão

## Quando NÃO usar?
- Dados não-estruturados (use MongoDB)
- Dados em tempo real muito rápido (use Redis)
- Dados em escala massiva (considere NoSQL)

## Como funciona?

### Estrutura

```
Database (Banco)
├── Table 1 (Tabela)
│   ├── Column 1 (Coluna)
│   ├── Column 2
│   └── Row 1, Row 2, ... (Linhas)
├── Table 2
└── Table 3
```

### Tipos de Dados

```sql
-- Números
INT, BIGINT, FLOAT, DECIMAL

-- Strings
VARCHAR(255), CHAR(10), TEXT

-- Data/Hora
DATE, TIME, DATETIME, TIMESTAMP

-- Booleano
BOOLEAN (0 ou 1)

-- JSON
JSON
```

### Chaves

```sql
-- Primary Key (Chave Primária)
CREATE TABLE usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100)
);

-- Foreign Key (Chave Estrangeira)
CREATE TABLE post (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT,
    titulo VARCHAR(200),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Unique Key
CREATE TABLE usuario (
    id INT PRIMARY KEY,
    email VARCHAR(100) UNIQUE
);
```

### Índices

```sql
-- Índice simples
CREATE INDEX idx_email ON usuario(email);

-- Índice composto
CREATE INDEX idx_nome_email ON usuario(nome, email);

-- Índice único
CREATE UNIQUE INDEX idx_email ON usuario(email);
```

## Conceitos Importantes

### ACID
- **Atomicity:** Transação é tudo ou nada
- **Consistency:** Dados sempre consistentes
- **Isolation:** Transações não interferem
- **Durability:** Dados persistem após commit

### Normalização
Organizar dados para evitar redundância:
- **1NF:** Sem grupos repetidos
- **2NF:** Sem dependências parciais
- **3NF:** Sem dependências transitivas

### Joins
```sql
-- INNER JOIN
SELECT u.nome, p.titulo
FROM usuario u
INNER JOIN post p ON u.id = p.usuario_id;

-- LEFT JOIN
SELECT u.nome, p.titulo
FROM usuario u
LEFT JOIN post p ON u.id = p.usuario_id;

-- RIGHT JOIN
SELECT u.nome, p.titulo
FROM usuario u
RIGHT JOIN post p ON u.id = p.usuario_id;
```

### Agregações
```sql
-- COUNT
SELECT COUNT(*) FROM usuario;

-- SUM, AVG, MIN, MAX
SELECT AVG(idade) FROM usuario;

-- GROUP BY
SELECT categoria, COUNT(*) FROM post GROUP BY categoria;

-- HAVING
SELECT categoria, COUNT(*) FROM post GROUP BY categoria HAVING COUNT(*) > 5;
```

## Exemplos Reais

### Exemplo 1: Criar Banco e Tabelas

```sql
-- Criar banco
CREATE DATABASE blog;
USE blog;

-- Criar tabela de usuários
CREATE TABLE usuario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criar tabela de posts
CREATE TABLE post (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    conteudo TEXT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Criar tabela de comentários
CREATE TABLE comentario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    usuario_id INT NOT NULL,
    conteudo TEXT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Criar índices
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_post_usuario ON post(usuario_id);
CREATE INDEX idx_comentario_post ON comentario(post_id);
```

### Exemplo 2: CRUD Básico

```sql
-- CREATE (Inserir)
INSERT INTO usuario (nome, email, senha)
VALUES ('João Silva', 'joao@email.com', 'hash_senha');

-- READ (Ler)
SELECT * FROM usuario WHERE email = 'joao@email.com';

-- UPDATE (Atualizar)
UPDATE usuario SET nome = 'João Santos' WHERE id = 1;

-- DELETE (Deletar)
DELETE FROM usuario WHERE id = 1;
```

### Exemplo 3: Queries Complexas

```sql
-- Posts com número de comentários
SELECT 
    p.id,
    p.titulo,
    u.nome as autor,
    COUNT(c.id) as num_comentarios
FROM post p
INNER JOIN usuario u ON p.usuario_id = u.id
LEFT JOIN comentario c ON p.id = c.post_id
GROUP BY p.id, p.titulo, u.nome
ORDER BY num_comentarios DESC;

-- Usuários com mais posts
SELECT 
    u.nome,
    COUNT(p.id) as num_posts
FROM usuario u
LEFT JOIN post p ON u.id = p.usuario_id
GROUP BY u.id, u.nome
HAVING COUNT(p.id) > 0
ORDER BY num_posts DESC;

-- Posts criados nos últimos 7 dias
SELECT * FROM post
WHERE data_criacao >= DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY data_criacao DESC;
```

### Exemplo 4: Transações

```sql
-- Iniciar transação
START TRANSACTION;

-- Múltiplas operações
INSERT INTO usuario (nome, email, senha)
VALUES ('Maria', 'maria@email.com', 'hash');

INSERT INTO post (usuario_id, titulo, conteudo)
VALUES (LAST_INSERT_ID(), 'Meu primeiro post', 'Conteúdo...');

-- Confirmar ou reverter
COMMIT;  -- Confirma todas as operações
-- ROLLBACK;  -- Reverte todas as operações
```

## Principais Erros

### 1. **Não usar prepared statements**
```sql
-- ❌ Errado (SQL Injection)
SELECT * FROM usuario WHERE email = '$email';

-- ✅ Correto
SELECT * FROM usuario WHERE email = ?;
```

### 2. **Não usar índices**
```sql
-- ❌ Errado
SELECT * FROM usuario WHERE email = 'joao@email.com';  -- Sem índice, lento

-- ✅ Correto
CREATE INDEX idx_email ON usuario(email);
SELECT * FROM usuario WHERE email = 'joao@email.com';  -- Rápido
```

### 3. **Não normalizar dados**
```sql
-- ❌ Errado
CREATE TABLE post (
    id INT,
    titulo VARCHAR(200),
    autor_nome VARCHAR(100),
    autor_email VARCHAR(100)
);

-- ✅ Correto
CREATE TABLE usuario (id INT, nome VARCHAR(100), email VARCHAR(100));
CREATE TABLE post (id INT, titulo VARCHAR(200), usuario_id INT);
```

### 4. **Não usar transações**
```sql
-- ❌ Errado
INSERT INTO usuario VALUES (...);
INSERT INTO post VALUES (...);
-- Se falhar no meio, dados inconsistentes

-- ✅ Correto
START TRANSACTION;
INSERT INTO usuario VALUES (...);
INSERT INTO post VALUES (...);
COMMIT;
```

### 5. **Não fazer backup**
```bash
# ✅ Bom
mysqldump -u root -p database > backup.sql
```

## Melhores Práticas

### 1. **Use prepared statements**
```java
// ✅ Bom
String sql = "SELECT * FROM usuario WHERE email = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, email);
```

### 2. **Crie índices apropriados**
```sql
-- ✅ Bom
CREATE INDEX idx_email ON usuario(email);
CREATE INDEX idx_post_usuario ON post(usuario_id);
```

### 3. **Normalize dados**
```sql
-- ✅ Bom
-- Separar em múltiplas tabelas com relacionamentos
```

### 4. **Use transações**
```sql
-- ✅ Bom
START TRANSACTION;
-- Múltiplas operações
COMMIT;
```

### 5. **Faça backups regulares**
```bash
# ✅ Bom
mysqldump -u root -p database > backup.sql
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre INNER JOIN e LEFT JOIN?**
INNER JOIN retorna apenas linhas que existem em ambas as tabelas. LEFT JOIN retorna todas as linhas da tabela esquerda.

### 2. **O que é normalização?**
Processo de organizar dados para evitar redundância e anomalias.

### 3. **Como evitar SQL Injection?**
Use prepared statements com placeholders.

### 4. **O que é índice?**
Estrutura que acelera buscas. Cria overhead em inserts/updates.

### 5. **Qual a diferença entre PRIMARY KEY e UNIQUE?**
PRIMARY KEY não pode ser NULL e é única. UNIQUE pode ser NULL.

## Relação com Outras Tecnologias

- **Java/Kotlin:** Acesso via JDBC, JPA, Hibernate
- **Spring Boot:** ORM com Spring Data JPA
- **Docker:** Containeriza MySQL
- **AWS RDS:** MySQL gerenciado na AWS
- **Backup:** mysqldump, AWS Backup

---

## Material de Estudo

### Documentação Oficial
- [MySQL Official Documentation](https://dev.mysql.com/doc/)
- [MySQL Tutorial](https://www.mysqltutorial.org/)

### Roadmap
- [roadmap.sh - SQL](https://roadmap.sh/sql)

### Cursos
- **Português:** [MySQL Completo - Udemy](https://www.udemy.com/course/mysql-completo/)
- **Inglês:** [The Complete SQL Bootcamp - Udemy](https://www.udemy.com/course/the-complete-sql-bootcamp/)

### Playlists YouTube
- **Português:** [MySQL - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO8xWLn2KZZr6q6DmSRR_nAJ)
- **Inglês:** [MySQL Tutorial - Traversy Media](https://www.youtube.com/watch?v=9OKr-7F1Be0)

### Livros
- **"SQL Performance Explained"** - Markus Winand
- **"Learning SQL"** - Alan Beaulieu

### Artigos e Blogs
- [MySQL Best Practices - Baeldung](https://www.baeldung.com/mysql)
- [MySQL Blog Official](https://mysqlserverteam.com/)

### GitHub Relevante
- [MySQL Official](https://github.com/mysql/mysql-server)

### Repositórios Exemplo
- [MySQL Examples](https://github.com/mysql/mysql-docker)

### Projetos para Praticar
1. **Blog** com usuários, posts, comentários
2. **E-commerce** com produtos, pedidos, clientes
3. **Rede social** com usuários, posts, likes
4. **Sistema de biblioteca** com livros, empréstimos
5. **Aplicação de tarefas** com categorias

---

## Certificações

### MySQL Certified Associate
- **Nível:** Associate
- **Preço:** ~$200 USD
- **Vale a pena?** Sim, especialmente para DBAs
- **Ordem ideal:** Após experiência prática com MySQL
