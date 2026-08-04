# Docker

## O que é?
Docker é uma plataforma de containerização que permite empacotar aplicações com todas as suas dependências em um container isolado. Containers são leves, portáveis e garantem que a aplicação rode da mesma forma em qualquer ambiente.

## Para que serve?
- Empacotar aplicações com dependências
- Garantir consistência entre ambientes (dev, test, prod)
- Facilitar deployment e escalabilidade
- Isolar aplicações
- Simplificar CI/CD

## Onde é usado?
- Microserviços
- Aplicações em nuvem
- Kubernetes
- CI/CD pipelines
- Desenvolvimento local

## Quando usar?
- Sempre que você quer garantir consistência entre ambientes
- Para microserviços
- Em pipelines CI/CD
- Para facilitar onboarding de desenvolvedores

## Quando NÃO usar?
- Aplicações que precisam de acesso direto ao hardware
- Quando performance é crítica (overhead mínimo, mas existe)

## Como funciona?

### Conceitos Principais

#### Image (Imagem)
Template read-only que contém tudo para rodar uma aplicação:
- Sistema operacional base
- Dependências
- Código da aplicação
- Configurações

#### Container (Container)
Instância em execução de uma imagem. Isolado, com seu próprio filesystem, rede e processos.

#### Dockerfile
Arquivo que define como construir uma imagem.

#### Registry
Repositório de imagens (Docker Hub, ECR, etc).

### Dockerfile

```dockerfile
# Imagem base
FROM openjdk:17-jdk-slim

# Metadados
LABEL maintainer="seu-email@exemplo.com"

# Variáveis de ambiente
ENV APP_HOME=/app
ENV JAVA_OPTS="-Xmx512m"

# Criar diretório
WORKDIR $APP_HOME

# Copiar arquivo JAR
COPY target/app-1.0.jar app.jar

# Expor porta
EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=jdbc:mysql://db:3306/mydb
      - DATABASE_USER=root
      - DATABASE_PASSWORD=secret
    depends_on:
      - db
    networks:
      - app-network

  db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=secret
      - MYSQL_DATABASE=mydb
    ports:
      - "3306:3306"
    volumes:
      - db-data:/var/lib/mysql
    networks:
      - app-network

volumes:
  db-data:

networks:
  app-network:
```

## Conceitos Importantes

### Layers (Camadas)
Cada instrução no Dockerfile cria uma camada. Camadas são cacheadas para builds mais rápidos.

```dockerfile
FROM openjdk:17          # Camada 1
WORKDIR /app             # Camada 2
COPY . .                 # Camada 3
RUN mvn clean package    # Camada 4
ENTRYPOINT ["java", "-jar", "app.jar"]  # Camada 5
```

### Volumes
Persistem dados fora do container:
```bash
docker run -v /host/path:/container/path image
docker run -v named-volume:/container/path image
```

### Networks
Conectam containers:
```bash
docker network create app-network
docker run --network app-network image
```

### Environment Variables
Configuração via variáveis:
```dockerfile
ENV DATABASE_URL=jdbc:mysql://localhost:3306/db
ENV LOG_LEVEL=INFO
```

## Exemplos Reais

### Exemplo 1: Dockerfile para Aplicação Java

```dockerfile
# Build stage
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/app-1.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Exemplo 2: Docker Compose com App e Banco

```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: meu-app
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/mydb
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: secret
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      - mysql
    networks:
      - app-network
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    container_name: mysql-db
    environment:
      MYSQL_ROOT_PASSWORD: secret
      MYSQL_DATABASE: mydb
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - app-network
    restart: unless-stopped

volumes:
  mysql-data:

networks:
  app-network:
    driver: bridge
```

### Exemplo 3: Multi-stage Build

```dockerfile
# Stage 1: Build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/app-1.0.jar app.jar

# Criar usuário não-root
RUN useradd -m appuser
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Exemplo 4: Comandos Docker Comuns

```bash
# Build imagem
docker build -t meu-app:1.0 .

# Rodar container
docker run -d -p 8080:8080 --name meu-app meu-app:1.0

# Ver logs
docker logs -f meu-app

# Entrar no container
docker exec -it meu-app /bin/bash

# Parar container
docker stop meu-app

# Remover container
docker rm meu-app

# Docker Compose
docker-compose up -d
docker-compose down
docker-compose logs -f
```

