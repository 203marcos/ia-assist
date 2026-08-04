# Virtual Threads e Kotlin Coroutines

## O que é?

### Virtual Threads (Java 19+)
Threads leves gerenciadas pela JVM. Milhares podem rodar simultaneamente sem overhead.

### Kotlin Coroutines
Funções que podem ser suspensas e retomadas. Permitem programação assíncrona sem callbacks.

## Para que serve?

- Programação assíncrona eficiente
- Operações I/O sem bloqueio
- Escalabilidade com muitas conexões simultâneas
- Código mais legível que callbacks

## Onde é usado?

- Servidores web de alta concorrência
- APIs REST
- Microserviços
- Processamento de eventos
- Aplicações reativas

## Quando usar?

- Para operações I/O (rede, banco de dados)
- Quando você precisa de muita concorrência
- Para melhorar responsividade

## Quando NÃO usar?

- Para CPU-bound (use threads tradicionais)
- Quando simplicidade é mais importante

## Como funciona?

### Virtual Threads

```java
// Antes (threads tradicionais)
new Thread(() -> {
    System.out.println("Executando");
}).start();

// Agora (virtual threads)
Thread.ofVirtual().start(() -> {
    System.out.println("Executando");
});

// Ou com ExecutorService
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
executor.submit(() -> {
    System.out.println("Executando");
});
```

### Kotlin Coroutines

```kotlin
// Simples
launch {
    println("Executando")
}

// Com resultado
val resultado = async {
    fazerAlgo()
}.await()

// Sequencial
launch {
    val dados1 = async { buscarDados1() }.await()
    val dados2 = async { buscarDados2() }.await()
    processar(dados1, dados2)
}
```

## Conceitos Importantes

### Virtual Threads - Carriers
Virtual threads rodam em carrier threads (threads do SO). JVM mapeia automaticamente.

### Virtual Threads - Pinning
Quando uma virtual thread não pode ser suspensa (synchronized, JNI), fica "pinned" ao carrier.

### Kotlin Coroutines - Dispatchers
Especificam em qual thread a coroutine roda:
- `Dispatchers.Main` - Thread principal
- `Dispatchers.IO` - Para I/O
- `Dispatchers.Default` - Para CPU-bound
- `Dispatchers.Unconfined` - Sem restrição

### Kotlin Coroutines - Scopes
Controlam ciclo de vida:
- `GlobalScope` - Vive enquanto app vive (evitar)
- `CoroutineScope` - Escopo customizado
- `viewModelScope` - Android
- `lifecycleScope` - Android

## Exemplos Reais

### Exemplo 1: Virtual Threads em Servidor Web

```java
@RestController
@RequestMapping("/api")
public class DataController {
    
    @GetMapping("/data/{id}")
    public String getData(@PathVariable String id) {
        // Roda em virtual thread
        // Pode fazer I/O sem bloquear
        return service.buscarDados(id);
    }
}

// Configurar Spring para usar virtual threads
@Configuration
public class WebConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.setProtocol("org.apache.coyote.http11.Http11NioProtocol");
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("useVirtualThreads", "true");
        });
    }
}
```

### Exemplo 2: Virtual Threads com ExecutorService

```java
public class VirtualThreadExample {
    
    public static void main(String[] args) throws InterruptedException {
        // Criar executor com virtual threads
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        
        // Submeter 10.000 tarefas
        for (int i = 0; i < 10000; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    // Simular I/O
                    Thread.sleep(1000);
                    System.out.println("Task " + taskId + " concluída");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }
}
```

### Exemplo 3: Kotlin Coroutines Básico

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    // Executar sequencialmente
    launch {
        delay(1000)
        println("Mundo!")
    }
    println("Olá")
    
    // Resultado
    val resultado = async {
        delay(1000)
        42
    }.await()
    println("Resultado: $resultado")
}
```

### Exemplo 4: Kotlin Coroutines com Dispatcher

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    // I/O bound
    launch(Dispatchers.IO) {
        val dados = buscarDadosDoServidor()
        println("Dados: $dados")
    }
    
    // CPU bound
    launch(Dispatchers.Default) {
        val resultado = processarDados()
        println("Resultado: $resultado")
    }
    
    // Main thread
    launch(Dispatchers.Main) {
        atualizarUI()
    }
}

suspend fun buscarDadosDoServidor(): String {
    delay(1000)
    return "Dados do servidor"
}

suspend fun processarDados(): Int {
    return (1..1000000).sum()
}

suspend fun atualizarUI() {
    println("UI atualizada")
}
```

