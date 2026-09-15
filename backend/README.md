# InovaGAB — Backend

API REST da plataforma de inovação InovaGAB (FIAP / Grupo Águia Branca).
Substitui o Firebase/Firestore usado na Sprint 1.

## Stack

| Item | Versão / escolha |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.16 |
| Build | Maven (com wrapper `mvnw`) |
| Web | Spring Web (MVC) |
| Segurança | Spring Security + JWT (jjwt 0.12.6) + BCrypt |
| Validação | Bean Validation (Jakarta) |
| Persistência | Spring Data MongoDB |
| Banco | MongoDB 7 |
| Documentação | springdoc-openapi (Swagger UI) |
| IA | Google Gemini API (`generateContent`) |

> **Por que Spring Data MongoDB e não JPA/Hibernate?**
> O banco escolhido é NoSQL orientado a documentos. Forçar JPA sobre documentos
> MongoDB seria artificial e traria um mapeamento relacional que não existe aqui.

---

## Pré-requisitos

- **JDK 21** (`java -version` deve mostrar 21.x)
- **MongoDB 7** rodando — via Docker (mais simples) ou instalação local
- Não é preciso instalar o Maven: o wrapper `./mvnw` baixa a versão correta

---

## 1. Subir o MongoDB

**Com Docker (recomendado):**

```bash
cd backend
docker compose up -d
```

Verificar:

```bash
docker ps --filter name=inovagab-mongo
docker exec inovagab-mongo mongosh --quiet --eval "db.runCommand({ping:1}).ok"   # deve imprimir 1
```

**Sem Docker:** instale o MongoDB Community Server e deixe-o ouvindo em
`localhost:27017`. O banco `inovagab` é criado automaticamente no primeiro boot da API.

---

## 2. Configurar as variáveis de ambiente

Nenhuma credencial fica no repositório. Copie `.env.example` como referência:

| Variável | Obrigatória | Padrão | Para que serve |
|---|---|---|---|
| `MONGODB_URI` | não | `mongodb://localhost:27017/inovagab` | Conexão com o MongoDB |
| `JWT_SECRET` | **sim em produção** | chave de desenvolvimento | Assinatura HMAC do JWT — mínimo 32 caracteres |
| `JWT_EXPIRATION_MS` | não | `86400000` (24 h) | Validade do token |
| `GEMINI_API_KEY` | só para a IA | vazio | Chave da API do Gemini |
| `GEMINI_MODEL` | não | `gemini-3.5-flash` | Modelo usado — configurável para não depender de um nome que pode ser depreciado |
| `GEMINI_BASE_URL` | não | `https://generativelanguage.googleapis.com` | Endpoint da API |
| `SERVER_PORT` | não | `8080` | Porta da API |

**Linux/macOS/Git Bash:**

```bash
export JWT_SECRET="troque-por-um-segredo-aleatorio-de-no-minimo-32-caracteres"
export GEMINI_API_KEY="sua-chave-aqui"
```

**PowerShell:**

```powershell
$env:JWT_SECRET = "troque-por-um-segredo-aleatorio-de-no-minimo-32-caracteres"
$env:GEMINI_API_KEY = "sua-chave-aqui"
```

Sem `GEMINI_API_KEY` a API sobe normalmente — apenas o endpoint de análise por IA
responde `503` com mensagem amigável.

---

## 3. Rodar a API

```bash
cd backend
./mvnw spring-boot:run          # Linux / macOS / Git Bash
mvnw.cmd spring-boot:run        # PowerShell / cmd
```

Pronto quando o log mostrar `Started BackendApplication`. Teste:

```bash
curl http://localhost:8080/api/health
# {"service":"inovagab-backend","status":"UP","timestamp":"..."}
```

Gerar o `.jar` executável:

```bash
./mvnw clean package
java -jar target/inovagab-backend-1.0.0.jar
```

---

## 4. Usuários de demonstração

Criados automaticamente no primeiro boot (o seed é **idempotente** — reiniciar não duplica nada).
Senha de todos: `123456`.

| E-mail | Perfil | Pontos iniciais |
|---|---|---|
| `operador@app.com` | OPERADOR | 360 |
| `gestor@app.com` | GESTOR | — |
| `lider@app.com` | LIDERANCA | — |
| `ana.souza@app.com` | OPERADOR | 410 |
| `carlos.nunes@app.com` | OPERADOR | 250 |
| `marcos.vieira@app.com` | OPERADOR | 190 |
| `juliana.prado@app.com` | OPERADOR | 100 |
| `beatriz.lima@app.com` | OPERADOR | 90 |

