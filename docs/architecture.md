# Arquitetura — InovaGAB (Sprint 2)

Projeto FIAP / Grupo Águia Branca. A Sprint 2 removeu a dependência de
Firebase/Firestore e passou o aplicativo Android a consumir um backend REST
próprio, em Spring Boot, com MongoDB e integração com o Google Gemini.

---

## 1. Visão geral

```mermaid
flowchart TD
    subgraph ANDROID["📱 Aplicativo Android — Kotlin + Jetpack Compose"]
        UI["Telas Compose<br/>Operador · Gestor · Liderança"]
        VM["ViewModels / estado de tela"]
        REPO["Repositories<br/>Auth · Strategy · Idea · Project · Dashboard · Ranking"]
        RETRO["Retrofit + OkHttp<br/>AuthInterceptor"]
        TOKEN["TokenManager<br/>(DataStore: JWT local)"]
    end

    subgraph BACKEND["☁️ Backend — Spring Boot 3.5 · Java 21"]
        FILTER["JwtAuthenticationFilter"]
        SEC["Spring Security<br/>autorização por rota + role"]
        CTRL["Controllers<br/>/api/auth · /api/strategies · /api/ideas<br/>/api/projects · /api/dashboard · /api/ranking"]
        SVC["Services<br/>regras de negócio"]
        AI["AiAnalysisService<br/>+ GeminiClient"]
        REPOS["Repositories<br/>Spring Data MongoDB"]
        EX["GlobalExceptionHandler<br/>formato único de erro"]
    end

    DB[("🍃 MongoDB<br/>users · strategies · strategy_history<br/>ideas · projects")]
    GEMINI["🤖 Google Gemini API<br/>generateContent"]

    UI --> VM --> REPO --> RETRO
    TOKEN -. "Bearer TOKEN" .-> RETRO
    RETRO -- "HTTPS/HTTP · REST/JSON" --> FILTER
    FILTER --> SEC --> CTRL --> SVC
    SVC --> REPOS --> DB
    SVC --> AI -- "HTTPS + GEMINI_API_KEY" --> GEMINI
    CTRL -. erros .-> EX

    style ANDROID fill:#eef4ff,stroke:#003b7a
    style BACKEND fill:#e9f7ef,stroke:#16794a
    style DB fill:#fff7e6,stroke:#b26a00
    style GEMINI fill:#f6eeff,stroke:#6b3fa0
```

**Regra de ouro da integração com IA:** a chave do Gemini nunca sai do backend.

```
Android  →  nosso backend  →  Gemini          ✅ implementado
Android  →  Gemini                            ❌ nunca acontece
```

---

## 2. Camadas do backend

```mermaid
flowchart LR
    REQ["Request HTTP"] --> F["JwtAuthenticationFilter<br/>lê Authorization: Bearer"]
    F --> S["SecurityConfig<br/>hasRole(OPERADOR/GESTOR/LIDERANCA)"]
    S --> C["Controller<br/>validação Bean Validation"]
    C --> SV["Service<br/>regras de negócio + autorização de dono"]
    SV --> R["Repository<br/>MongoRepository"]
    R --> M[("MongoDB")]
    SV -. exceções de domínio .-> H["GlobalExceptionHandler<br/>ApiError padronizado"]
```

Nenhuma regra de negócio vive no controller: ele valida o payload, chama o
service e devolve o status HTTP correto.

### Pacotes

```
backend/src/main/java/br/com/fiap/inovagab/backend/
├── BackendApplication.java
├── ai/            GeminiProperties · GeminiClient · AiAnalysisService · AiScoreResult
├── config/        OpenApiConfig · DataSeeder (seed idempotente)
├── controller/    Auth · Strategy · Idea · Project · Dashboard · Ranking · Health
├── dto/           auth · strategy · idea · project · dashboard · ranking
├── exception/     ApiError · ApiException + subclasses · GlobalExceptionHandler
├── model/         User · Strategy · StrategyHistory · Idea · Project · AiAnalysis + enums
├── repository/    interfaces Spring Data MongoDB
├── security/      SecurityConfig · JwtService · JwtAuthenticationFilter
│                  AuthenticatedUser · CurrentUser · handlers 401/403
└── service/       Auth · User · Strategy · Idea · Project · Dashboard · Ranking
```

