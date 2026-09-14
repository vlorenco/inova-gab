# Endpoints da API — InovaGAB (Sprint 2)

Base URL local: `http://localhost:8080`
Base URL vista pelo Android Emulator: `http://10.0.2.2:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

Todos os endpoints, exceto `POST /api/auth/login` e `GET /api/health`, exigem o header:

```
Authorization: Bearer <token>
```

---

## Tabela resumida

| # | Método | Rota | Auth | Roles | O que faz |
|---|--------|------|------|-------|-----------|
| 1 | POST | `/api/auth/login` | ❌ | público | Autentica e devolve o JWT |
| 2 | GET | `/api/auth/me` | ✅ | todas | Perfil do dono do token |
| 3 | GET | `/api/health` | ❌ | público | Confirma que a API está no ar |
| 4 | GET | `/api/strategies` | ✅ | OPERADOR, GESTOR, LIDERANCA | Lista orientações (`?activeOnly=true`) |
| 5 | GET | `/api/strategies/{id}` | ✅ | OPERADOR, GESTOR, LIDERANCA | Detalhe da orientação |
| 6 | POST | `/api/strategies` | ✅ | **LIDERANCA** | Cria orientação |
| 7 | PUT | `/api/strategies/{id}` | ✅ | **LIDERANCA** | Atualiza orientação |
| 8 | DELETE | `/api/strategies/{id}` | ✅ | **LIDERANCA** | Exclui orientação |
| 9 | GET | `/api/strategies/{id}/history` | ✅ | **LIDERANCA** | Histórico de alterações |
| 10 | POST | `/api/ideas` | ✅ | **OPERADOR** | Cadastra ideia (dono vem do JWT) |
| 11 | GET | `/api/ideas/my` | ✅ | **OPERADOR** | Lista só as ideias do próprio operador |
| 12 | GET | `/api/ideas` | ✅ | **GESTOR** | Lista todas (`?status=EM_ANALISE`) |
| 13 | GET | `/api/ideas/{id}` | ✅ | OPERADOR (dono), GESTOR | Detalhe da ideia |
| 14 | PUT | `/api/ideas/{id}` | ✅ | **OPERADOR** (dono) | Edita a própria ideia |
| 15 | DELETE | `/api/ideas/{id}` | ✅ | **OPERADOR** (dono) | Exclui a própria ideia |
| 16 | PATCH | `/api/ideas/{id}/priority` | ✅ | **GESTOR** | Define prioridade |
| 17 | PATCH | `/api/ideas/{id}/status` | ✅ | **GESTOR** | Aprova / rejeita / reclassifica |
| 18 | POST | `/api/ideas/{id}/ai-analysis` | ✅ | **GESTOR** | 🤖 Pontuação automática pelo Gemini |
| 19 | GET | `/api/projects` | ✅ | GESTOR, LIDERANCA | Lista projetos |
| 20 | GET | `/api/projects/{id}` | ✅ | GESTOR, LIDERANCA | Detalhe do projeto |
| 21 | POST | `/api/projects` | ✅ | **GESTOR** | Cria projeto (opcionalmente a partir de ideia) |
| 22 | PUT | `/api/projects/{id}` | ✅ | **GESTOR** | Atualiza dados e resultados |
| 23 | DELETE | `/api/projects/{id}` | ✅ | **GESTOR** | Exclui projeto |
| 24 | GET | `/api/dashboard/summary` | ✅ | **LIDERANCA** | Indicadores gerais + ROI |
| 25 | GET | `/api/dashboard/strategies/{id}` | ✅ | **LIDERANCA** | Indicadores por orientação |
| 26 | GET | `/api/dashboard/projects/{id}` | ✅ | **LIDERANCA** | Indicadores por projeto |
| 27 | GET | `/api/ranking` | ✅ | todas | Operadores por pontos (desc) |
| 28 | GET | `/api/ranking/me` | ✅ | todas | Minha posição e pontuação |

---

## Formato padrão de erro

Toda falha responde no mesmo formato:

```json
{
  "timestamp": "2026-09-14T18:15:42.666Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados invalidos na requisicao.",
  "path": "/api/ideas",
  "details": ["title: Titulo e obrigatorio."]
}
```

`details` só aparece em erros de validação de campo.

| Código | Quando acontece |
|---|---|
| 200 OK | Consulta ou atualização bem-sucedida |
| 201 CREATED | Recurso criado (`POST /api/strategies`, `/api/ideas`, `/api/projects`) |
| 204 NO CONTENT | Exclusão bem-sucedida (`DELETE`) |
| 400 BAD REQUEST | Validação de payload ou regra de negócio violada |
| 401 UNAUTHORIZED | Sem token, token inválido/expirado, ou credenciais erradas no login |
| 403 FORBIDDEN | Perfil sem permissão, ou operador tentando acessar ideia de outro |
| 404 NOT FOUND | Registro inexistente, ou `strategyId`/`ideaId` que não existe |
| 409 CONFLICT | Ideia que já virou projeto sendo convertida de novo |
| 503 SERVICE UNAVAILABLE | Gemini indisponível ou `GEMINI_API_KEY` não configurada |
| 500 INTERNAL SERVER ERROR | Apenas erro inesperado |

---

## Detalhamento

### 1. `POST /api/auth/login` — público

**Request**
```json
{ "email": "operador@app.com", "password": "123456" }
```

**200 OK**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresInMs": 86400000,
  "user": {
    "id": "6aa83953abdd8565be94ffe2",
    "name": "Operador Demo",
    "email": "operador@app.com",
    "role": "OPERADOR",
    "points": 0
  }
}
```