## Principais Erros

### 1. **Imagem muito grande**
```dockerfile
# ❌ Errado
FROM ubuntu:20.04
RUN apt-get update && apt-get install -y openjdk-17-jdk maven

# ✅ Correto
FROM maven:3.8.1-openjdk-17
```

### 2. **Não usar .dockerignore**
```
# .dockerignore
node_modules
.git
.env
target
```

### 3. **Rodar como root**
```dockerfile
# ❌ Errado
ENTRYPOINT ["java", "-jar", "app.jar"]

# ✅ Correto
RUN useradd -m appuser
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 4. **Não usar health checks**
```dockerfile
# ✅ Bom
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1
```

### 5. **Não limpar cache**
```dockerfile
# ❌ Errado
RUN apt-get update
RUN apt-get install -y package1
RUN apt-get install -y package2

# ✅ Correto
RUN apt-get update && \
    apt-get install -y package1 package2 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
```

## Melhores Práticas

### 1. **Use imagens base leves**
```dockerfile
# ✅ Bom
FROM openjdk:17-jdk-slim

# ❌ Evitar
FROM openjdk:17
```

### 2. **Multi-stage builds**
```dockerfile
# ✅ Bom
FROM maven:3.8.1-openjdk-17 AS builder
# ... build ...
FROM openjdk:17-jdk-slim
COPY --from=builder /app/target/app.jar .
```

### 3. **Ordem de instruções**
```dockerfile
# ✅ Bom (menos invalidação de cache)
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package
```

### 4. **Use .dockerignore**
```
.git
.gitignore
node_modules
npm-debug.log
.env
.DS_Store
target
```

### 5. **Não rodar como root**
```dockerfile
RUN useradd -m appuser
USER appuser
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre imagem e container?**
Imagem é um template read-only. Container é uma instância em execução.

### 2. **Como reduzir tamanho de imagem?**
Use imagens base leves, multi-stage builds, limpe cache.

### 3. **O que é Docker Compose?**
Ferramenta para definir e rodar múltiplos containers com um arquivo YAML.

### 4. **Como persistir dados em Docker?**
Use volumes: `-v /host/path:/container/path` ou named volumes.

### 5. **Como comunicar entre containers?**
Use networks: `docker network create` e `--network`.

## Relação com Outras Tecnologias

- **Kubernetes:** Orquestra containers Docker
- **AWS ECS:** Serviço de containers na AWS
- **CI/CD:** Integra com pipelines
- **Docker Hub:** Registry de imagens
- **Docker Compose:** Orquestra múltiplos containers localmente

---

## Material de Estudo

### Documentação Oficial
- [Docker Official Documentation](https://docs.docker.com/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

### Roadmap
- [roadmap.sh - Docker](https://roadmap.sh/docker)

### Cursos
- **Português:** [Docker Completo - Udemy](https://www.udemy.com/course/docker-completo/)
- **Inglês:** [Docker & Kubernetes - Udemy](https://www.udemy.com/course/docker-kubernetes-complete-guide/)

### Playlists YouTube
- **Português:** [Docker - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO8xWLn2KZZr6q6DmSRR_nAJ)
- **Inglês:** [Docker Tutorial - Traversy Media](https://www.youtube.com/watch?v=fqMOX6JJhGo)

### Livros
- **"Docker in Action"** - Jeff Nickoloff
- **"The Docker Book"** - James Turnbull

### Artigos e Blogs
- [Docker Best Practices - Baeldung](https://www.baeldung.com/docker-best-practices)
- [Docker Blog Official](https://www.docker.com/blog/)

### GitHub Relevante
- [Docker Official](https://github.com/moby/moby)
- [Awesome Docker](https://github.com/veggiemonk/awesome-docker)

### Repositórios Exemplo
- [Docker Examples](https://github.com/docker/awesome-compose)

### Projetos para Praticar
1. **Containerizar aplicação Java**
2. **Docker Compose** com app e banco
3. **Multi-stage build**
4. **Health checks**
5. **Volumes e networks**

---

## Certificações

### Docker Certified Associate (DCA)
- **Nível:** Associate
- **Preço:** ~$165 USD
- **Vale a pena?** Sim, especialmente para DevOps/SRE
- **Ordem ideal:** Após experiência prática com Docker
