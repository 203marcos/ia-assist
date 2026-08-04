# SOLID e Padrões de Design

## O que é?

### SOLID
Conjunto de 5 princípios para escrever código mais limpo, manutenível e escalável.

### Padrões de Design
Soluções reutilizáveis para problemas comuns em design de software.

## Para que serve?

- Melhorar qualidade do código
- Facilitar manutenção
- Reduzir acoplamento
- Aumentar coesão
- Facilitar testes

## Onde é usado?

- Desenvolvimento profissional
- Arquitetura de software
- Refatoração de código
- Design de APIs

## Quando usar?

- Sempre que você escreve código
- Ao refatorar
- Ao revisar código

## SOLID

### S - Single Responsibility Principle (SRP)

Uma classe deve ter uma única responsabilidade.

```java
// ❌ Errado
class Usuario {
    public void criar() { }
    public void salvarNoBanco() { }
    public void enviarEmail() { }
    public void gerarRelatorio() { }
}

// ✅ Correto
class Usuario {
    private String nome;
    private String email;
}

class UsuarioService {
    public void criar(Usuario usuario) { }
}

class UsuarioRepository {
    public void salvar(Usuario usuario) { }
}

class EmailService {
    public void enviar(String email) { }
}

class RelatorioService {
    public void gerar() { }
}
```

### O - Open/Closed Principle (OCP)

Aberto para extensão, fechado para modificação.

```java
// ❌ Errado
class Pagamento {
    public void processar(String tipo) {
        if (tipo.equals("cartao")) {
            // Processar cartão
        } else if (tipo.equals("boleto")) {
            // Processar boleto
        }
    }
}

// ✅ Correto
interface MetodoPagamento {
    void processar();
}

class PagamentoCartao implements MetodoPagamento {
    @Override
    public void processar() { }
}

class PagamentoBoleto implements MetodoPagamento {
    @Override
    public void processar() { }
}

class Pagamento {
    private MetodoPagamento metodo;
    
    public void processar() {
        metodo.processar();
    }
}
```

### L - Liskov Substitution Principle (LSP)

Subclasses devem ser substituíveis por suas superclasses.

```java
// ❌ Errado
class Ave {
    public void voar() { }
}

class Pinguim extends Ave {
    @Override
    public void voar() {
        throw new UnsupportedOperationException("Pinguim não voa");
    }
}

// ✅ Correto
class Ave {
    public void mover() { }
}

class Passaro extends Ave {
    @Override
    public void mover() {
        // Voar
    }
}

class Pinguim extends Ave {
    @Override
    public void mover() {
        // Nadar
    }
}
```

### I - Interface Segregation Principle (ISP)

Muitas interfaces específicas é melhor que uma interface genérica.

```java
// ❌ Errado
interface Animal {
    void voar();
    void nadar();
    void correr();
}

class Cachorro implements Animal {
    @Override
    public void voar() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void nadar() { }
    
    @Override
    public void correr() { }
}

// ✅ Correto
interface Voador {
    void voar();
}

interface Nadador {
    void nadar();
}

interface Corredor {
    void correr();
}

class Cachorro implements Nadador, Corredor {
    @Override
    public void nadar() { }
    
    @Override
    public void correr() { }
}
```

### D - Dependency Inversion Principle (DIP)

Dependa de abstrações, não de implementações concretas.

```java
// ❌ Errado
class UsuarioService {
    private UsuarioRepository repository = new UsuarioRepository();
    
    public void criar(Usuario usuario) {
        repository.salvar(usuario);
    }
}

// ✅ Correto
interface UsuarioRepository {
    void salvar(Usuario usuario);
}

class UsuarioService {
    private UsuarioRepository repository;
    
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }
    
    public void criar(Usuario usuario) {
        repository.salvar(usuario);
    }
}
```

## Padrões de Design

### Criacionais

#### Singleton
Uma única instância da classe.

```java
public class Database {
    private static Database instance;
    
    private Database() { }
    
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}

// Ou com enum (thread-safe)
public enum Database {
    INSTANCE;
    
    public void conectar() { }
}
```

#### Factory
Criar objetos sem especificar classes concretas.

```java
interface Veiculo { }
class Carro implements Veiculo { }
class Moto implements Veiculo { }

class VeiculoFactory {
    public static Veiculo criar(String tipo) {
        if (tipo.equals("carro")) {
            return new Carro();
        } else if (tipo.equals("moto")) {
            return new Moto();
        }
        return null;
    }
}
```

#### Builder
Construir objetos complexos passo a passo.

```java
class Usuario {
    private String nome;
    private String email;
    private int idade;
    
    private Usuario(Builder builder) {
        this.nome = builder.nome;
        this.email = builder.email;
        this.idade = builder.idade;
    }
    
    public static class Builder {
        private String nome;
        private String email;
        private int idade;
        
        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder idade(int idade) {
            this.idade = idade;
            return this;
        }
        
        public Usuario build() {
            return new Usuario(this);
        }
    }
}

// Uso
Usuario usuario = new Usuario.Builder()
    .nome("João")
    .email("joao@email.com")
    .idade(30)
    .build();
```

### Estruturais

#### Adapter
Adaptar interface de uma classe para outra.