**Erros:** `400` e-mail/senha ausentes ou e-mail inválido · `401` credenciais incorretas.

A senha nunca é devolvida em nenhuma resposta.

---

### 2. `GET /api/auth/me` — todas as roles

**200 OK**
```json
{ "id": "...", "name": "Gestor Demo", "email": "gestor@app.com", "role": "GESTOR", "points": 0 }
```

**Erros:** `401` sem token ou token inválido.

Usado pela tela de Perfil do Android (substituiu o Firebase Auth).

---

### 4-5. `GET /api/strategies` · `GET /api/strategies/{id}`

Query param opcional: `activeOnly=true` devolve só as orientações vigentes
(é o que a tela do operador usa).

**200 OK**
```json
[{
  "id": "6aa83986abdd8565be94fff2",
  "title": "Automacao de patio 2026",
  "description": "Reduzir movimentacoes manuais no patio.",
  "date": "01/02/2026",
  "category": "Operacoes",
  "campaign": "Inova 2026",
  "active": true,
  "createdAt": "2026-09-14T18:14:30.979Z",
  "updatedAt": "2026-09-14T18:14:30.979Z",
  "createdBy": "6aa83953abdd8565be94ffe4"
}]
```

---

### 6-8. `POST` / `PUT` / `DELETE /api/strategies` — somente LIDERANCA

**Request** (POST e PUT)
```json
{
  "title": "Automacao de patio 2026",
  "description": "Reduzir movimentacoes manuais no patio.",
  "date": "01/02/2026",
  "category": "Operacoes",
  "campaign": "Inova 2026",
  "active": true
}
```

Validações: `title` obrigatório (≤150), `description` obrigatória (≤2000).

**Respostas:** `201` no POST · `200` no PUT · `204` no DELETE.
**Erros:** `400` validação · `403` operador/gestor tentando escrever · `404` id inexistente.

---

### 9. `GET /api/strategies/{id}/history` — somente LIDERANCA

Um registro é gravado a cada criação, atualização, desativação ou exclusão.
O histórico sobrevive à exclusão da orientação.

**200 OK**
```json
[{
  "id": "6aa83987abdd8565be94fff4",
  "strategyId": "6aa83986abdd8565be94fff2",
  "title": "Automacao de patio 2026",
  "description": "Reduzir movimentacoes manuais e tempo de espera no patio.",
  "active": true,
  "action": "ATUALIZADA",
  "changedAt": "2026-09-14T18:14:31.411Z",
  "changedBy": "6aa83953abdd8565be94ffe4"
}]
```

`action` ∈ `CRIADA` · `ATUALIZADA` · `DESATIVADA` · `EXCLUIDA`.

---

### 10. `POST /api/ideas` — somente OPERADOR

**Request**
```json
{
  "title": "Fila unica de carregamento",
  "problem": "Caminhoes esperam em filas paralelas sem ordem definida.",
  "solution": "App de senha digital com ordem de chegada.",
  "area": "Patio",
  "benefit": "Reducao do tempo de espera e melhor uso das docas.",
  "strategyId": "6aa83986abdd8565be94fff2"
}
```

> **Não existe campo `operatorId` no payload — de propósito.** O dono da ideia é
> sempre extraído do JWT. O aplicativo não escolhe em nome de quem a ideia é criada.

**201 CREATED** — devolve a ideia completa, já com `operatorId`, `operatorName`,
`strategyTitle`, `status: "EM_ANALISE"` e `priority: "NORMAL"`.
Efeito colateral: **+10 pontos** para o operador.

