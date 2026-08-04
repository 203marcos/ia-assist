# Git

## O que é?
Git é um sistema de controle de versão distribuído que rastreia mudanças em arquivos. Permite que múltiplos desenvolvedores trabalhem no mesmo projeto sem conflitos.

## Para que serve?
- Rastrear histórico de mudanças
- Colaboração entre desenvolvedores
- Reverter mudanças
- Criar branches para features
- Integração com CI/CD

## Onde é usado?
- Desenvolvimento de software profissional
- Projetos open source
- Qualquer projeto que precisa versionamento
- CI/CD pipelines

## Quando usar?
- Sempre que você escreve código
- Em qualquer projeto com múltiplos desenvolvedores
- Para backup distribuído

## Quando NÃO usar?
- Arquivos binários muito grandes (use Git LFS)
- Dados sensíveis (use .gitignore)

## Como funciona?

### Conceitos Principais

#### Repository (Repositório)
Pasta que contém histórico completo do projeto.

#### Commit
Snapshot de mudanças com mensagem descritiva.

#### Branch
Linha de desenvolvimento independente.

#### Remote
Repositório remoto (GitHub, GitLab, etc).

### Fluxo Básico

```
1. git clone - Clonar repositório
2. git checkout -b feature - Criar branch
3. git add - Adicionar mudanças
4. git commit - Confirmar mudanças
5. git push - Enviar para remoto
6. git pull request - Solicitar merge
7. git merge - Mesclar branches
```

## Conceitos Importantes

### Staging Area
Área intermediária entre working directory e commit.

```bash
git add arquivo.txt      # Adicionar arquivo
git add .                # Adicionar todos
git reset arquivo.txt    # Remover do staging
```

### Commits
```bash
git commit -m "Mensagem descritiva"
git commit --amend       # Alterar último commit
git revert <commit>      # Reverter commit
```

### Branches
```bash
git branch                    # Listar branches
git branch feature-x          # Criar branch
git checkout feature-x        # Mudar para branch
git checkout -b feature-x     # Criar e mudar
git merge feature-x           # Mesclar branch
git branch -d feature-x       # Deletar branch
```

### Remotes
```bash
git remote -v                 # Listar remotes
git remote add origin <url>   # Adicionar remote
git push origin main          # Enviar para remoto
git pull origin main          # Receber do remoto
git fetch origin              # Buscar sem mesclar
```

## Exemplos Reais

### Exemplo 1: Fluxo Básico

```bash
# 1. Clonar repositório
git clone https://github.com/usuario/projeto.git
cd projeto

# 2. Criar branch para feature
git checkout -b feature/login

# 3. Fazer mudanças
echo "código" > login.java

# 4. Adicionar mudanças
git add login.java

# 5. Commit
git commit -m "Implementar login com JWT"

# 6. Push para remoto
git push origin feature/login

# 7. Criar pull request no GitHub
# (via interface web)

# 8. Após aprovação, merge
git checkout main
git pull origin main
git merge feature/login
git push origin main
```

### Exemplo 2: Resolver Conflitos

```bash
# 1. Tentar merge
git merge feature-x
# CONFLICT (content merge): Merge conflict in arquivo.txt

# 2. Ver conflitos
git status
git diff

# 3. Editar arquivo manualmente
# Remover marcadores de conflito (<<<<, ====, >>>>)

# 4. Adicionar e confirmar
git add arquivo.txt
git commit -m "Resolver conflito de merge"
git push origin main
```

### Exemplo 3: Rebase

```bash
# 1. Criar branch
git checkout -b feature-x

# 2. Fazer commits
git commit -m "Commit 1"
git commit -m "Commit 2"

# 3. Main foi atualizado
git fetch origin

# 4. Rebase em vez de merge
git rebase origin/main

# 5. Se houver conflitos, resolver e continuar
git add .
git rebase --continue

# 6. Force push (cuidado!)
git push origin feature-x --force
```

### Exemplo 4: Stash

```bash
# 1. Mudanças não commitadas
git status
# modified: arquivo.txt

# 2. Guardar mudanças temporariamente
git stash

# 3. Trabalhar em outra coisa
git checkout main

# 4. Recuperar mudanças
git stash pop
```

### Exemplo 5: Cherry-pick

```bash
# 1. Ver commits
git log --oneline

# 2. Copiar commit específico para branch atual
git cherry-pick <commit-hash>

# 3. Se houver conflitos, resolver
git add .
git cherry-pick --continue
```

