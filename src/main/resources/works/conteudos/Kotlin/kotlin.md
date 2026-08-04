# Kotlin

## O que é?
Kotlin é uma linguagem de programação moderna que roda na JVM (Java Virtual Machine), desenvolvida pela JetBrains. É totalmente interoperável com Java e oferece sintaxe mais concisa, segurança de tipos aprimorada e recursos funcionais.

## Para que serve?
Kotlin serve para desenvolver aplicações que rodam na JVM com código mais limpo e seguro que Java. É especialmente útil para:
- Aplicações Android (linguagem oficial desde 2019)
- Microserviços
- Aplicações web
- Scripts e automação

## Onde é usado?
- Android (linguagem oficial)
- Backend com Spring Boot, Quarkus, Micronaut
- Scripting e automação
- Processamento de dados
- Empresas como Google, Netflix, Pinterest, Uber

## Quando usar?
- Quando você quer sintaxe mais concisa que Java
- Para novos projetos na JVM
- Quando trabalha com Android
- Para equipes que valorizam segurança de tipos e null-safety

## Quando NÃO usar?
- Quando a equipe não tem experiência com Kotlin (curva de aprendizado)
- Projetos legados em Java puro (migração pode ser complexa)
- Quando performance é crítica (Kotlin tem overhead mínimo, mas existe)

## Como funciona?

### Compilação
Kotlin compila para bytecode Java, assim como Java. É totalmente interoperável:
```
Código Kotlin (.kt) → Compilador Kotlin → Bytecode (.class) → JVM
```

### Principais Características

#### 1. **Null Safety**
Kotlin diferencia tipos nullable e non-nullable:
```kotlin
val nome: String = "João"        // Não pode ser null
val sobrenome: String? = null    // Pode ser null

// Acesso seguro
val tamanho = sobrenome?.length ?: 0  // Elvis operator
```

#### 2. **Extension Functions**
Adiciona métodos a classes existentes:
```kotlin
fun String.isPalindromo(): Boolean {
    return this == this.reversed()
}

"aba".isPalindromo() // true
```

#### 3. **Data Classes**
Simplificam criação de classes com dados:
```kotlin
data class Usuario(val email: String, val idade: Int)

val user = Usuario("marcos@email.com", 25)
println(user) // Usuario(email=marcos@email.com, idade=25)
```

#### 4. **Lambdas e Higher-Order Functions**
Suporte nativo a programação funcional:
```kotlin
val numeros = listOf(1, 2, 3, 4, 5)
val pares = numeros.filter { it % 2 == 0 }
val dobrados = numeros.map { it * 2 }
```

#### 5. **Coroutines**
Programação assíncrona simplificada:
```kotlin
launch {
    val resultado = async { fazerRequisicao() }.await()
    processar(resultado)
}
```

#### 6. **Scope Functions**
Executam bloco de código no contexto de um objeto:
```kotlin
Usuario("marcos@email.com", 25).apply {
    println("Email: $email")
    println("Idade: $idade")
}
```

## Conceitos Importantes

### Imutabilidade
```kotlin
val x = 10      // Imutável (val)
var y = 20      // Mutável (var)
```

### Smart Casts
Kotlin automaticamente faz cast após verificação:
```kotlin
if (obj is String) {
    println(obj.length) // obj já é String aqui
}
```

### Destructuring
Desempacota valores:
```kotlin
val (email, idade) = usuario
```

### Sealed Classes
Restringe herança:
```kotlin
sealed class Resultado
data class Sucesso(val dados: String) : Resultado()
data class Erro(val mensagem: String) : Resultado()
```

## Exemplos Reais

### Exemplo 1: Data Class com Null Safety
```kotlin
data class Pessoa(
    val nome: String,
    val email: String?,
    val idade: Int
)

fun enviarEmail(pessoa: Pessoa) {
    val email = pessoa.email ?: run {
        println("Email não fornecido")
        return
    }
    println("Enviando para $email")
}
```

### Exemplo 2: Extension Function
```kotlin
fun <T> List<T>.segundoOuNull(): T? = if (size > 1) this[1] else null

val numeros = listOf(1, 2, 3)
println(numeros.segundoOuNull()) // 2
```

### Exemplo 3: Coroutine Simples
```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        delay(1000)
        println("Mundo!")
    }
    println("Olá")
}
// Output: Olá, Mundo!
```

### Exemplo 4: Sealed Class com When
```kotlin
sealed class HttpResponse
data class Success(val body: String) : HttpResponse()
data class Error(val code: Int, val message: String) : HttpResponse()

fun handleResponse(response: HttpResponse) {
    when (response) {
        is Success -> println("Sucesso: ${response.body}")
        is Error -> println("Erro ${response.code}: ${response.message}")
    }
}
```

## Principais Erros

### 1. **Confundir val com var**
```kotlin
val lista = mutableListOf(1, 2, 3)
lista.add(4) // ✅ Funciona (lista é mutável)
lista = mutableListOf(5) // ❌ Erro (val não pode ser reatribuído)
```

### 2. **Esquecer ? em tipos nullable**
```kotlin
val nome: String = null // ❌ Erro de compilação
val nome: String? = null // ✅ Correto
```

### 3. **Usar !! sem cuidado**
```kotlin
val email: String? = null
println(email!!.length) // ❌ NullPointerException em runtime
```

### 4. **Misturar Kotlin e Java incorretamente**
```kotlin
// ❌ Evitar
val lista = java.util.ArrayList<String>()

// ✅ Preferir
val lista = mutableListOf<String>()
```