---

## 3. Fluxo de autenticação

```mermaid
sequenceDiagram
    participant A as Android (LoginScreen)
    participant API as POST /api/auth/login
    participant S as AuthService
    participant DB as MongoDB (users)

    A->>API: {email, password}
    API->>S: login()
    S->>DB: findByEmailIgnoreCase
    DB-->>S: User (senha BCrypt)
    S->>S: passwordEncoder.matches()
    alt credenciais válidas
        S-->>API: JWT (sub=userId, role, name, email)
        API-->>A: 200 {token, user}
        A->>A: TokenManager.save(token, role)
        A->>A: navega conforme role
    else inválidas
        S-->>API: BadCredentialsException
        API-->>A: 401 {timestamp, status, error, message, path}
    end
```

Depois do login, **toda** requisição sai do app com
`Authorization: Bearer <token>`, injetado pelo `AuthInterceptor` do OkHttp.

---

## 4. Fluxo da ideia: do operador ao projeto

```mermaid
sequenceDiagram
    participant OP as Operador
    participant GE as Gestor
    participant API as Backend
    participant G as Gemini
    participant DB as MongoDB

    OP->>API: POST /api/ideas (+strategyId)
    Note over API: operatorId vem do JWT,<br/>nunca do corpo da requisição
    API->>DB: salva ideia (EM_ANALISE)
    API->>DB: users.points += 10

    GE->>API: GET /api/ideas
    GE->>API: POST /api/ideas/{id}/ai-analysis
    API->>G: prompt com ideia + estratégia vinculada
    G-->>API: JSON estruturado
    Note over API: valida tipo, faixa 0-100<br/>e domínio de recommendation
    API->>DB: grava aiAnalysis na ideia

    GE->>API: PATCH /api/ideas/{id}/priority {ALTA}
    GE->>API: PATCH /api/ideas/{id}/status {APROVADA}
    API->>DB: users.points += 50 (uma única vez)

    GE->>API: POST /api/projects {ideaId}
    API->>DB: cria projeto + convertedToProject = true
    API->>DB: users.points += 100 (uma única vez)
```

---

## 5. Autorização por perfil

```mermaid
flowchart TD
    subgraph OPERADOR
        O1["GET  /api/strategies"]
        O2["POST /api/ideas"]
        O3["GET/PUT/DELETE /api/ideas/{id} — só as próprias"]
        O4["GET  /api/ideas/my"]
        O5["GET  /api/ranking · /api/ranking/me"]
        O6["GET  /api/auth/me"]
    end
    subgraph GESTOR
        G1["GET  /api/strategies"]
        G2["GET  /api/ideas (+filtro por status)"]
        G3["PATCH /api/ideas/{id}/priority · /status"]
        G4["POST /api/ideas/{id}/ai-analysis"]
        G5["CRUD /api/projects"]
    end
    subgraph LIDERANCA
        L1["CRUD /api/strategies"]
        L2["GET  /api/strategies/{id}/history"]
        L3["GET  /api/projects (somente leitura)"]
        L4["GET  /api/dashboard/**"]
    end
```

A autorização é aplicada em **duas camadas**:

1. **`SecurityConfig`** — rota + método HTTP + role. Um operador que chame
   `GET /api/dashboard/summary` recebe `403` antes de qualquer código de negócio rodar.
2. **Service** — regra de dono. Um operador que troque o id na URL de
   `GET /api/ideas/{id}` recebe `403` porque `IdeaService` compara o
   `operatorId` da ideia com o id do JWT.

Esconder um botão no Android **não** é considerado segurança em nenhum ponto do projeto.

---

## 6. Modelo de dados (MongoDB)

