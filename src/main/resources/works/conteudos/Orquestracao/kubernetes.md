# Kubernetes

## O que é / Para que serve
- **Kubernetes (K8s):** Plataforma open-source para orquestração de containers, automatizando deploy, scaling e gerenciamento de aplicações containerizadas.
- **Objetivo:** Abstrair infraestrutura, garantir alta disponibilidade, auto-scaling, auto-healing, rolling updates, gerenciamento de recursos.

## Quando usar / Quando NÃO usar
- **Usar:** Aplicações em containers, microsserviços, ambientes de produção, quando precisa de escalabilidade e resiliência.
- **NÃO usar:** Aplicações simples (overhead), desenvolvimento local (use Docker Compose), quando infraestrutura é muito simples.

## Como funciona
- **Cluster:** Conjunto de nós (máquinas) que executam containers.
- **Pod:** Unidade mínima, contém um ou mais containers (geralmente um).
- **Deployment:** Define como criar e atualizar pods, número de réplicas, estratégia de rolling update.
- **Service:** Abstração para expor pods, fornece DNS e load balancing.
- **ConfigMap/Secret:** Armazenam configurações e dados sensíveis.
- **Ingress:** Roteia tráfego HTTP/HTTPS para serviços.
- **PersistentVolume:** Armazenamento persistente para dados.
- **Scheduler:** Aloca pods em nós baseado em recursos disponíveis.

## Conceitos Importantes
- **Declarativo:** Descrever estado desejado, Kubernetes mantém esse estado.
- **Self-healing:** Reinicia containers que falham, substitui nós mortos.
- **Auto-scaling:** Horizontal Pod Autoscaler (HPA) escala pods baseado em métricas.
- **Rolling Update:** Atualiza pods gradualmente sem downtime.
- **Namespace:** Isolamento lógico de recursos dentro de um cluster.
- **RBAC (Role-Based Access Control):** Controle de acesso granular.
- **Labels/Selectors:** Organizam e selecionam recursos.
- **Liveness/Readiness Probes:** Verificam saúde de containers.

## Exemplo de Código

```yaml
# Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: myregistry/user-service:1.0
        ports:
        - containerPort: 8080
        env:
        - name: DATABASE_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: db-url
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5

---
# Service
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector:
    app: user-service
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer

---
# ConfigMap
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  db-url: "jdbc:mysql://mysql:3306/mydb"
```

## Principais Erros e Melhores Práticas
- **Erro:** Não definir resource requests/limits — pode causar eviction.
- **Prática:** Sempre definir CPU e memória (requests e limits).
- **Erro:** Usar latest tag em imagens — difícil rastrear versões.
- **Prática:** Usar tags específicas (v1.0, v1.1) ou SHA do commit.
- **Erro:** Não implementar health checks — Kubernetes não sabe se pod está saudável.
- **Prática:** Implementar liveness e readiness probes.
- **Erro:** Armazenar secrets em ConfigMap — inseguro.
- **Prática:** Usar Secret para dados sensíveis, considerar external secret management.
- **Erro:** Não usar namespaces — difícil organizar recursos.
- **Prática:** Usar namespaces para separar ambientes (dev, staging, prod).

## Perguntas Comuns em Entrevistas
1. O que é Kubernetes e qual é seu propósito principal?
2. Qual é a diferença entre Pod, Deployment e Service?
3. Como funciona o auto-scaling em Kubernetes?
4. O que são liveness e readiness probes?
5. Como você faria um rolling update em Kubernetes?
6. O que é um Ingress e como funciona?
7. Como gerenciar configurações e secrets em Kubernetes?
8. O que é um namespace e quando usar?
9. Como você monitoraria uma aplicação em Kubernetes?
10. Qual é a diferença entre StatefulSet e Deployment?

## Relação com Outras Tecnologias
- **Docker:** Containers executados por Kubernetes.
- **Microsserviços:** Kubernetes orquestra microsserviços.
- **Cloud:** AWS EKS, Azure AKS, GCP GKE.
- **Helm:** Package manager para Kubernetes.
- **Prometheus:** Monitoramento e métricas em Kubernetes.
- **Istio:** Service mesh para comunicação entre serviços.
- **ArgoCD:** GitOps para deploy contínuo em Kubernetes.
- **Jenkins/GitLab CI:** CI/CD para build e deploy em Kubernetes.

## Material de Estudo
- https://kubernetes.io/docs/
- https://kubernetes.io/docs/tutorials/
- https://www.udemy.com/course/kubernetes-complete-guide-to-devops/
- https://www.youtube.com/playlist?list=PLLQuc_7jk63W92oDrT6K6We56yJYNS38A
- https://www.digitalocean.com/community/tutorials/an-introduction-to-kubernetes
- https://github.com/kubernetes/kubernetes
- https://www.katacoda.com/courses/kubernetes
- https://play.instruqt.com/embed/kubernetes

## Certificações
- **Kubernetes Application Developer (CKAD):** Valida conhecimento em deploy de aplicações em Kubernetes. Nível intermediário. Custo: ~$395 USD. Vale a pena para plenos com foco em DevOps.
- **Certified Kubernetes Administrator (CKA):** Valida conhecimento em administração de clusters Kubernetes. Nível avançado. Custo: ~$395 USD. Vale a pena para sêniors com foco em infraestrutura.