## Melhores Práticas

### 1. **Use val por padrão**
```kotlin
// ✅ Bom
val usuario = Usuario("marcos@email.com", 25)

// ❌ Evitar
var usuario = Usuario("marcos@email.com", 25)
```

### 2. **Prefira Safe Calls e Elvis Operator**
```kotlin
// ✅ Bom
val tamanho = email?.length ?: 0

// ❌ Evitar
val tamanho = if (email != null) email.length else 0
```

### 3. **Use Data Classes para DTOs**
```kotlin
// ✅ Bom
data class UsuarioDTO(val email: String, val idade: Int)

// ❌ Evitar
class UsuarioDTO(val email: String, val idade: Int)
```

### 4. **Aproveite Scope Functions**
```kotlin
// ✅ Bom
Usuario("marcos@email.com", 25).apply {
    println("Criado: $email")
}.also { usuario ->
    salvarNoBanco(usuario)
}

// ❌ Evitar
val usuario = Usuario("marcos@email.com", 25)
println("Criado: ${usuario.email}")
salvarNoBanco(usuario)
```

### 5. **Use Coroutines para Assincronismo**
```kotlin
// ✅ Bom
launch {
    val dados = async { buscarDados() }.await()
    processar(dados)
}

// ❌ Evitar
Thread {
    val dados = buscarDados()
    processar(dados)
}.start()
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre val e var?**
- `val`: Imutável (read-only), não pode ser reatribuído
- `var`: Mutável, pode ser reatribuído

### 2. **O que é Null Safety em Kotlin?**
Kotlin diferencia tipos nullable (`String?`) de non-nullable (`String`), prevenindo NullPointerException em tempo de compilação.

### 3. **Como funcionam Coroutines?**
Coroutines são funções que podem ser suspensas e retomadas, permitindo programação assíncrona sem callbacks. São mais leves que threads.

### 4. **O que é Elvis Operator?**
O operador `?:` retorna o valor à esquerda se não for null, caso contrário retorna o valor à direita: `email ?: "sem-email"`

### 5. **Qual a diferença entre apply, let, run, with e also?**
- `apply`: Retorna o objeto, útil para inicialização
- `let`: Retorna o resultado do bloco, útil para transformações
- `run`: Retorna o resultado do bloco, útil para operações
- `with`: Retorna o resultado do bloco, útil para múltiplas operações
- `also`: Retorna o objeto, útil para side effects

### 6. **Kotlin é mais rápido que Java?**
Não. Ambos compilam para bytecode e rodam na JVM. Performance é similar. Kotlin pode ter overhead mínimo de compilação.

## Relação com Outras Tecnologias

- **Java:** Kotlin roda na JVM e é totalmente interoperável com Java
- **Spring Boot:** Suporte nativo a Kotlin
- **Quarkus/Micronaut:** Suportam Kotlin
- **Android:** Linguagem oficial desde 2019
- **Coroutines:** Alternativa a Virtual Threads do Java
- **Docker:** Containeriza aplicações Kotlin

---

## Material de Estudo

### Documentação Oficial
- [Kotlin Official Documentation](https://kotlinlang.org/docs/)
- [Kotlin Playground](https://play.kotlinlang.org/)

### Roadmap
- [roadmap.sh - Kotlin](https://roadmap.sh/kotlin)

### Cursos
- **Português:** [Kotlin para Android - Udemy](https://www.udemy.com/course/kotlin-para-android/)
- **Inglês:** [Kotlin for Java Developers - Coursera](https://www.coursera.org/learn/kotlin-for-java-developers)

### Playlists YouTube
- **Português:** [Kotlin Básico - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO-AU-CyHLz7E7VmMRWPEKZJ)
- **Inglês:** [Kotlin Tutorial - Traversy Media](https://www.youtube.com/playlist?list=PLillGF-RfqbZ7s3t6ZInY3TjEw77zYSVJ)

### Livros
- **"Kotlin in Action"** - Dmitry Jemerov, Svetlana Isakova
- **"Programming Kotlin"** - Stephen Samuel, Stefan Bocutiu

### Artigos e Blogs
- [Kotlin Blog Official](https://blog.jetbrains.com/kotlin/)
- [Baeldung - Kotlin](https://www.baeldung.com/kotlin)
- [Medium - Kotlin Tag](https://medium.com/tag/kotlin)

### GitHub Relevante
- [Kotlin Official](https://github.com/JetBrains/kotlin)
- [Awesome Kotlin](https://github.com/KotlinBy/awesome-kotlin)

### Repositórios Exemplo
- [Spring Boot Kotlin Examples](https://github.com/spring-projects/spring-boot/tree/main/spring-boot-samples)
- [Kotlin Coroutines Examples](https://github.com/Kotlin/kotlinx.coroutines/tree/master/examples)

### Projetos para Praticar
1. **Conversor de moedas** com Coroutines
2. **API REST** com Spring Boot e Kotlin
3. **Aplicação de notas** com persistência
4. **Web scraper** com Coroutines
5. **Microserviço** com Quarkus e Kotlin

---

## Certificações

### Kotlin Certified Associate Developer
- **Nível:** Associate (Iniciante/Intermediário)
- **Preço:** ~$99 USD
- **Vale a pena?** Sim, especialmente se trabalha com Kotlin profissionalmente.
- **Ordem ideal:** Após dominar fundamentos de Kotlin

### Observação
Certificações Kotlin são menos comuns que Java, mas agregam valor. Experiência prática é mais importante.