**Erros:** `400` título/problema/solução em branco · `403` gestor ou liderança tentando criar · `404` `strategyId` inexistente.

---

### 11-15. Ideias do próprio operador

| Rota | Comportamento |
|---|---|
| `GET /api/ideas/my` | Devolve apenas as ideias cujo `operatorId` é o do token |
| `GET /api/ideas/{id}` | Operador só enxerga as próprias → `403` na ideia de outro |
| `PUT /api/ideas/{id}` | Só o dono edita; bloqueado se já `APROVADA`/`REJEITADA` (`400`) |
| `DELETE /api/ideas/{id}` | Só o dono exclui; bloqueado se `APROVADA` ou já convertida (`400`) |

```json
{
  "timestamp": "2026-09-14T18:15:04.369Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Voce so pode acessar as suas proprias ideias.",
  "path": "/api/ideas/6aa83996abdd8565be94fff5"
}
```

---

### 12. `GET /api/ideas` — somente GESTOR

Query param opcional `status` ∈ `EM_ANALISE` · `PRIORIZADA` · `APROVADA` · `REJEITADA`.
É o que alimenta as abas de filtro da tela do gestor.

---

### 16-17. `PATCH /api/ideas/{id}/priority` e `/status` — somente GESTOR

```json
{ "priority": "ALTA" }
```
```json
{ "status": "APROVADA" }
```

Regras aplicadas no service:

- Priorizar como `ALTA` uma ideia em `EM_ANALISE` move o status para `PRIORIZADA`.
- Aprovar preenche `approvedAt` e credita **+50 pontos** ao operador — **uma única vez**,
  mesmo que o gestor aprove de novo.
- Ideia já convertida em projeto não muda mais de status (`400`).

---

### 18. `POST /api/ideas/{id}/ai-analysis` — somente GESTOR 🤖

Sem corpo de requisição. O backend monta o prompt com título, problema, solução,
área, benefício e a orientação estratégica vinculada, chama o Gemini e valida a resposta.

**200 OK**
```json
{
  "score": 88,
  "impactScore": 92,
  "feasibilityScore": 80,
  "innovationScore": 85,
  "strategicAlignmentScore": 95,
  "recommendation": "ALTA_PRIORIDADE",
  "summary": "Reduz filas no patio e conversa direto com a orientacao de automacao.",
  "analyzedAt": "2026-09-14T18:48:19.843Z",
  "model": "gemini-2.0-flash"
}
```

A análise fica persistida no documento da ideia (campo `aiAnalysis`) e passa a
vir em todas as consultas dela.

`recommendation` ∈ `ALTA_PRIORIDADE` · `MEDIA_PRIORIDADE` · `BAIXA_PRIORIDADE` · `REVISAR`.
Todas as notas são inteiros de 0 a 100.

**Erros:** `403` operador ou liderança · `404` ideia inexistente ·
`503` `GEMINI_API_KEY` ausente, Gemini fora do ar, ou resposta que não pôde ser
interpretada — sempre com mensagem amigável e **sem derrubar o backend**.

```json
{
  "status": 503,
  "error": "Service Unavailable",
  "message": "Analise por IA indisponivel: configure a variavel de ambiente GEMINI_API_KEY no backend.",
  "path": "/api/ideas/.../ai-analysis"
}
```

---

### 19-23. Projetos

**Request** (POST e PUT)
```json
{
  "name": "Senha digital de patio",
  "description": "Fila unica digital para carregamento.",
  "responsible": "Operacoes Vitoria",
  "status": "EM_ANDAMENTO",
  "currentStage": "Piloto",
  "investment": 20000,
  "financialReturn": 50000,
  "costReduction": 8000,
  "productivityGain": 25,
  "deadline": "30/09/2026",
  "ideaId": "6aa83996abdd8565be94fff5",
  "strategyId": null
}
```

**201 CREATED**
```json
{
  "id": "6aa839b9abdd8565be94fff6",
  "ideaId": "6aa83996abdd8565be94fff5",
  "strategyId": "6aa83986abdd8565be94fff2",
  "strategyTitle": "Automacao de patio 2026",
  "name": "Senha digital de patio",
  "status": "EM_ANDAMENTO",
  "currentStage": "Piloto",
  "investment": 20000.0,
  "financialReturn": 50000.0,
  "costReduction": 8000.0,
  "productivityGain": 25.0,
  "roi": 150.0,
  "deadline": "30/09/2026"
}
```

Quando `ideaId` é informado:

- a ideia precisa estar **APROVADA**, senão `400`;
- a ideia não pode já pertencer a outro projeto, senão `409`;
- a ideia é marcada com `convertedToProject = true` e o operador ganha **+100 pontos** (uma vez);
- se `strategyId` não for informado, o projeto **herda a estratégia da ideia**.