## Principais Erros

### 1. **Commits muito grandes**
```bash
# ❌ Errado
git commit -m "Implementar login, refatorar código, corrigir bugs"

# ✅ Correto
git commit -m "Implementar login com JWT"
git commit -m "Refatorar UsuarioService"
git commit -m "Corrigir bug de validação"
```

### 2. **Mensagens de commit ruins**
```bash
# ❌ Errado
git commit -m "fix"
git commit -m "alterações"

# ✅ Correto
git commit -m "Corrigir validação de email"
git commit -m "Implementar refresh token"
```

### 3. **Commitar arquivos sensíveis**
```bash
# ❌ Errado
git add .env
git commit -m "Adicionar configurações"

# ✅ Correto
# .gitignore
.env
*.key
secrets/
```

### 4. **Force push sem cuidado**
```bash
# ❌ Errado
git push origin main --force

# ✅ Correto
git push origin feature-x --force-with-lease
```

### 5. **Não fazer pull antes de push**
```bash
# ❌ Errado
git push origin main
# Erro: rejected (fetch first)

# ✅ Correto
git pull origin main
git push origin main
```

## Melhores Práticas

### 1. **Use .gitignore**
```
# .gitignore
.env
.DS_Store
node_modules/
target/
*.log
```

### 2. **Commits atômicos**
```bash
# ✅ Bom
git commit -m "Implementar autenticação JWT"
git commit -m "Adicionar testes para JWT"

# ❌ Evitar
git commit -m "Implementar autenticação, testes, refatoração"
```

### 3. **Mensagens descritivas**
```bash
# ✅ Bom
git commit -m "Implementar login com OAuth 2.0"

# ❌ Evitar
git commit -m "fix"
```

### 4. **Branches com nomes claros**
```bash
# ✅ Bom
git checkout -b feature/login
git checkout -b bugfix/email-validation
git checkout -b refactor/user-service

# ❌ Evitar
git checkout -b feature1
git checkout -b fix
```

### 5. **Pull requests antes de merge**
```bash
# ✅ Bom
# Criar PR, revisar código, depois merge

# ❌ Evitar
# Fazer merge direto sem revisão
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre merge e rebase?**
Merge cria commit de merge. Rebase reaplica commits. Rebase é mais limpo, merge é mais seguro.

### 2. **Como reverter um commit?**
`git revert <commit>` cria novo commit que desfaz mudanças. `git reset` remove commits (cuidado!).

### 3. **O que é cherry-pick?**
Copia um commit específico para branch atual.

### 4. **Como resolver conflitos?**
Editar arquivo manualmente, remover marcadores, `git add`, `git commit`.

### 5. **O que é stash?**
Guarda mudanças temporariamente sem commitar.

## Relação com Outras Tecnologias

- **GitHub/GitLab:** Hospedagem de repositórios
- **CI/CD:** Integração automática
- **Docker:** Versionamento de imagens
- **Kubernetes:** Versionamento de manifests

---

## Material de Estudo

### Documentação Oficial
- [Git Official Documentation](https://git-scm.com/doc)
- [Git Book](https://git-scm.com/book/en/v2)

### Roadmap
- [roadmap.sh - Git](https://roadmap.sh/git-github)

### Cursos
- **Português:** [Git e GitHub - Udemy](https://www.udemy.com/course/git-e-github/)
- **Inglês:** [Git Complete - Udemy](https://www.udemy.com/course/git-complete/)

### Playlists YouTube
- **Português:** [Git - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO8xWLn2KZZr6q6DmSRR_nAJ)
- **Inglês:** [Git Tutorial - Traversy Media](https://www.youtube.com/watch?v=SWYqp7iY_Tc)

### Livros
- **"Pro Git"** - Scott Chacon, Ben Straub

### Artigos e Blogs
- [Git Best Practices - Baeldung](https://www.baeldung.com/git)
- [Git Tips - Medium](https://medium.com/tag/git)

### GitHub Relevante
- [Git Official](https://github.com/git/git)

### Projetos para Praticar
1. **Repositório pessoal** com múltiplos branches
2. **Colaboração** em projeto open source
3. **Resolução de conflitos**
4. **Rebase e cherry-pick**
5. **CI/CD** com Git

---

## Certificações

Não há certificações específicas para Git, mas conhecimento é essencial para qualquer desenvolvedor.
