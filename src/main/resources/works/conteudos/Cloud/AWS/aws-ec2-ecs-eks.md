# AWS: EC2, ECS, EKS, ECR e Load Balancers

## O que é?

### EC2 (Elastic Compute Cloud)
Serviço de computação em nuvem que oferece máquinas virtuais escaláveis. Você controla totalmente a instância.

### ECS (Elastic Container Service)
Serviço de orquestração de containers gerenciado pela AWS. Mais simples que Kubernetes.

### EKS (Elastic Kubernetes Service)
Serviço Kubernetes gerenciado pela AWS. Mais complexo, mas mais poderoso.

### ECR (Elastic Container Registry)
Registro privado de imagens Docker na AWS.

### Load Balancers
Distribuem tráfego entre múltiplas instâncias.

## Para que serve?

- **EC2:** Rodar aplicações em máquinas virtuais
- **ECS:** Orquestrar containers de forma simples
- **EKS:** Orquestrar containers com Kubernetes
- **ECR:** Armazenar imagens Docker privadas
- **Load Balancers:** Distribuir tráfego e garantir alta disponibilidade

## Onde é usado?

- Aplicações web
- Microserviços
- APIs REST
- Processamento de dados
- Qualquer aplicação que precisa escalar

## Quando usar?

### EC2
- Quando você precisa de controle total
- Para aplicações legadas
- Quando você quer customizar o SO

### ECS
- Para microserviços simples
- Quando você quer simplicidade
- Integração nativa com AWS

### EKS
- Para microserviços complexos
- Quando você quer portabilidade (Kubernetes)
- Para equipes experientes com Kubernetes

## Como funciona?

### EC2

```
1. Criar instância (escolher AMI, tipo, segurança)
2. Conectar via SSH
3. Instalar dependências
4. Deploy da aplicação
5. Configurar auto-scaling
```

### ECS

```
1. Criar ECR repository
2. Build e push de imagem Docker
3. Criar task definition
4. Criar serviço
5. Configurar load balancer
```

### EKS

```
1. Criar cluster EKS
2. Criar ECR repository
3. Build e push de imagem Docker
4. Criar deployment Kubernetes
5. Expor via service
```

## Conceitos Importantes

### EC2 - Tipos de Instância
- **t3:** Uso geral, burstable
- **m5:** Uso geral, performance consistente
- **c5:** Otimizado para computação
- **r5:** Otimizado para memória
- **i3:** Otimizado para I/O

### ECS - Conceitos
- **Task:** Unidade de trabalho (container)
- **Task Definition:** Template para task
- **Service:** Mantém número desejado de tasks
- **Cluster:** Grupo de recursos

### EKS - Conceitos
- **Pod:** Unidade mínima (container)
- **Deployment:** Gerencia pods
- **Service:** Expõe pods
- **Namespace:** Isolamento lógico

### Load Balancers
- **ALB (Application Load Balancer):** Layer 7 (HTTP/HTTPS)
- **NLB (Network Load Balancer):** Layer 4 (TCP/UDP)
- **CLB (Classic Load Balancer):** Legado

## Exemplos Reais

### Exemplo 1: EC2 com Auto Scaling

```bash
# 1. Criar AMI com aplicação
# 2. Criar launch template
aws ec2 create-launch-template \
  --launch-template-name my-app-template \
  --version-description "App v1.0" \
  --launch-template-data '{
    "ImageId": "ami-0c55b159cbfafe1f0",
    "InstanceType": "t3.micro",
    "KeyName": "my-key",
    "SecurityGroupIds": ["sg-12345678"]
  }'

# 3. Criar auto scaling group
aws autoscaling create-auto-scaling-group \
  --auto-scaling-group-name my-app-asg \
  --launch-template LaunchTemplateName=my-app-template \
  --min-size 1 \
  --max-size 5 \
  --desired-capacity 2 \
  --availability-zones us-east-1a us-east-1b
```

### Exemplo 2: ECS com Docker

```bash
# 1. Criar ECR repository
aws ecr create-repository --repository-name my-app

# 2. Build e push de imagem
docker build -t my-app:1.0 .
docker tag my-app:1.0 123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:1.0
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:1.0

# 3. Criar task definition
aws ecs register-task-definition \
  --family my-app-task \
  --container-definitions '[{
    "name": "my-app",
    "image": "123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:1.0",
    "memory": 512,
    "cpu": 256,
    "portMappings": [{
      "containerPort": 8080,
      "hostPort": 8080
    }]
  }]'

# 4. Criar serviço
aws ecs create-service \
  --cluster my-cluster \
  --service-name my-app-service \
  --task-definition my-app-task \
  --desired-count 2 \
  --launch-type EC2
```

### Exemplo 3: EKS com Kubernetes

```bash
# 1. Criar cluster EKS
eksctl create cluster \
  --name my-cluster \
  --version 1.27 \
  --region us-east-1 \
  --nodegroup-name my-nodes \
  --nodes 2 \
  --nodes-min 1 \
  --nodes-max 5

# 2. Build e push de imagem
docker build -t my-app:1.0 .
docker tag my-app:1.0 123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:1.0
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:1.0

# 3. Criar deployment
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: my-app
        image: 123456789.dkr.ecr.us-east-1.amazonaws.com/my-app:1.0
        ports:
        - containerPort: 8080
EOF

# 4. Expor via service
kubectl apply -f - <<EOF
apiVersion: v1
kind: Service
metadata:
  name: my-app-service
spec:
  type: LoadBalancer
  selector:
    app: my-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
EOF
```

