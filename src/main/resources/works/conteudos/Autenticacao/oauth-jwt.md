# OAuth 2.0 e JWT

## O que é?

### OAuth 2.0
OAuth 2.0 é um protocolo de autorização que permite que usuários concedam acesso a aplicações terceiras sem compartilhar suas credenciais. É o padrão de facto para autenticação em APIs modernas.

### JWT (JSON Web Token)
JWT é um padrão de token compacto e auto-contido para transmitir informações entre partes. Frequentemente usado com OAuth 2.0 para representar claims (permissões) do usuário.

## Para que serve?

### OAuth 2.0
- Permitir login com redes sociais (Google, GitHub, Facebook)
- Autorizar acesso a recursos protegidos
- Integrar aplicações terceiras
- Delegar autenticação a provedores especializados

### JWT
- Representar identidade e permissões do usuário
- Transmitir informações de forma segura e verificável
- Implementar autenticação stateless
- Facilitar comunicação entre microserviços

## Onde é usado?

- Aplicações web e mobile
- APIs REST
- Microserviços
- Single Sign-On (SSO)
- Integração com redes sociais
- Plataformas SaaS

## Quando usar?

### OAuth 2.0
- Quando você quer permitir login com redes sociais
- Para integração com serviços terceiros
- Quando você quer delegar autenticação
- Em aplicações distribuídas

### JWT
- Para autenticação stateless
- Em APIs REST
- Para comunicação entre microserviços
- Quando você quer evitar sessões no servidor

## Quando NÃO usar?

### OAuth 2.0
- Para autenticação simples (username/password é suficiente)
- Quando você controla totalmente a autenticação

### JWT
- Para dados sensíveis (JWT é apenas encoded, não encrypted)
- Quando você precisa revogar tokens instantaneamente
- Para armazenar informações muito grandes

## Como funciona?

### OAuth 2.0 - Authorization Code Flow

```
1. Usuário clica "Login com Google"
2. App redireciona para Google (com client_id, redirect_uri, scope)
3. Usuário faz login no Google
4. Google redireciona para app com authorization_code
5. App troca code por access_token (backend)
6. App usa access_token para acessar recursos do usuário
```

### JWT - Estrutura

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikpvw6NvIiwiaWF0IjoxNTE2MjM5MDIyfQ.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

Três partes separadas por `.`:
1. **Header:** Algoritmo e tipo de token
2. **Payload:** Claims (dados do usuário)
3. **Signature:** Assinatura para verificar integridade

## Conceitos Importantes

### OAuth 2.0 - Fluxos

#### Authorization Code Flow
Mais seguro, usado por aplicações web tradicionais.

#### Implicit Flow
Deprecated, não use.

#### Client Credentials Flow
Para comunicação servidor-servidor.

#### Resource Owner Password Credentials Flow
Para aplicações confiáveis (mobile, desktop).

#### Refresh Token Flow
Permite renovar access_token expirado sem re-autenticar.

### JWT - Claims

```json
{
  "sub": "1234567890",        // Subject (ID do usuário)
  "name": "João Silva",        // Nome
  "email": "joao@email.com",   // Email
  "iat": 1516239022,           // Issued At (quando foi criado)
  "exp": 1516242622,           // Expiration (quando expira)
  "iss": "https://api.exemplo.com",  // Issuer
  "aud": "app-cliente"         // Audience
}
```

### Algoritmos de Assinatura

- **HS256:** HMAC com SHA-256 (simétrico, chave compartilhada)
- **RS256:** RSA com SHA-256 (assimétrico, chave privada/pública)
- **ES256:** ECDSA com SHA-256 (assimétrico, mais eficiente)

## Exemplos Reais

### Exemplo 1: OAuth 2.0 com Spring Security

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/home")
            );
        return http.build();
    }
}
```

```yaml
# application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid,profile,email
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope: user:email
```

### Exemplo 2: JWT com Spring Security

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }
}
```

```java
@Component
public class JwtTokenProvider {
    
    @Value("${app.jwtSecret}")
    private String jwtSecret;
    
    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;
    
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        return Jwts.builder()
            .setSubject(Long.toString(userPrincipal.getId()))
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
        
        return Long.parseLong(claims.getSubject());
    }
    
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            return false;
        } catch (MalformedJwtException ex) {
            return false;
        } catch (ExpiredJwtException ex) {
            return false;
        } catch (UnsupportedJwtException ex) {
            return false;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
```

### Exemplo 3: JWT Filter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromJWT(jwt);
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### Exemplo 4: Refresh Token

```java
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();
    
    if (refreshTokenRepository.existsByToken(requestRefreshToken)) {
        RefreshToken refreshToken = refreshTokenRepository
            .findByToken(requestRefreshToken)
            .orElseThrow(() -> new TokenRefreshException(
                requestRefreshToken, "Refresh token not found"
            ));
        
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException(
                requestRefreshToken, "Refresh token was expired"
            );
        }
        
        User user = refreshToken.getUser();
        String token = tokenProvider.generateTokenFromUserId(user.getId());
        
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    } else {
        throw new TokenRefreshException(
            requestRefreshToken, "Refresh token is not in database"
        );
    }
}
```

## Principais Erros

### 1. **Armazenar token em localStorage (XSS vulnerability)**
```javascript
// ❌ Errado
localStorage.setItem('token', jwtToken);

// ✅ Correto
// Armazenar em httpOnly cookie
response.cookie('token', jwtToken, { httpOnly: true, secure: true });
```

### 2. **Não validar assinatura do JWT**
```java
// ❌ Errado
String[] parts = token.split("\\.");
String payload = new String(Base64.getDecoder().decode(parts[1]));

// ✅ Correto
Claims claims = Jwts.parser()
    .setSigningKey(jwtSecret)
    .parseClaimsJws(token)
    .getBody();
```

