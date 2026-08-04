# Azure

## O que é / Para que serve
- **Microsoft Azure:** Plataforma de cloud computing com serviços de computação, armazenamento, rede, banco de dados, IA, analytics.
- **Objetivo:** Infraestrutura escalável, gerenciada, com integração com ecossistema Microsoft e suporte a múltiplas linguagens.

## Quando usar / Quando NÃO usar
- **Usar:** Empresas com stack Microsoft, integração com Office 365/Dynamics, quando precisa de compliance em regiões específicas.
- **NÃO usar:** Quando AWS ou GCP têm melhor custo/benefício, quando não há requisitos Microsoft.

## Como funciona
- **Subscriptions:** Conta de faturamento, contém resource groups.
- **Resource Groups:** Agrupamento lógico de recursos para gerenciamento.
- **Virtual Machines (VMs):** Computação sob demanda, similar a EC2 na AWS.
- **App Service:** Plataforma para hospedar aplicações web, APIs, mobile backends.
- **Azure Container Instances (ACI):** Executar containers sem gerenciar infraestrutura.
- **Azure Kubernetes Service (AKS):** Kubernetes gerenciado.
- **Azure SQL Database:** Banco de dados SQL gerenciado.
- **Azure Cosmos DB:** Banco de dados NoSQL distribuído globalmente.
- **Azure Service Bus:** Mensageria gerenciada.
- **Azure Storage:** Armazenamento de blobs, arquivos, tabelas, filas.

## Conceitos Importantes
- **Regiões:** Localizações geográficas onde recursos são deployados.
- **Availability Zones:** Datacenters isolados dentro de uma região para alta disponibilidade.
- **Virtual Networks (VNets):** Rede privada isolada em Azure.
- **Network Security Groups (NSGs):** Firewall para controlar tráfego.
- **Load Balancer:** Distribuir tráfego entre VMs.
- **Application Gateway:** Load balancer de camada 7 com roteamento baseado em URL.
- **Azure DevOps:** Plataforma para CI/CD, versionamento, planejamento.
- **Managed Identity:** Autenticação sem armazenar credenciais.

## Exemplo de Código

```bash
# Azure CLI - Criar resource group
az group create --name myResourceGroup --location eastus

# Criar App Service Plan
az appservice plan create --name myAppServicePlan \
  --resource-group myResourceGroup --sku B1 --is-linux

# Criar Web App
az webapp create --resource-group myResourceGroup \
  --plan myAppServicePlan --name myWebApp \
  --runtime "JAVA|11-java11"

# Deploy aplicação Java
az webapp deployment source config-zip \
  --resource-group myResourceGroup --name myWebApp \
  --src-path app.zip

# Criar Azure SQL Database
az sql server create --name myserver \
  --resource-group myResourceGroup \
  --admin-user azureuser --admin-password P@ssw0rd1234

az sql db create --resource-group myResourceGroup \
  --server myserver --name mydb \
  --service-objective S0
```

```java
// Conectar a Azure SQL Database em Java
String connectionUrl = "jdbc:sqlserver://myserver.database.windows.net:1433;" +
    "database=mydb;user=azureuser@myserver;password=P@ssw0rd1234;" +
    "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;";

Connection connection = DriverManager.getConnection(connectionUrl);

// Usar Azure Storage em Java
BlobContainerClient containerClient = new BlobContainerClientBuilder()
    .connectionString("DefaultEndpointsProtocol=https;...")
    .containerName("mycontainer")
    .buildClient();

BlobClient blobClient = containerClient.getBlobClient("myfile.txt");
blobClient.uploadFromFile("path/to/file.txt");
```

## Principais Erros e Melhores Práticas
- **Erro:** Não usar resource groups — difícil organizar recursos.
- **Prática:** Organizar recursos por projeto, ambiente, ou função.
- **Erro:** Deixar VMs públicas sem NSG — segurança comprometida.
- **Prática:** Usar NSGs para restringir tráfego, usar bastion hosts para acesso.
- **Erro:** Não usar Managed Identity — armazenar credenciais em código.
- **Prática:** Usar Managed Identity para autenticação entre serviços Azure.
- **Erro:** Não monitorar custos — surpresas na fatura.
- **Prática:** Usar Azure Cost Management, definir alertas, revisar regularmente.
- **Erro:** Não usar backups — perda de dados.
- **Prática:** Configurar backups automáticos, testar restauração.

## Perguntas Comuns em Entrevistas
1. O que é Azure e quais são seus principais serviços?
2. Qual é a diferença entre Azure VMs e App Service?
3. Como funciona o Azure Kubernetes Service (AKS)?
4. O que é Managed Identity e por que usar?
5. Como configurar alta disponibilidade em Azure?
6. Qual é a diferença entre Azure SQL Database e Cosmos DB?
7. Como usar Azure DevOps para CI/CD?
8. O que é um resource group e como organizar recursos?
9. Como monitorar e otimizar custos em Azure?
10. Como implementar disaster recovery em Azure?

## Relação com Outras Tecnologias
- **AWS:** Plataforma concorrente, ambas oferecem serviços similares.
- **GCP:** Plataforma concorrente do Google.
- **Docker:** Containerizar aplicações para Azure Container Instances ou AKS.
- **Kubernetes:** AKS é Kubernetes gerenciado no Azure.
- **Java:** Azure suporta Java em App Service, VMs, AKS.
- **Spring Boot:** Aplicações Spring Boot rodam em Azure App Service.
- **Azure DevOps:** CI/CD para deploy em Azure.
- **Terraform:** Infrastructure as Code para provisionar recursos Azure.

## Material de Estudo
- https://docs.microsoft.com/en-us/azure/
- https://docs.microsoft.com/en-us/azure/developer/java/
- https://www.udemy.com/course/azure-fundamentals-az-900-exam-prep/
- https://www.youtube.com/playlist?list=PLLQuc_7jk63W92oDrT6K6We56yJYNS38A
- https://learn.microsoft.com/en-us/training/azure/
- https://github.com/Azure-Samples
- https://azure.microsoft.com/en-us/free/
- https://www.microsoft.com/en-us/learning/azure-certifications.aspx

## Certificações
- **Azure Fundamentals (AZ-900):** Valida conhecimento básico em Azure. Nível iniciante. Custo: ~$99 USD. Vale a pena para iniciantes.
- **Azure Developer Associate (AZ-204):** Valida conhecimento em desenvolvimento em Azure. Nível intermediário. Custo: ~$165 USD. Vale a pena para plenos.
- **Azure Solutions Architect Expert (AZ-305):** Valida conhecimento em arquitetura em Azure. Nível avançado. Custo: ~$165 USD. Vale a pena para sêniors.