Validações: `name` obrigatório · valores financeiros `>= 0` · `productivityGain` entre 0 e 1000.
`status` ∈ `PLANEJADO` · `EM_ANDAMENTO` · `CONCLUIDO` · `CANCELADO`.

---

### 24. `GET /api/dashboard/summary` — somente LIDERANCA

**200 OK**
```json
{
  "totalProjects": 2,
  "activeProjects": 1,
  "completedProjects": 1,
  "plannedProjects": 0,
  "cancelledProjects": 0,
  "totalInvestment": 18000.0,
  "totalFinancialReturn": 46000.0,
  "profit": 28000.0,
  "roi": 155.55555555555557,
  "totalCostReduction": 11000.0,
  "averageProductivityGain": 16.5,
  "totalIdeas": 2,
  "approvedIdeas": 0,
  "ideasUnderAnalysis": 1,
  "totalStrategies": 4,
  "activeStrategies": 3
}
```

**Cálculo do ROI (feito no backend, não no app):**

```
roi = ((totalFinancialReturn - totalInvestment) / totalInvestment) * 100
```

Investimento zero devolve `roi = 0` — sem capital aplicado não há retorno
percentual a medir, e assim não há divisão por zero.

---

### 25-26. Dashboards específicos — somente LIDERANCA

`GET /api/dashboard/strategies/{strategyId}` agrega apenas as ideias e projetos
vinculados àquela orientação:

```json
{
  "strategyId": "6aa83986abdd8565be94fff2",
  "strategyTitle": "Automacao de patio 2026",
  "active": true,
  "totalIdeas": 1,
  "approvedIdeas": 1,
  "totalProjects": 1,
  "completedProjects": 0,
  "totalInvestment": 20000.0,
  "totalFinancialReturn": 50000.0,
  "profit": 30000.0,
  "roi": 150.0,
  "totalCostReduction": 8000.0,
  "averageProductivityGain": 25.0
}
```

`GET /api/dashboard/projects/{projectId}` devolve os indicadores de um projeto,
já resolvendo os títulos da estratégia e da ideia de origem.

---

### 27-28. Ranking

`GET /api/ranking` — operadores ordenados por pontos, posição já numerada:

```json
[
  { "position": 1, "userId": "...", "name": "Operador Demo", "email": "operador@app.com", "points": 160 },
  { "position": 2, "userId": "...", "name": "Ana Souza", "email": "ana.souza@app.com", "points": 0 }
]
```

`GET /api/ranking/me`:

```json
{ "position": 1, "totalOperators": 2, "userId": "...", "name": "Operador Demo", "points": 160 }
```

Gestor e liderança recebem `position: 0` — não competem no ranking de operadores.

#### Regras de pontuação (centralizadas em `RankingService`)

| Evento | Pontos |
|---|---|
| Cadastrar uma ideia | **+10** |
| Ideia aprovada pelo gestor | **+50** |
| Ideia convertida em projeto | **+100** |

Cada evento credita **uma única vez**. A garantia vem de três marcadores gravados
no próprio documento da ideia (`creationPointsAwarded`, `approvalPointsAwarded`,
`conversionPointsAwarded`): repetir a operação não repete a pontuação.

---

## Roteiro rápido de teste (curl)

```bash
API=http://localhost:8080/api

# login dos três perfis
OP=$(curl -s -X POST $API/auth/login -H "Content-Type: application/json" \
  -d '{"email":"operador@app.com","password":"123456"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
GE=$(curl -s -X POST $API/auth/login -H "Content-Type: application/json" \
  -d '{"email":"gestor@app.com","password":"123456"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
LI=$(curl -s -X POST $API/auth/login -H "Content-Type: application/json" \
  -d '{"email":"lider@app.com","password":"123456"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# operador consulta estratégias e cria ideia
curl -s $API/strategies -H "Authorization: Bearer $OP"
curl -s -X POST $API/ideas -H "Authorization: Bearer $OP" -H "Content-Type: application/json" \
  -d '{"title":"Minha ideia","problem":"Problema real","solution":"Solucao proposta"}'

# operador barrado nos endpoints da liderança
curl -s -o /dev/null -w "%{http_code}\n" $API/dashboard/summary -H "Authorization: Bearer $OP"   # 403

# gestor lista, prioriza e aprova
curl -s $API/ideas -H "Authorization: Bearer $GE"

# liderança consulta indicadores
curl -s $API/dashboard/summary -H "Authorization: Bearer $LI"
```
