# JUnit e TestNG

## O que é?

### JUnit
JUnit é um framework de testes unitários para Java. É o padrão de facto para testes em Java, com suporte a assertions, fixtures e test runners.

### TestNG
TestNG é um framework de testes mais moderno e flexível que JUnit, inspirado em NUnit e JUnit. Oferece recursos avançados como data-driven testing e testes paralelos.

## Para que serve?

- Validar comportamento de unidades de código (métodos, classes)
- Automatizar testes
- Documentar comportamento esperado
- Facilitar refatoração com confiança
- Integrar com CI/CD

## Onde é usado?

- Desenvolvimento de software profissional
- Testes unitários em Java
- Testes de integração
- Testes parametrizados
- Testes paralelos

## Quando usar?

- Sempre que você escreve código (TDD)
- Para validar lógica de negócio
- Para regressão
- Para documentação viva

## Quando NÃO usar?

- Para testes de UI (use Selenium, Cypress)
- Para testes de performance (use JMH)
- Para testes de carga (use JMeter, Gatling)

## Como funciona?

### JUnit 5 (Jupiter)

#### Estrutura Básica
```java
@DisplayName("Testes de Calculadora")
class CalculadoraTest {
    
    private Calculadora calculadora;
    
    @BeforeEach
    void setUp() {
        calculadora = new Calculadora();
    }
    
    @Test
    @DisplayName("Deve somar dois números")
    void testSoma() {
        int resultado = calculadora.somar(2, 3);
        assertEquals(5, resultado);
    }
    
    @Test
    void testSubtracao() {
        int resultado = calculadora.subtrair(5, 3);
        assertEquals(2, resultado);
    }
    
    @AfterEach
    void tearDown() {
        calculadora = null;
    }
}
```

#### Ciclo de Vida
1. `@BeforeAll` - Executado uma vez antes de todos os testes
2. `@BeforeEach` - Executado antes de cada teste
3. `@Test` - Teste
4. `@AfterEach` - Executado após cada teste
5. `@AfterAll` - Executado uma vez após todos os testes

### TestNG

#### Estrutura Básica
```java
public class CalculadoraTest {
    
    private Calculadora calculadora;
    
    @BeforeMethod
    public void setUp() {
        calculadora = new Calculadora();
    }
    
    @Test
    public void testSoma() {
        int resultado = calculadora.somar(2, 3);
        Assert.assertEquals(resultado, 5);
    }
    
    @Test
    public void testSubtracao() {
        int resultado = calculadora.subtrair(5, 3);
        Assert.assertEquals(resultado, 2);
    }
    
    @AfterMethod
    public void tearDown() {
        calculadora = null;
    }
}
```

## Conceitos Importantes

### Assertions (JUnit 5)

```java
// Igualdade
assertEquals(5, 2 + 3);
assertNotEquals(5, 2 + 2);

// Booleanos
assertTrue(true);
assertFalse(false);

// Nulos
assertNull(null);
assertNotNull("valor");

// Exceções
assertThrows(IllegalArgumentException.class, () -> {
    throw new IllegalArgumentException();
});

// Mensagens customizadas
assertEquals(5, 2 + 3, "2 + 3 deve ser 5");

// Múltiplas assertions
assertAll(
    () -> assertEquals(5, 2 + 3),
    () -> assertEquals(1, 2 - 1),
    () -> assertTrue(true)
);
```

### Testes Parametrizados (JUnit 5)

```java
@ParameterizedTest
@ValueSource(ints = { 1, 3, 5, -3, 15, Integer.MAX_VALUE })
void testNumeroImpar(int numero) {
    assertTrue(numero % 2 != 0);
}

@ParameterizedTest
@CsvSource({
    "1, 1, 2",
    "2, 3, 5",
    "5, 3, 8"
})
void testSoma(int a, int b, int esperado) {
    assertEquals(esperado, calculadora.somar(a, b));
}

@ParameterizedTest
@MethodSource("provideNumbers")
void testComMethodSource(int numero) {
    assertTrue(numero > 0);
}

static Stream<Arguments> provideNumbers() {
    return Stream.of(
        Arguments.of(1),
        Arguments.of(2),
        Arguments.of(3)
    );
}
```

### Testes Parametrizados (TestNG)

```java
@DataProvider
public Object[][] somas() {
    return new Object[][] {
        { 1, 1, 2 },
        { 2, 3, 5 },
        { 5, 3, 8 }
    };
}

@Test(dataProvider = "somas")
public void testSoma(int a, int b, int esperado) {
    Assert.assertEquals(calculadora.somar(a, b), esperado);
}
```

### Testes Paralelos (TestNG)

```xml
<!-- testng.xml -->
<suite name="Suite" parallel="methods" thread-count="5">
    <test name="Test">
        <classes>
            <class name="com.exemplo.CalculadoraTest" />
        </classes>
    </test>
</suite>
```

### Mocks com Mockito

