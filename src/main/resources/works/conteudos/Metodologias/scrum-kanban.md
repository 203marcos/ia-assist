# Scrum e Kanban

## O que é / Para que serve
- **Scrum:** Framework ágil com sprints (iterações de 1-4 semanas), cerimônias estruturadas, papéis definidos (Product Owner, Scrum Master, Time).
- **Kanban:** Método ágil contínuo com fluxo de trabalho visual, limite de WIP (Work In Progress), foco em entrega contínua.
- **Objetivo:** Entregar valor rapidamente, adaptar-se a mudanças, melhorar comunicação, aumentar produtividade.

## Quando usar / Quando NÃO usar
- **Scrum:** Projetos com requisitos claros, equipes co-locadas ou bem sincronizadas, quando sprints fazem sentido.
- **Kanban:** Fluxo contínuo, requisitos emergentes, suporte/manutenção, quando não há ciclos naturais.
- **Ambos:** Scrumban combina ambos, Scrum com fluxo contínuo.

## Como funciona

### Scrum
- **Sprint Planning:** Equipe planeja o que será feito na sprint (1-4 semanas).
- **Daily Standup:** Reunião diária de 15 min, cada um fala: o que fez, o que vai fazer, bloqueios.
- **Sprint Review:** Demonstração do que foi entregue, feedback do cliente.
- **Sprint Retrospective:** Equipe reflete sobre o que funcionou, o que melhorar.
- **Backlog:** Lista priorizada de funcionalidades.
- **User Stories:** Descrição de funcionalidade do ponto de vista do usuário.

### Kanban
- **Quadro Kanban:** Colunas (To Do, In Progress, Done), cartões representam tarefas.
- **WIP Limit:** Limite de tarefas em cada coluna, evita sobrecarga.
- **Fluxo Contínuo:** Tarefas se movem conforme progridem, sem ciclos fixos.
- **Métricas:** Lead time (tempo total), cycle time (tempo em progresso), throughput (tarefas/período).
- **Melhoria Contínua:** Identificar gargalos, otimizar fluxo.

## Conceitos Importantes

### Scrum
- **Product Owner:** Gerencia backlog, prioriza funcionalidades, representa cliente.
- **Scrum Master:** Facilita processo, remove bloqueios, protege equipe.
- **Development Team:** Desenvolvedores, testers, designers que entregam incremento.
- **Sprint Goal:** Objetivo da sprint, guia o trabalho.
- **Definition of Done:** Critérios para considerar uma tarefa completa.
- **Velocity:** Quantidade de trabalho completado por sprint, usado para planejamento.

### Kanban
- **Lead Time:** Tempo do pedido até entrega.
- **Cycle Time:** Tempo que tarefa leva em progresso.
- **Throughput:** Número de tarefas completadas por período.
- **Bottleneck:** Gargalo no fluxo, identifica onde otimizar.
- **Service Level Agreement (SLA):** Tempo máximo aceitável para completar tarefa.

## Exemplo de Código

```yaml
# Exemplo de User Story em Scrum
User Story: Como usuário, quero fazer login com email e senha
Critérios de Aceitação:
  - Deve validar email válido
  - Deve validar senha com mínimo 8 caracteres
  - Deve retornar token JWT após login bem-sucedido
  - Deve retornar erro 401 se credenciais inválidas
Tamanho: 5 pontos (story points)
Prioridade: Alta

---

# Exemplo de Quadro Kanban
To Do (WIP: 5)
  - [ ] Implementar autenticação OAuth
  - [ ] Criar endpoint de usuários
  - [ ] Documentar API

In Progress (WIP: 3)
  - [ ] Implementar cache Redis (50%)
  - [ ] Testes de integração (80%)

Done
  - [x] Setup do projeto
  - [x] Configurar Docker
  - [x] Criar banco de dados
```

## Principais Erros e Melhores Práticas
- **Erro:** Sprints muito longas — difícil adaptar-se a mudanças.
- **Prática:** Sprints de 1-2 semanas, ciclo rápido de feedback.
- **Erro:** Não respeitar WIP limit em Kanban — acumula trabalho.
- **Prática:** Respeitar limite, terminar tarefas antes de começar novas.
- **Erro:** User stories muito grandes — difícil estimar e completar.
- **Prática:** User stories pequenas, completáveis em 1-2 dias.
- **Erro:** Não fazer retrospectiva — não melhora.
- **Prática:** Retrospectiva regular, implementar melhorias identificadas.
- **Erro:** Não envolver Product Owner — requisitos errados.
- **Prática:** Product Owner presente, backlog bem priorizado.
- **Erro:** Não medir métricas — não sabe se está melhorando.
- **Prática:** Rastrear velocity, lead time, throughput.

## Perguntas Comuns em Entrevistas
1. O que é Scrum e quais são seus papéis principais?
2. Qual é a diferença entre Scrum e Kanban?
3. O que é uma user story e como escrever bem?
4. Como você estimaria uma tarefa em story points?
5. O que é velocity e como usar para planejamento?
6. Como você lidaria com mudanças durante uma sprint?
7. O que é WIP limit e por que é importante?
8. Como você mediria sucesso em um projeto ágil?
9. O que é Definition of Done e por que é importante?
10. Como você conduziria uma retrospectiva efetiva?

## Relação com Outras Tecnologias
- **Ferramentas:** Jira, Azure DevOps, Trello, Asana para gerenciar Scrum/Kanban.
- **CI/CD:** Integração contínua facilita entrega contínua em Kanban.
- **Git:** Versionamento suporta fluxo ágil.
- **Testes Automatizados:** Essencial para manter qualidade em ciclos rápidos.
- **Documentação:** Manter documentação atualizada em metodologias ágeis.
- **Comunicação:** Ferramentas como Slack, Teams para comunicação ágil.

## Material de Estudo
- https://www.scrum.org/resources/what-is-scrum
- https://www.atlassian.com/agile/scrum
- https://www.atlassian.com/agile/kanban
- https://www.udemy.com/course/scrum-master-certification-preparation/
- https://www.youtube.com/playlist?list=PLqq-6Pq4lWTa8AUUSDZTVrVqYJBNyWaZe
- https://www.scrumguides.org/
- https://kanbanize.com/kanban-resources
- https://www.mountaingoatsoftware.com/agile/user-stories

## Certificações
- **Certified ScrumMaster (CSM):** Valida conhecimento em Scrum. Nível intermediário. Custo: ~$385 USD. Vale a pena para líderes técnicos.
- **Professional Scrum Master (PSM I):** Valida conhecimento em Scrum. Nível intermediário. Custo: ~$200 USD. Vale a pena para Scrum Masters.
- **Certified Kanban Manager (CKM):** Valida conhecimento em Kanban. Nível intermediário. Custo: ~$395 USD. Vale a pena para quem trabalha com Kanban.