Os pontos iniciais não são chutados: o seed aplica as regras do `RankingService`
(+10 criar, +50 aprovar, +100 virar projeto) sobre as próprias ideias que ele cria.
As ideias nascem com os marcadores de pontuação já fechados, então nenhum evento
pontua duas vezes quando você usa o app.

### O que mais é criado

| Coleção | Quantidade | Distribuição |
|---|---|---|
| Orientações estratégicas | 7 | 6 vigentes, 1 encerrada (campanha Inova 2025) |
| Ideias | 30 | 10 aprovadas, 4 priorizadas, 14 em análise, 2 rejeitadas |
| Projetos | 12 | 4 em andamento, 5 concluídos, 2 planejados, 1 cancelado |

Onze ideias já vêm com análise de IA gravada (nota, subnotas e justificativa), para
as telas do gestor terem conteúdo sem precisar chamar o Gemini. Seis projetos estão
vinculados à ideia que os originou (`ideaId`); os outros seis foram "cadastrados
direto pelo gestor" e ficam sem origem, que é um caso real do fluxo.

Com essa massa o dashboard da liderança fecha em **R$ 724.000** de investimento,
**R$ 1.685.000** de retorno, **ROI de 133%** e **R$ 375.000** de redução de custos.
O funil afunila de verdade: 30 ideias → 14 em análise → 10 aprovadas.

Nada disso é mock: tudo é CRUD real persistido no MongoDB e editável pelo app.

O texto é acentuado. O arquivo é UTF-8 sem BOM e o `pom.xml` fixa
`project.build.sourceEncoding=UTF-8` — se aparecer `LogÃ­stica` na tela, o
problema é o encoding do terminal ou da IDE, não do seed.

### Recriar a massa do zero

O seed é idempotente por **chave natural**: usuário pelo e-mail, orientação e
ideia pelo título, projeto pelo nome. Rodar de novo só acrescenta o que falta.

A contrapartida é que **mudar um título cria um registro novo em vez de
atualizar o antigo**. Foi o que aconteceu quando os textos foram acentuados:
`"Reduzir tempo de conferencia de cargas"` e `"Reduzir tempo de conferência de
cargas"` são chaves diferentes. Por isso, ao mexer nos títulos do seed, comece
de um banco limpo:

```bash
docker compose down -v && docker compose up -d
```

Ou aponte para outro banco sem apagar nada:

```bash
MONGODB_URI=mongodb://localhost:27017/inovagab_v2 ./mvnw spring-boot:run
```

---

## 5. Swagger UI

Com a API rodando: **http://localhost:8080/swagger-ui.html**

Para testar endpoints protegidos:

1. Abra `POST /api/auth/login` → *Try it out* → use um dos e-mails acima.
2. Copie o valor do campo `token` da resposta.
3. Clique em **Authorize** (cadeado, canto superior direito) e cole **apenas o token**
   (o prefixo `Bearer` é adicionado automaticamente).
4. A partir daí todos os endpoints respeitam o perfil daquele token.

Especificação OpenAPI em JSON: `http://localhost:8080/v3/api-docs`

---

## 6. Perfis e autorização

| Perfil | Pode |
|---|---|
| **OPERADOR** | Consultar estratégias · criar, listar, editar e excluir **as próprias** ideias · consultar perfil e ranking |
| **GESTOR** | Consultar estratégias · listar e filtrar todas as ideias · priorizar · aprovar/rejeitar · solicitar análise da IA · CRUD completo de projetos |
| **LIDERANCA** | CRUD de estratégias · consultar histórico das estratégias · consultar projetos · dashboard e indicadores |

A autorização é aplicada em duas camadas:

1. **`SecurityConfig`** — rota + método HTTP + role.
2. **Services** — regra de dono: um operador que troque o id na URL de
   `GET /api/ideas/{id}` recebe `403`, porque o `operatorId` da ideia é comparado
   com o id do JWT.

O `operatorId` **nunca** vem do corpo da requisição — é sempre lido do token.

---

## 7. Integração com IA (funcionalidade Plus)

**Pontuação e priorização automática das ideias.**

`POST /api/ideas/{id}/ai-analysis` (somente GESTOR) monta um prompt com título,
problema, solução, área impactada, benefício esperado e a orientação estratégica
vinculada, e pede ao Gemini um JSON estruturado:

```json
{
  "score": 88,
  "impactScore": 92,
  "feasibilityScore": 80,
  "innovationScore": 85,
  "strategicAlignmentScore": 95,
  "recommendation": "ALTA_PRIORIDADE",
  "summary": "Reduz filas no patio e conversa direto com a orientacao de automacao."
}
```