### 3. **Usar HS256 com chave fraca**
```java
// ❌ Errado
String jwtSecret = "secret";

// ✅ Correto
String jwtSecret = "sua-chave-secreta-muito-longa-e-aleatoria-com-256-bits";
```

### 4. **Não verificar expiração do token**
```java
// ❌ Errado
Claims claims = Jwts.parser()
    .setSigningKey(jwtSecret)
    .parseClaimsJws(token)
    .getBody();

// ✅ Correto (já verifica automaticamente)
Claims claims = Jwts.parser()
    .setSigningKey(jwtSecret)
    .parseClaimsJws(token)
    .getBody();
```

### 5. **Colocar dados sensíveis no JWT**
```json
// ❌ Errado
{
  "sub": "123",
  "senha": "senha123",
  "numero_cartao": "1234-5678-9012-3456"
}

// ✅ Correto
{
  "sub": "123",
  "email": "joao@email.com",
  "roles": ["USER", "ADMIN"]
}
```

## Melhores Práticas

### 1. **Use HTTPS sempre**
```
✅ https://api.exemplo.com
❌ http://api.exemplo.com
```

### 2. **Armazene tokens em httpOnly cookies**
```java
response.addCookie(new Cookie("token", jwtToken) {{
    setHttpOnly(true);
    setSecure(true);
    setPath("/");
    setMaxAge(3600);
}});
```

### 3. **Use RS256 para múltiplos serviços**
```java
// ✅ Bom para microserviços
String token = Jwts.builder()
    .setSubject(userId)
    .signWith(SignatureAlgorithm.RS256, privateKey)
    .compact();

// Verificar com chave pública
Jwts.parser()
    .setSigningKey(publicKey)
    .parseClaimsJws(token);
```

### 4. **Implemente refresh tokens**
```
Access Token: Curta duração (15 min)
Refresh Token: Longa duração (7 dias)
```

### 5. **Valide sempre no backend**
```java
// ✅ Bom
@PostMapping("/dados-sensivel")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> getDados() {
    // Validação automática via Spring Security
    return ResponseEntity.ok(dados);
}
```

## Perguntas Comuns em Entrevistas

### 1. **Qual a diferença entre OAuth 2.0 e JWT?**
OAuth 2.0 é um protocolo de autorização. JWT é um formato de token. Frequentemente usados juntos.

### 2. **Como funciona o Authorization Code Flow?**
Usuário é redirecionado para provedor, faz login, recebe código, app troca código por token.

### 3. **JWT é seguro?**
JWT é apenas encoded, não encrypted. Não coloque dados sensíveis. Use HTTPS sempre.

### 4. **Como revogar um JWT?**
JWTs não podem ser revogados instantaneamente. Use blacklist ou short expiration.

### 5. **Qual a diferença entre HS256 e RS256?**
HS256 é simétrico (mesma chave para assinar e verificar). RS256 é assimétrico (chave privada para assinar, pública para verificar).

### 6. **Como implementar logout com JWT?**
Remova token do cliente (localStorage/cookie). Opcionalmente, mantenha blacklist no servidor.

## Relação com Outras Tecnologias

- **Spring Security:** Implementação de OAuth e JWT
- **APIs REST:** Autenticação de requisições
- **Microserviços:** Comunicação segura entre serviços
- **Docker:** Containeriza aplicações com autenticação
- **AWS:** Cognito para OAuth/JWT gerenciado

---

## Material de Estudo

### Documentação Oficial
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [JWT RFC 7519](https://tools.ietf.org/html/rfc7519)
- [Spring Security OAuth2](https://spring.io/projects/spring-security-oauth)

### Roadmap
- [roadmap.sh - OAuth](https://roadmap.sh/guides/oauth-2-0)

### Cursos
- **Português:** [OAuth 2.0 e JWT - Udemy](https://www.udemy.com/course/oauth-2-0-e-jwt/)
- **Inglês:** [OAuth 2.0 and OpenID Connect - Udemy](https://www.udemy.com/course/oauth-2-0-and-openid-connect/)

### Playlists YouTube
- **Português:** [OAuth 2.0 - Código Fonte TV](https://www.youtube.com/playlist?list=PLXik_5Br-zO8xWLn2KZZr6q6DmSRR_nAJ)
- **Inglês:** [OAuth 2.0 Tutorial - Traversy Media](https://www.youtube.com/watch?v=996OiexHze0)

### Livros
- **"OAuth 2.0 Simplified"** - Aaron Parecki
- **"API Security in Action"** - Neil Madden

### Artigos e Blogs
- [OAuth 2.0 Best Practices - IETF](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-security-topics)
- [JWT Best Practices - Auth0](https://auth0.com/blog/critical-vulnerabilities-in-json-web-token-libraries/)

### GitHub Relevante
- [jjwt - Java JWT Library](https://github.com/jwtk/jjwt)
- [Spring Security OAuth2](https://github.com/spring-projects/spring-security-oauth)

### Repositórios Exemplo
- [Spring Boot OAuth2 Examples](https://github.com/spring-projects/spring-boot/tree/main/spring-boot-samples)

### Projetos para Praticar
1. **Login com Google** usando OAuth 2.0
2. **API com JWT** e Spring Security
3. **Refresh Token** implementation
4. **Multi-tenant** com OAuth
5. **Microserviços** com JWT

---

## Certificações

Não há certificações específicas para OAuth/JWT, mas conhecimento é essencial para:
- AWS Certified Developer Associate
- Oracle Certified Associate Java Programmer
- Kubernetes Application Developer (CKA)