### Exemplo 5: Kotlin Coroutines com Scope

```kotlin
class UsuarioViewModel : ViewModel() {
    
    fun carregarUsuarios() {
        viewModelScope.launch {
            try {
                val usuarios = async(Dispatchers.IO) {
                    usuarioService.listar()
                }.await()
                
                _usuarios.value = usuarios
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }
}
```

## Principais Erros

### 1. **Virtual Threads com synchronized**
```java
// ❌ Errado (pinning)
synchronized void metodo() {
    // Virtual thread fica pinned
}

// ✅ Correto
private final ReentrantLock lock = new ReentrantLock();
void metodo() {
    lock.lock();
    try {
        // Virtual thread pode ser suspensa
    } finally {
        lock.unlock();
    }
}
```

### 2. **Coroutines em GlobalScope**
```kotlin
// ❌ Errado
GlobalScope.launch {
    // Vive enquanto app vive
}

// ✅ Correto
viewModelScope.launch {
    // Morre com ViewModel
}
```

### 3. **Não usar await()**
```kotlin
// ❌ Errado
val resultado = async { buscarDados() }
// resultado é Job, não dados

// ✅ Correto
val resultado = async { buscarDados() }.await()
// resultado são os dados
```

### 4. **Bloquear coroutine**
```kotlin
// ❌ Errado
launch {
    Thread.sleep(1000)  // Bloqueia thread
}

// ✅ Correto
launch {
    delay(1000)  // Suspende, não bloqueia
}
```

## Melhores Práticas

### 1. **Use Virtual Threads para I/O**
```java
// ✅ Bom
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
executor.submit(() -> {
    // I/O operations
});
```

### 2. **Use Coroutines com Dispatchers apropriados**
```kotlin
// ✅ Bom
launch(Dispatchers.IO) {
    val dados = buscarDados()
}
```

### 3. **Use Scopes apropriados**
```kotlin
// ✅ Bom
viewModelScope.launch {
    // Morre com ViewModel
}
```

### 4. **Evite GlobalScope**
```kotlin
// ❌ Evitar
GlobalScope.launch { }

// ✅ Bom
CoroutineScope(Dispatchers.Main).launch { }
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre Virtual Threads e threads tradicionais?**
Virtual threads são leves, gerenciadas pela JVM. Threads tradicionais são pesadas, gerenciadas pelo SO.

### 2. **O que é pinning em Virtual Threads?**
Quando uma virtual thread não pode ser suspensa (synchronized, JNI), fica "pinned" ao carrier thread.

### 3. **Como funcionam Kotlin Coroutines?**
Funções que podem ser suspensas e retomadas. Compilador transforma em máquina de estados.

### 4. **Qual a diferença entre launch e async?**
launch não retorna resultado. async retorna Deferred que pode ser aguardado.

### 5. **O que é Dispatcher?**
Especifica em qual thread a coroutine roda (IO, Default, Main, etc).

## Relação com Outras Tecnologias

- **Spring Boot:** Suporte a Virtual Threads
- **Quarkus/Micronaut:** Suporte a Coroutines
- **Reactive Streams:** Alternativa a Coroutines
- **Project Reactor:** Mono, Flux (alternativa)

---

## Material de Estudo

### Documentação Oficial
- [Virtual Threads - Java 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

### Cursos
- **Português:** [Coroutines em Kotlin - Udemy](https://www.udemy.com/course/coroutines-em-kotlin/)
- **Inglês:** [Kotlin Coroutines - Udemy](https://www.udemy.com/course/kotlin-coroutines/)

### Playlists YouTube
- **Português:** [Coroutines - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO8xWLn2KZZr6q6DmSRR_nAJ)
- **Inglês:** [Virtual Threads - Oracle](https://www.youtube.com/watch?v=lKSSBvzXBRM)

### Artigos e Blogs
- [Virtual Threads - Baeldung](https://www.baeldung.com/java-virtual-threads)
- [Kotlin Coroutines - Baeldung](https://www.baeldung.com/kotlin-coroutines)

### Projetos para Praticar
1. **Servidor web** com Virtual Threads
2. **API reativa** com Coroutines
3. **Processador de eventos** com Coroutines
4. **Comparação** Virtual Threads vs Coroutines
5. **Aplicação Android** com Coroutines