A resposta **não é aceita cegamente**. `AiAnalysisService` valida campo a campo:

- notas precisam ser numéricas, e são limitadas à faixa 0–100;
- `recommendation` fora do domínio conhecido cai para um valor derivado do score;
- `summary` ausente recebe texto padrão e é truncado se vier gigante;
- JSON envolvido em cerca de código markdown é desembrulhado;
- resposta que não é JSON, erro HTTP ou timeout viram `503` com mensagem amigável —
  **o backend não cai**.

O resultado é persistido no campo `aiAnalysis` do documento da ideia.

**A chave nunca sai do backend:**

```
Android → nosso backend → Gemini      ✅
Android → Gemini                      ❌ nunca
```

Obtenha uma chave em https://aistudio.google.com/app/apikey e exporte como
`GEMINI_API_KEY` **antes** de iniciar a API.

---

## 8. Regras de pontuação do ranking

Centralizadas em `RankingService`:

| Evento | Pontos |
|---|---|
| Cadastrar uma ideia | +10 |
| Ideia aprovada pelo gestor | +50 |
| Ideia convertida em projeto | +100 |

Cada evento pontua **uma única vez**. Três marcadores no documento da ideia
(`creationPointsAwarded`, `approvalPointsAwarded`, `conversionPointsAwarded`)
garantem que repetir a operação não repita a pontuação.

---

## 9. Testes

```bash
./mvnw test
```

64 testes cobrindo:

| Arquivo | O que valida |
|---|---|
| `JwtServiceTest` | Geração e leitura do token · rejeição de assinatura errada e token corrompido · recusa de segredo curto |
| `ApiSecurityTest` (`@WebMvcTest`) | Login válido e inválido · endpoint protegido sem token · token inválido · operador barrado nos endpoints de liderança · gestor barrado em estratégias e dashboard · liderança sem CRUD de projetos · só o gestor dispara a IA |
| `IdeaServiceTest` | Dono da ideia vindo do JWT · operador bloqueado na ideia de outro (leitura, edição e exclusão) · gestor aprovando · idempotência dos pontos |
| `StrategyServiceTest` | CRUD completo · histórico CRIADA/ATUALIZADA/DESATIVADA/EXCLUIDA · `strategyId` inexistente · vínculo obrigatório · recusa de vínculo novo com orientação não vigente · edição preservada quando a orientação já vinculada foi desativada depois |
| `ProjectServiceTest` | Cálculo do ROI (positivo, negativo e investimento zero) · criação a partir de ideia aprovada · vínculo validado é o herdado da ideia · recusa de ideia não aprovada · conflito de dupla conversão |
| `DashboardServiceTest` | Consolidação do summary · ROI · portfólio vazio · indicadores por estratégia |
| `RankingServiceTest` | Crédito de pontos · numeração das posições · gestor fora do ranking |
| `AiAnalysisServiceTest` | Parsing válido · cerca markdown · notas fora de faixa · `recommendation` desconhecida · JSON inválido · resposta não-JSON |

Os testes de segurança usam o `SecurityConfig` **real** com tokens JWT **reais** —
apenas os services são mockados.

---

## 10. Estrutura

```
backend/
├── pom.xml
├── mvnw / mvnw.cmd              wrapper (não exige Maven instalado)
├── docker-compose.yml           MongoDB local
├── .env.example                 modelo de configuração (sem credenciais reais)
├── README.md
└── src/
    ├── main/java/br/com/fiap/inovagab/backend/
    │   ├── BackendApplication.java
    │   ├── ai/                  GeminiClient · AiAnalysisService · GeminiProperties
    │   ├── config/              OpenApiConfig · DataSeeder
    │   ├── controller/          Auth · Strategy · Idea · Project · Dashboard · Ranking · Health
    │   ├── dto/                 records de request/response por domínio
    │   ├── exception/           ApiError · ApiException + subclasses · GlobalExceptionHandler
    │   ├── model/               documentos MongoDB + enums
    │   ├── repository/          interfaces Spring Data
    │   ├── security/            SecurityConfig · JwtService · filtro · handlers 401/403
    │   └── service/             regras de negócio
    ├── main/resources/application.yml
    └── test/java/...            64 testes
```

Fluxo: **Controller → Service → Repository → MongoDB**.
Nenhuma regra de negócio dentro de controller.

---

## 11. Documentação complementar

- [`../docs/endpoints.md`](../docs/endpoints.md) — tabela completa dos endpoints, payloads e erros
- [`../docs/architecture.md`](../docs/architecture.md) — diagramas de arquitetura (Mermaid)
- [`../README.md`](../README.md) — visão geral do projeto e do aplicativo Android
