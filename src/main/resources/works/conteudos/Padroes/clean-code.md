# Clean Code

## O que é / Para que serve
- **Clean Code:** Conjunto de princípios e práticas para escrever código legível, manutenível, testável e de fácil compreensão.
- **Objetivo:** Reduzir complexidade, facilitar manutenção, diminuir bugs, melhorar colaboração entre desenvolvedores.

## Quando usar / Quando NÃO usar
- **Usar:** Sempre, em qualquer projeto profissional, código que será mantido por tempo.
- **NÃO usar:** Prototipagem rápida (pode sacrificar qualidade), scripts descartáveis (mas mesmo assim, boas práticas ajudam).

## Como funciona
- **Nomes significativos:** Variáveis, funções, classes com nomes claros e descritivos.
- **Funções pequenas:** Cada função faz uma coisa bem, fácil de testar e entender.
- **Comentários úteis:** Explicar o "por quê", não o "o quê" (código já diz o quê).
- **Tratamento de erros:** Usar exceções, não códigos de erro.
- **DRY (Don't Repeat Yourself):** Evitar duplicação de código.
- **KISS (Keep It Simple, Stupid):** Simplicidade é melhor que complexidade.
- **Formatação consistente:** Indentação, espaçamento, convenções de nomenclatura.

## Conceitos Importantes
- **Nomes Significativos:** `getUserById()` vs `getUser()`, `elapsedTimeInDays` vs `d`.
- **Funções Pequenas:** Máximo 20-30 linhas, uma responsabilidade.
- **Parâmetros:** Máximo 3 parâmetros, considerar usar objetos.
- **Sem Side Effects:** Função não deve modificar estado global.
- **Tratamento de Erros:** Usar exceções checked/unchecked apropriadamente.
- **Testes:** Código testável é código limpo.
- **Refatoração:** Melhorar código sem mudar comportamento.
- **Princípios SOLID:** Single Responsibility, Open/Closed, Liskov, Interface Segregation, Dependency Inversion.

## Exemplo de Código

```java
// ❌ Ruim
public class User {
    public String n;
    public String e;
    public int a;
    
    public void p() {
        if (a > 18) {
            System.out.println("Adult");
        }
    }
}

// ✅ Bom
public class User {
    private String name;
    private String email;
    private int age;
    
    public boolean isAdult() {
        return age >= 18;
    }
    
    public void printStatus() {
        if (isAdult()) {
            System.out.println("Adult");
        }
    }
}

// ❌ Ruim - Função grande com múltiplas responsabilidades
public void processOrder(Order order) {
    // Validar
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Order must have items");
    }
    
    // Calcular
    double total = 0;
    for (Item item : order.getItems()) {
        total += item.getPrice() * item.getQuantity();
    }
    
    // Salvar
    database.save(order);
    
    // Enviar email
    emailService.send(order.getCustomer().getEmail(), "Order confirmed");
}

// ✅ Bom - Funções pequenas, cada uma com uma responsabilidade
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotal(order);
    saveOrder(order);
    notifyCustomer(order);
}

private void validateOrder(Order order) {
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Order must have items");
    }
}

private void calculateTotal(Order order) {
    double total = order.getItems().stream()
        .mapToDouble(item -> item.getPrice() * item.getQuantity())
        .sum();
    order.setTotal(total);
}

private void saveOrder(Order order) {
    database.save(order);
}

private void notifyCustomer(Order order) {
    emailService.send(order.getCustomer().getEmail(), "Order confirmed");
}
```

## Principais Erros e Melhores Práticas
- **Erro:** Nomes genéricos (`data`, `temp`, `x`) — difícil entender.
- **Prática:** Usar nomes descritivos que revelam intenção.
- **Erro:** Funções muito grandes — difícil testar e manter.
- **Prática:** Funções pequenas, máximo 20-30 linhas.
- **Erro:** Muitos parâmetros — difícil chamar e entender.
- **Prática:** Máximo 3 parâmetros, usar objetos se necessário.
- **Erro:** Comentários explicando código óbvio — ruído.
- **Prática:** Comentários explicam "por quê", não "o quê".
- **Erro:** Código duplicado — manutenção difícil.
- **Prática:** Extrair código comum em funções reutilizáveis.
- **Erro:** Sem testes — código não é confiável.
- **Prática:** Escrever testes unitários, código testável.

## Perguntas Comuns em Entrevistas
1. O que é Clean Code e por que é importante?
2. Como você nomearia uma variável que armazena a idade de um usuário?
3. Qual é o tamanho ideal de uma função?
4. Como você evitaria duplicação de código?
5. O que é refatoração e quando fazer?
6. Como você trataria erros em Clean Code?
7. Qual é a diferença entre comentários bons e ruins?
8. Como você tornaria código testável?
9. O que é SOLID e como se relaciona com Clean Code?
10. Como você melhoraria um código legado?

## Relação com Outras Tecnologias
- **SOLID:** Princípios que complementam Clean Code.
- **Design Patterns:** Soluções comprovadas para problemas comuns.
- **Testes Unitários:** Código limpo é testável.
- **Refatoração:** Melhorar código mantendo comportamento.
- **Code Review:** Prática para garantir Clean Code.
- **Linters:** Ferramentas para verificar qualidade de código (SonarQube, Checkstyle).
- **IDE:** Ferramentas modernas ajudam a escrever Clean Code.

## Material de Estudo
- https://www.oreilly.com/library/view/clean-code-a/9780136083238/
- https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882
- https://www.youtube.com/watch?v=7EmboKQH8M0
- https://www.baeldung.com/java-clean-code
- https://github.com/ryanmcdermott/clean-code-javascript (conceitos aplicáveis)
- https://www.sonarsource.com/products/sonarqube/
- https://checkstyle.sourceforge.io/
- https://www.refactoring.com/

## Certificações
- **Não há certificação oficial de Clean Code**, mas é coberto em certificações de Java e arquitetura.
- **Oracle Certified Associate Java Programmer:** Inclui boas práticas. Nível intermediário. Custo: ~$245 USD.
- **Spring Professional Certification:** Inclui Clean Code e SOLID. Nível intermediário. Custo: ~$200 USD.