```java
interface NovaInterface {
    void novoMetodo();
}

class ClasseAntiga {
    public void metodoAntigo() { }
}

class Adapter implements NovaInterface {
    private ClasseAntiga antiga;
    
    public Adapter(ClasseAntiga antiga) {
        this.antiga = antiga;
    }
    
    @Override
    public void novoMetodo() {
        antiga.metodoAntigo();
    }
}
```

#### Decorator
Adicionar funcionalidade a um objeto dinamicamente.

```java
interface Componente {
    void operacao();
}

class ComponenteConcreto implements Componente {
    @Override
    public void operacao() {
        System.out.println("Operação base");
    }
}

class Decorador implements Componente {
    protected Componente componente;
    
    public Decorador(Componente componente) {
        this.componente = componente;
    }
    
    @Override
    public void operacao() {
        componente.operacao();
    }
}

class DecoradorConcreto extends Decorador {
    @Override
    public void operacao() {
        super.operacao();
        System.out.println("Funcionalidade adicional");
    }
}
```

### Comportamentais

#### Observer
Notificar múltiplos objetos sobre mudanças.

```java
interface Observer {
    void atualizar(String mensagem);
}

class Sujeito {
    private List<Observer> observers = new ArrayList<>();
    
    public void adicionar(Observer observer) {
        observers.add(observer);
    }
    
    public void notificar(String mensagem) {
        for (Observer observer : observers) {
            observer.atualizar(mensagem);
        }
    }
}

class ObservadorConcreto implements Observer {
    @Override
    public void atualizar(String mensagem) {
        System.out.println("Notificação: " + mensagem);
    }
}
```

#### Strategy
Encapsular algoritmos intercambiáveis.

```java
interface Estrategia {
    int executar(int a, int b);
}

class Soma implements Estrategia {
    @Override
    public int executar(int a, int b) {
        return a + b;
    }
}

class Subtracao implements Estrategia {
    @Override
    public int executar(int a, int b) {
        return a - b;
    }
}

class Calculadora {
    private Estrategia estrategia;
    
    public void setEstrategia(Estrategia estrategia) {
        this.estrategia = estrategia;
    }
    
    public int executar(int a, int b) {
        return estrategia.executar(a, b);
    }
}
```

## Exemplos Reais

### Exemplo 1: Aplicar SOLID em Serviço

```java
// ❌ Antes (violando SOLID)
class UsuarioService {
    public void criar(Usuario usuario) {
        // Validar
        if (usuario.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome vazio");
        }
        
        // Salvar
        Connection conn = DriverManager.getConnection("jdbc:mysql://...");
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuario...");
        stmt.executeUpdate();
        
        // Enviar email
        SMTPClient smtp = new SMTPClient();
        smtp.enviar(usuario.getEmail(), "Bem-vindo!");
    }
}

// ✅ Depois (aplicando SOLID)
interface UsuarioRepository {
    void salvar(Usuario usuario);
}

interface EmailService {
    void enviarBoasVindas(Usuario usuario);
}

interface ValidadorUsuario {
    void validar(Usuario usuario);
}

class UsuarioService {
    private UsuarioRepository repository;
    private EmailService emailService;
    private ValidadorUsuario validador;
    
    public UsuarioService(
        UsuarioRepository repository,
        EmailService emailService,
        ValidadorUsuario validador
    ) {
        this.repository = repository;
        this.emailService = emailService;
        this.validador = validador;
    }
    
    public void criar(Usuario usuario) {
        validador.validar(usuario);
        repository.salvar(usuario);
        emailService.enviarBoasVindas(usuario);
    }
}
```

## Principais Erros

### 1. **Violar SRP**
```java
// ❌ Errado
class Usuario {
    public void criar() { }
    public void salvar() { }
    public void enviarEmail() { }
}

// ✅ Correto
class Usuario { }
class UsuarioService { }
class EmailService { }
```

### 2. **Violar DIP**
```java
// ❌ Errado
class UsuarioService {
    private UsuarioRepository repository = new UsuarioRepository();
}

// ✅ Correto
class UsuarioService {
    private UsuarioRepository repository;
    
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }
}
```

## Melhores Práticas

### 1. **Sempre aplicar SOLID**
Código mais limpo e manutenível.

### 2. **Usar padrões apropriados**
Não force padrões onde não são necessários.

### 3. **Refatorar regularmente**
Melhorar código existente.

### 4. **Revisar código**
Identificar violações de SOLID.

## Perguntas Comuns em Entrevistas

### 1. **O que é SOLID?**
Conjunto de 5 princípios para código limpo.

### 2. **Qual a diferença entre Factory e Builder?**
Factory cria objetos simples. Builder cria objetos complexos passo a passo.

### 3. **Quando usar Singleton?**
Quando você precisa de uma única instância (Database, Logger).

### 4. **O que é Dependency Injection?**
Injetar dependências em vez de criá-las internamente.

---

## Material de Estudo

### Livros
- **"Clean Code"** - Robert C. Martin
- **"Design Patterns"** - Gang of Four
- **"Refactoring"** - Martin Fowler

### Artigos
- [SOLID Principles - Baeldung](https://www.baeldung.com/solid-principles)
- [Design Patterns - Refactoring Guru](https://refactoring.guru/design-patterns)

### Cursos
- **Português:** [SOLID em Java - Udemy](https://www.udemy.com/course/solid-em-java/)
- **Inglês:** [Design Patterns in Java - Udemy](https://www.udemy.com/course/design-patterns-java/)