```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    
    @Mock
    private UsuarioRepository repository;
    
    @InjectMocks
    private UsuarioService service;
    
    @Test
    void testObterUsuario() {
        // Arrange
        Usuario usuario = new Usuario(1L, "João", "joao@email.com");
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        
        // Act
        Usuario resultado = service.obter(1L);
        
        // Assert
        assertEquals("João", resultado.getNome());
        verify(repository, times(1)).findById(1L);
    }
}
```

## Exemplos Reais

### Exemplo 1: Teste Unitário Simples (JUnit 5)

```java
@DisplayName("Testes de Validação de Email")
class EmailValidatorTest {
    
    private EmailValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
    }
    
    @Test
    @DisplayName("Email válido deve retornar true")
    void testEmailValido() {
        assertTrue(validator.isValid("joao@email.com"));
    }
    
    @Test
    @DisplayName("Email sem @ deve retornar false")
    void testEmailSemArroba() {
        assertFalse(validator.isValid("joaoemail.com"));
    }
    
    @Test
    @DisplayName("Email vazio deve retornar false")
    void testEmailVazio() {
        assertFalse(validator.isValid(""));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "joao@email.com",
        "maria@empresa.com.br",
        "user+tag@domain.co.uk"
    })
    void testEmailsValidos(String email) {
        assertTrue(validator.isValid(email));
    }
}
```

### Exemplo 2: Teste com Mock (JUnit 5 + Mockito)

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de UsuarioService")
class UsuarioServiceTest {
    
    @Mock
    private UsuarioRepository repository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UsuarioService service;
    
    @Test
    @DisplayName("Deve criar usuário e enviar email")
    void testCriarUsuario() {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);
        
        // Act
        Usuario resultado = service.criar(usuario);
        
        // Assert
        assertEquals("João", resultado.getNome());
        verify(repository, times(1)).save(usuario);
        verify(emailService, times(1)).enviarBoasVindas(usuario);
    }
    
    @Test
    @DisplayName("Deve lançar exceção se email já existe")
    void testCriarUsuarioDuplicado() {
        // Arrange
        Usuario usuario = new Usuario("João", "joao@email.com");
        when(repository.existsByEmail("joao@email.com")).thenReturn(true);
        
        // Act & Assert
        assertThrows(EmailJaExisteException.class, () -> {
            service.criar(usuario);
        });
    }
}
```

### Exemplo 3: Teste de Integração

```java
@SpringBootTest
@DisplayName("Testes de Integração de UsuarioController")
class UsuarioControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UsuarioRepository repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
    
    @Test
    @DisplayName("GET /usuarios deve retornar lista vazia")
    void testListarVazio() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    @DisplayName("POST /usuarios deve criar novo usuário")
    void testCriarUsuario() throws Exception {
        String json = """
            {
                "nome": "João",
                "email": "joao@email.com"
            }
            """;
        
        mockMvc.perform(post("/api/v1/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome", is("João")));
    }
}
```

### Exemplo 4: Teste Parametrizado (TestNG)

```java
public class CalculadoraTest {
    
    private Calculadora calculadora;
    
    @BeforeMethod
    public void setUp() {
        calculadora = new Calculadora();
    }
    
    @DataProvider(name = "operacoes")
    public Object[][] operacoes() {
        return new Object[][] {
            { 2, 3, 5, "somar" },
            { 5, 3, 2, "subtrair" },
            { 4, 5, 20, "multiplicar" },
            { 10, 2, 5, "dividir" }
        };
    }
    
    @Test(dataProvider = "operacoes")
    public void testOperacoes(int a, int b, int esperado, String operacao) {
        int resultado;
        
        switch (operacao) {
            case "somar":
                resultado = calculadora.somar(a, b);
                break;
            case "subtrair":
                resultado = calculadora.subtrair(a, b);
                break;
            case "multiplicar":
                resultado = calculadora.multiplicar(a, b);
                break;
            case "dividir":
                resultado = calculadora.dividir(a, b);
                break;
            default:
                throw new IllegalArgumentException("Operação desconhecida");
        }
        
        Assert.assertEquals(resultado, esperado);
    }
}
```

## Principais Erros

### 1. **Testar implementação em vez de comportamento**
```java
// ❌ Errado
@Test
void testSoma() {
    Calculadora calc = new Calculadora();
    int resultado = calc.somar(2, 3);
    // Testa implementação interna
}

// ✅ Correto
@Test
void testSomaDeveRetornarCinco() {
    assertEquals(5, calculadora.somar(2, 3));
}
```

### 2. **Testes muito acoplados**
```java
// ❌ Errado
@Test
void testCriarEListar() {
    service.criar(usuario);
    List<Usuario> usuarios = service.listar();
    assertEquals(1, usuarios.size());
}

// ✅ Correto
@Test
void testCriar() {
    Usuario criado = service.criar(usuario);
    assertNotNull(criado.getId());
}

@Test
void testListar() {
    service.criar(usuario);
    List<Usuario> usuarios = service.listar();
    assertEquals(1, usuarios.size());
}
```

### 3. **Não limpar estado entre testes**
```java
// ❌ Errado
private static List<Usuario> usuarios = new ArrayList<>();