```mermaid
erDiagram
    USERS ||--o{ IDEAS : "cria (operatorId)"
    STRATEGIES ||--o{ IDEAS : "orienta (strategyId)"
    STRATEGIES ||--o{ PROJECTS : "orienta (strategyId)"
    STRATEGIES ||--o{ STRATEGY_HISTORY : "versiona (strategyId)"
    IDEAS ||--o| PROJECTS : "vira (ideaId)"

    USERS {
        string id PK
        string name
        string email UK
        string password "BCrypt"
        enum   role "OPERADOR|GESTOR|LIDERANCA"
        int    points
        instant createdAt
        instant updatedAt
    }
    STRATEGIES {
        string id PK
        string title
        string description
        string date
        string category
        string campaign
        bool   active
        string createdBy
    }
    STRATEGY_HISTORY {
        string id PK
        string strategyId FK
        string title
        string description
        bool   active
        enum   action "CRIADA|ATUALIZADA|DESATIVADA|EXCLUIDA"
        instant changedAt
        string changedBy
    }
    IDEAS {
        string id PK
        string title
        string problem
        string solution
        string area
        string benefit
        enum   status "EM_ANALISE|PRIORIZADA|APROVADA|REJEITADA"
        enum   priority "BAIXA|NORMAL|ALTA"
        string operatorId FK
        string strategyId FK
        bool   convertedToProject
        object aiAnalysis
    }
    PROJECTS {
        string id PK
        string ideaId FK
        string strategyId FK
        string name
        string responsible
        enum   status "PLANEJADO|EM_ANDAMENTO|CONCLUIDO|CANCELADO"
        string currentStage
        double investment
        double financialReturn
        double costReduction
        double productivityGain
        string deadline
    }
```

---

## 7. Camada de rede no Android

```
app/src/main/java/br/com/fiap/inovagab/
├── InovaGabApplication.kt      inicializa ApiClient e recarrega o JWT salvo
├── data/
│   ├── model/                  modelos de domínio únicos (sem duplicação)
│   ├── remote/
│   │   ├── ApiClient.kt        Retrofit singleton (BASE URL vem do BuildConfig)
│   │   ├── ApiCall.kt          traduz HttpException/IOException em mensagem amigável
│   │   ├── api/                interfaces Retrofit por recurso
│   │   ├── dto/                espelham o JSON + Mappers.kt para o domínio
│   │   └── interceptor/        AuthInterceptor (Bearer token)
│   ├── repository/             Auth · Strategy · Idea · Project · Dashboard · Ranking
│   └── session/TokenManager.kt DataStore + cache em memória para o interceptor
├── navigation/                 Routes · AppNavGraph
└── ui/                         login · operador · gestor · lideranca · profile · components
```

A URL base fica em **um único lugar** (`buildConfigField` em
`app/build.gradle.kts`), com padrão `http://10.0.2.2:8080/` — o endereço do host
visto de dentro do Android Emulator.

---

## 8. Decisões de projeto

| Decisão | Motivo |
|---|---|
| Spring Data MongoDB, não JPA/Hibernate | O banco é NoSQL orientado a documentos; forçar JPA sobre documentos seria artificial. |
| `aiAnalysis` embutido no documento da ideia | Relação 1:1 e sempre lida junto com a ideia — evita um join desnecessário. |
| `strategy_history` em coleção separada | Histórico é append-only e cresce sem limite; manter fora do documento evita estourar o tamanho do documento. |
| Flags `...PointsAwarded` na ideia | Tornam a pontuação idempotente sem precisar de transação distribuída. |
| Resposta do Gemini validada campo a campo | Modelo de linguagem pode devolver texto fora do formato; nota fora de 0-100 é limitada e `recommendation` desconhecida cai para um valor derivado do score. |
| ROI = 0 quando investimento é 0 | Evita divisão por zero: sem capital aplicado não há retorno percentual a medir. |
| DTOs próprios em todas as respostas | O documento `User` nunca chega ao cliente — a senha (mesmo em hash) jamais é serializada. |