### Exemplo 4: ALB com ECS

```bash
# 1. Criar ALB
aws elbv2 create-load-balancer \
  --name my-app-alb \
  --subnets subnet-12345678 subnet-87654321 \
  --security-groups sg-12345678

# 2. Criar target group
aws elbv2 create-target-group \
  --name my-app-targets \
  --protocol HTTP \
  --port 8080 \
  --vpc-id vpc-12345678

# 3. Criar listener
aws elbv2 create-listener \
  --load-balancer-arn arn:aws:elasticloadbalancing:... \
  --protocol HTTP \
  --port 80 \
  --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:...
```

## Principais Erros

### 1. **Não usar Auto Scaling**
```bash
# ❌ Errado
# Instância única, sem escalabilidade

# ✅ Correto
# Auto Scaling Group com min/max
```

### 2. **Não usar Load Balancer**
```bash
# ❌ Errado
# Acessar instância diretamente

# ✅ Correto
# Usar ALB/NLB para distribuir tráfego
```

### 3. **Não configurar health checks**
```bash
# ❌ Errado
# Sem health checks, instâncias ruins continuam recebendo tráfego

# ✅ Correto
aws elbv2 modify-target-group \
  --target-group-arn arn:... \
  --health-check-enabled \
  --health-check-path /health \
  --health-check-interval-seconds 30
```

### 4. **Usar EC2 quando ECS/EKS seria melhor**
```bash
# ❌ Errado
# Gerenciar containers manualmente em EC2

# ✅ Correto
# Usar ECS ou EKS para orquestração
```

## Melhores Práticas

### 1. **Use Auto Scaling**
```bash
# ✅ Bom
aws autoscaling create-auto-scaling-group \
  --auto-scaling-group-name my-app-asg \
  --min-size 2 \
  --max-size 10 \
  --desired-capacity 3
```

### 2. **Use Load Balancer**
```bash
# ✅ Bom
# Sempre usar ALB/NLB na frente de instâncias
```

### 3. **Configure Health Checks**
```bash
# ✅ Bom
aws elbv2 modify-target-group \
  --target-group-arn arn:... \
  --health-check-path /health
```

### 4. **Use Security Groups**
```bash
# ✅ Bom
# Restringir acesso apenas ao necessário
```

### 5. **Monitore com CloudWatch**
```bash
# ✅ Bom
aws cloudwatch put-metric-alarm \
  --alarm-name high-cpu \
  --metric-name CPUUtilization \
  --threshold 80
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre EC2, ECS e EKS?**
EC2 é máquina virtual. ECS é orquestração simples. EKS é Kubernetes gerenciado.

### 2. **Quando usar ECS vs EKS?**
ECS para simplicidade. EKS para complexidade e portabilidade.

### 3. **O que é Auto Scaling?**
Aumenta/diminui número de instâncias baseado em métricas.

### 4. **Como funciona Load Balancer?**
Distribui tráfego entre múltiplas instâncias.

### 5. **O que é ECR?**
Registro privado de imagens Docker na AWS.

## Relação com Outras Tecnologias

- **Docker:** Containerização
- **Kubernetes:** Orquestração (EKS)
- **CloudWatch:** Monitoramento
- **RDS:** Banco de dados gerenciado
- **S3:** Armazenamento de objetos

---

## Material de Estudo

### Documentação Oficial
- [AWS EC2 Documentation](https://docs.aws.amazon.com/ec2/)
- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [AWS ECR Documentation](https://docs.aws.amazon.com/ecr/)

### Roadmap
- [roadmap.sh - AWS](https://roadmap.sh/aws)

### Cursos
- **Português:** [AWS Completo - Udemy](https://www.udemy.com/course/aws-completo/)
- **Inglês:** [AWS Certified Solutions Architect - Udemy](https://www.udemy.com/course/aws-certified-solutions-architect-associate/)

### Playlists YouTube
- **Português:** [AWS - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO8xWLn2KZZr6q6DmSRR_nAJ)
- **Inglês:** [AWS Tutorial - Traversy Media](https://www.youtube.com/watch?v=XwfxjkKvFwo)

### Livros
- **"AWS in Action"** - Andreas Wittig, Michael Wittig
- **"The AWS Well-Architected Framework"** - AWS

### Artigos e Blogs
- [AWS Blog Official](https://aws.amazon.com/blogs/)
- [Baeldung - AWS](https://www.baeldung.com/aws)

### GitHub Relevante
- [AWS Examples](https://github.com/aws/aws-sdk-java)

### Repositórios Exemplo
- [AWS Samples](https://github.com/aws-samples)

### Projetos para Praticar
1. **Aplicação em EC2** com Auto Scaling
2. **Microserviço em ECS** com Docker
3. **Cluster EKS** com Kubernetes
4. **Load Balancer** com múltiplas instâncias
5. **CI/CD** com CodePipeline

---

## Certificações

### AWS Certified Cloud Practitioner
- **Nível:** Foundational
- **Preço:** ~$100 USD
- **Vale a pena?** Sim, para iniciantes
- **Ordem ideal:** Primeira certificação AWS

### AWS Certified Solutions Architect Associate
- **Nível:** Associate
- **Preço:** ~$150 USD
- **Vale a pena?** Sim, muito valorizada
- **Ordem ideal:** Após Cloud Practitioner

### AWS Certified Developer Associate
- **Nível:** Associate
- **Preço:** ~$150 USD
- **Vale a pena?** Sim, para desenvolvedores
- **Ordem ideal:** Após Cloud Practitioner