@Test
void test1() {
    usuarios.add(new Usuario("João"));
}

@Test
void test2() {
    // usuarios ainda tem dados do test1
}

// ✅ Correto
@BeforeEach
void setUp() {
    usuarios = new ArrayList<>();
}
```

### 4. **Testes muito lentos**
```java
// ❌ Errado
@Test
void testComBancoDados() {
    // Acessa banco de dados real
    usuario = repository.save(usuario);
}

// ✅ Correto
@Test
void testComMock() {
    when(repository.save(any())).thenReturn(usuario);
    service.criar(usuario);
}
```

## Melhores Práticas

### 1. **Use padrão AAA (Arrange, Act, Assert)**
```java
@Test
void testSoma() {
    // Arrange
    int a = 2, b = 3;
    
    // Act
    int resultado = calculadora.somar(a, b);
    
    // Assert
    assertEquals(5, resultado);
}
```

### 2. **Nomes descritivos**
```java
// ✅ Bom
@Test
void testSomaDeveRetornarCincoQuandoSomaDoisMaisTres() {
    assertEquals(5, calculadora.somar(2, 3));
}

// ❌ Evitar
@Test
void test1() {
    assertEquals(5, calculadora.somar(2, 3));
}
```

### 3. **Um assert por teste (quando possível)**
```java
// ✅ Bom
@Test
void testSoma() {
    assertEquals(5, calculadora.somar(2, 3));
}

@Test
void testSubtracao() {
    assertEquals(2, calculadora.subtrair(5, 3));
}

// ❌ Evitar
@Test
void testOperacoes() {
    assertEquals(5, calculadora.somar(2, 3));
    assertEquals(2, calculadora.subtrair(5, 3));
}
```

### 4. **Use fixtures e setup/teardown**
```java
@BeforeEach
void setUp() {
    calculadora = new Calculadora();
}

@AfterEach
void tearDown() {
    calculadora = null;
}
```

### 5. **Teste casos extremos**
```java
@ParameterizedTest
@ValueSource(ints = { 0, -1, Integer.MAX_VALUE, Integer.MIN_VALUE })
void testCasosExtremos(int numero) {
    // Testar comportamento em casos extremos
}
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre JUnit e TestNG?**
JUnit é mais simples e padrão. TestNG é mais flexível com data-driven testing e testes paralelos.

### 2. **O que é TDD?**
Test-Driven Development: escrever testes antes do código. Red → Green → Refactor.

### 3. **Como mockar dependências?**
Use Mockito: `@Mock`, `when()`, `verify()`.

### 4. **Qual a diferença entre teste unitário e integração?**
Unitário testa uma unidade isolada. Integração testa múltiplos componentes juntos.

### 5. **Como testar exceções?**
Use `assertThrows()` em JUnit 5 ou `@Test(expectedExceptions = ...)` em TestNG.

## Relação com Outras Tecnologias

- **Spring Boot:** Testes com `@SpringBootTest`
- **Mockito:** Mocking de dependências
- **AssertJ:** Assertions mais fluentes
- **Selenium:** Testes de UI
- **CI/CD:** Execução automática de testes

---

## Material de Estudo

### Documentação Oficial
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [TestNG Documentation](https://testng.org/doc/)

### Roadmap
- [roadmap.sh - Testing](https://roadmap.sh/testing)

### Cursos
- **Português:** [JUnit e Testes em Java - Udemy](https://www.udemy.com/course/junit-testes-em-java/)
- **Inglês:** [Testing Java with JUnit 5 - Udemy](https://www.udemy.com/course/testing-java-with-junit-5/)

### Playlists YouTube
- **Português:** [JUnit - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO8xWLn2KZZr6q6DmSRR_nAJ)
- **Inglês:** [JUnit 5 Tutorial - Traversy Media](https://www.youtube.com/watch?v=flpmSXVnGMU)

### Livros
- **"Working Effectively with Legacy Code"** - Michael Feathers
- **"Test Driven Development: By Example"** - Kent Beck

### Artigos e Blogs
- [JUnit 5 Best Practices - Baeldung](https://www.baeldung.com/junit-5)
- [TestNG Best Practices - Medium](https://medium.com/tag/testng)

### GitHub Relevante
- [JUnit 5](https://github.com/junit-team/junit5)
- [TestNG](https://github.com/cbeust/testng)
- [Mockito](https://github.com/mockito/mockito)

### Repositórios Exemplo
- [Spring Boot Test Examples](https://github.com/spring-projects/spring-boot/tree/main/spring-boot-samples)

### Projetos para Praticar
1. **Testes unitários** de calculadora
2. **Testes com mock** de serviço
3. **Testes parametrizados** com múltiplos casos
4. **Testes de integração** com banco de dados
5. **Testes paralelos** com TestNG

---

## Certificações

Não há certificações específicas para JUnit/TestNG, mas conhecimento é essencial para:
- Oracle Certified Associate Java Programmer
- AWS Certified Developer Associate
