# lyceum

Sistema acadêmico de matrículas composto por dois serviços Spring Boot (`academic` e `notification`), mensageria assíncrona via RabbitMQ, controle de concorrência por lock pessimista, observabilidade básica com Prometheus e frontend web em TypeScript.

## Como rodar o projeto localmente

### Pré-requisitos

- Java 21
- Maven 3.9+
- Node 22+
- Docker + Docker Compose

#### Suba infraestrutura

##### Banco de dados

**Opção A — Docker Compose (recomendado)**
```bash
docker compose up -d postgres
```

O profile `local` já aponta para `localhost:5432`.

**Opção B — PostgreSQL instalado localmente**
Crie os bancos e edite as credenciais nos arquivos `application-db_local.yml` de cada módulo:

```sql
CREATE DATABASE academicdb;
CREATE DATABASE notificationdb;
```

- `academic/src/main/resources/config/application-local.yml`
- `notification/src/main/resources/application-local.yml`

**Opção C — H2 in-memory (sem instalação, para desenvolvimento rápido)**
Descomente a seção H2 no `application-db_local.yml` do módulo desejado e comente a seção PostgreSQL. Veja o próprio arquivo para instruções.

**Opção D — Banco em nuvem**
Edite `application-db_cloud.yml` com host, porta, usuário e senha, ou exporte-os como variáveis de ambiente. Ative o profile `cloud`.

##### Mensageria (RabbitMQ)

**Opção A — Docker Compose (recomendado)**
```bash
docker compose up -d rabbitmq
```

**Opção B — RabbitMQ instalado localmente**
O profile `local` já aponta para `localhost:5672` com as credenciais padrão (`guest/guest`). Edite `application-msg_local.yml` se necessário.

**Opção C — Serviço AMQP em nuvem (ex: CloudAMQP, AWS MQ)**
Descomente e preencha a opção `rabbitmq.addresses` no `application-local.yml` (para uso local) ou `application-msg_cloud.yml` (para deploy).

##### Profiles disponíveis

| Profile | Quando usar | Como ativar | Arquivo editável |
|---|---|---|---|
| `local` (padrão) | Infra na máquina local (opções A, B ou C acima) | nenhuma ação necessária | `application-local.yml` |
| `docker` | Toda a solução via `docker compose up` | `SPRING_PROFILES_ACTIVE=docker` (já no compose) | não editável |
| `cloud` | Deploy em nuvem | `SPRING_PROFILES_ACTIVE=cloud` + variáveis de ambiente | `application-cloud.yml` |


### Backend sem Docker

1. Execute o módulo acadêmico (profile `local` ativado por padrão):
   ```bash
   mvn -pl academic spring-boot:run
   ```
   Para usar outro profile: `mvn -pl academic spring-boot:run -Dspring-boot.run.profiles=docker`
2. Execute o módulo de notificações:
   ```bash
   mvn -pl notification spring-boot:run
   ```

### Depurando via VS Code (launch.json)

O arquivo `.vscode/launch.json` contém configurações prontas para rodar e depurar os serviços diretamente pelo VS Code, sem usar o terminal. As configurações mapeiam os três profiles disponíveis.

#### Backend

| Configuração | Profile | Quando usar |
|---|---|---|
| `Academic - local` | `local` | Infra rodando local ou via Docker Compose na porta padrão |
| `Academic - cloud` | `cloud` | Conectar a banco e mensageria em nuvem; edite os valores de env diretamente no `launch.json` |
| `Notification - local` | `local` | Mesmo cenário do Academic |
| `Notification - cloud` | `cloud` | Mesmo cenário do Academic |

Para o profile **`local`** (padrão), nenhuma variável de ambiente é necessária — os valores vêm do `application-local.yml`. Para o profile **`cloud`**, os valores das variáveis `DB_HOST`, `DB_PASS`, `RABBIT_HOST` etc. devem ser preenchidos diretamente no `launch.json` (ou exportados no shell antes de abrir o VS Code).

> **Nota de segurança:** não comite credenciais reais no `launch.json`. Para ambientes sensíveis, prefira exportar as variáveis no shell ou usar um gerenciador de segredos.

#### Frontend

A única variável de ambiente do frontend é `VITE_API_BASE`, lida em `src/api.ts` via `import.meta.env.VITE_API_BASE`. O valor padrão (`http://localhost:8080`) já está no arquivo `frontend/.env`. A configuração `Frontend - dev` no `launch.json` permite sobrescrever esse valor sem editar o `.env`:

```json
"env": { "VITE_API_BASE": "http://localhost:8080" }
```

Altere o valor caso o backend esteja em outra porta ou endereço. Variáveis definidas no `launch.json` têm precedência sobre o arquivo `.env`.

> O `launch.json` inicia o servidor Vite via `npm run dev`. Para depurar o JavaScript no browser, use a extensão **Playwright** ou o **Chrome Debugger** do VS Code em conjunto.

### Frontend sem Docker

**Pré-requisitos:** Node 22+, backend `academic` em execução (porta 8080).

1. Instale as dependências:
   ```bash
   cd frontend
   npm ci
   ```
2. (Opcional) Configure a URL da API — por padrão aponta para `http://localhost:8080`:
   ```bash
   cp .env.example .env
   # edite VITE_API_BASE se o backend estiver em outra porta
   ```
3. Inicie o servidor de desenvolvimento:
   ```bash
   npm run dev
   ```
   Frontend disponível em `http://localhost:5173`.

> Para gerar o build de produção: `npm run build`. Os artefatos ficam em `dist/` e podem ser servidos com `npm run preview` (porta 4173).

## Como subir a solução com Docker Compose

```bash
docker compose up --build
```

| Serviços | URL | Funçaõ |
|---|---|---|
| Academic API: | `http://localhost:8080` | Backend de domínio, onde rodam as regras de negócio |
| Notification API: | `http://localhost:8081`| Backend de notificações, controle de eventos |
| Frontend: | `http://localhost:4173` |  |
| RabbitMQ Management: | `http://localhost:15672` |  |
| Prometheus: | `http://localhost:9090` |  |

## Como executar os testes automatizados

### Backend

Use `mvn clean` antes de `test` para garantir que o código seja recompilado com o flag `-parameters` (necessário para resolução de `@PathVariable` no Spring MVC).

```bash
mvn clean test -pl academic
mvn clean test -pl notification
```

Ou ambos em paralelo:

```bash
mvn clean test -pl academic,notification
```

### Frontend

Instale as dependências antes da primeira execução:

```bash
cd frontend
npm install
```

**Testes unitários** (Vitest — sem backend, sem browser):

```bash
cd frontend
npm run test:unit
```

**Testes E2E** (Playwright — inicia o servidor de dev automaticamente, sem backend real pois as chamadas de API são interceptadas via `page.route()`):

```bash
cd frontend
npx playwright install chromium   # apenas na primeira vez
npm run test:e2e
```

#### Tipos de testes do frontend

**Testes unitários** (`tests/unit/`, Vitest):
- `buildClassroomLabels.test.ts` — 6 casos: disciplina única sem contador, duas turmas da mesma disciplina recebem `(1)` e `(2)`, contadores independentes por disciplina, fallback para ID quando disciplina ausente
- `api.test.ts` — 5 casos: URL e JSON corretos no GET, `Content-Type` adicionado apenas em POST/PUT/DELETE, erro lança `Error` com a mensagem do servidor, 204 retorna `null`

**Testes E2E** (`tests/e2e/`, Playwright — Chromium):
- `home.spec.ts` — 4 casos: cards visíveis na home, navegação para cada página
- `aluno.spec.ts` — 5 casos: seletor de aluno populado, lista de disciplinas exibida, matrícula com sucesso, botão cancelar para aluno já matriculado, filtro de busca por nome
- `matriculas.spec.ts` — 6 casos: filtros exibidos, alunos carregados no select, turmas com mesmo nome recebem contador, filtro por aluno, filtro por turma, mensagem de lista vazia

Total: **11 testes unitários** e **15 testes E2E**.

#### Tipos de testes

**Testes unitários** (não requerem infra):
- Domínio: `EnrollmentTest`, `ClassroomTest`, `SeatLimitTest`, `EnrollmentPeriodTest`
- Serviço: `EnrollmentServiceTest` (74 casos, incluindo regras de negócio e concorrência simulada em `EnrollmentSeatConsistencyTest`)
- Controller: `EnrollmentControllerTest`, `ClassroomControllerTest`
- Mensageria: `RabbitMqEventPublisherTest`, `EnrollmentEventPublicationTest`
- Notification: `EnrollmentEventListenerTest`, `NotificationServiceTest`, configurações

**Testes de integração** (H2 in-memory, sem PostgreSQL ou RabbitMQ):
- `EnrollmentApiIntegrationTest` — sobe o contexto Spring completo com `@SpringBootTest(RANDOM_PORT)`, cria entidades via REST e valida o ciclo completo de matrícula através de HTTP
- `EnrollmentEventListenerIntegrationTest` — valida o fluxo listener → serviço → repositório com banco H2, incluindo idempotência

Total: **84 testes no `academic`** e **61 testes no `notification`**.

## Quais tecnologias foram usadas

- Java 21
- Spring Boot 3.5 (Web, Data JPA, AMQP, Actuator, Validation)
- PostgreSQL 15
- RabbitMQ 3 (com dead-letter queue)
- Flyway (migrations versionadas)
- OpenAPI/Swagger (springdoc-openapi 2.6)
- Micrometer + Prometheus (métricas)
- H2 (banco in-memory exclusivo para testes de integração)
- Frontend: Vite + TypeScript (sem framework)
- Docker Compose

## Quais foram as principais decisões técnicas

- **Monorepo Maven multi-module** com dois serviços (`academic` e `notification`) que se comunicam exclusivamente via RabbitMQ, sem dependência direta de código entre eles.
- **Arquitetura hexagonal (ports & adapters)** no módulo `academic`: domínio isolado de infraestrutura via interfaces de repositório e `EventPublisher`.
- **Três profiles de execução**: `local` (editável, valores explícitos para máquina do dev), `docker` (hardcoded para nomes de serviços do Compose) e `cloud` (variáveis de ambiente obrigatórias, sem padrões). O profile ativo é definido por `SPRING_PROFILES_ACTIVE`; padrão é `local`.
- **Fluxo de matrícula em etapa única**: `POST /enrollments` cria e confirma a matrícula imediatamente, consumindo a vaga. O endpoint `POST /enrollments/{id}/confirm` existe para re-confirmação explícita (idempotente).
- **Rematrícula após cancelamento permitida**: a regra de duplicidade é verificada no nível de aplicação (`existsByStudentIdAndClassroomIdAndStatusNot`) ignorando matrículas canceladas. Não há unique constraint composta no banco para esse par, evitando bloqueio por constraint em reinserções legítimas.
- **Eventos de domínio padronizados** com `eventId` UUID para permitir idempotência no consumidor.
- **Frontend dividido em três páginas**: `home` (navegação), `cadastros` (CRUD de alunos, cursos, disciplinas, turmas e gestão de matrículas pelo administrador) e `aluno` (perfil do aluno: busca de disciplinas, matrícula e cancelamento de matrícula).
- **Validação de vagas exclusivamente pelo backend**: o frontend envia a requisição e exibe a mensagem de erro retornada pela API, sem pré-verificação no cliente.

## Como a regra de vagas foi protegida

- O `POST /enrollments` cria a matrícula e a confirma imediatamente, consumindo uma vaga. Essa etapa é protegida por lock pessimista (`PESSIMISTIC_WRITE`) na turma.
- O cancelamento de uma matrícula confirmada devolve a vaga para a turma.
- Cancelamento de uma matrícula ainda não confirmada não altera o contador de vagas.
- O controle do limite de vagas é encapsulado no value object `SeatLimit`, que lança exceção ao tentar consumir sem disponibilidade.
- A tentativa de matrícula em turma sem vagas retorna `409 Conflict` com a mensagem `No seats available`.

## Como a solução trata concorrência na matrícula

- `@Transactional` em casos de uso críticos.
- Busca da turma com `PESSIMISTIC_WRITE` (`findByIdForUpdate`), serializando o acesso ao contador de vagas.
- Validação de duplicidade ativa no serviço via `existsByStudentIdAndClassroomIdAndStatusNot`, ignorando matrículas canceladas e permitindo rematrícula.

## Como os eventos de domínio são publicados e consumidos

- O `academic` publica eventos `MatriculaCriada`, `MatriculaConfirmada` e `MatriculaCancelada` no exchange `enrollment.events.exchange` via `RabbitMqEventPublisher`.
- O `notification` consome da fila `enrollment.events` via `@RabbitListener`, persiste uma `Notification` no banco e registra o `eventId` na tabela `processed_events` para garantir idempotência.
- O payload do evento é um JSON com `eventId` (UUID), `eventType`, `enrollmentId`, `studentId`, `classroomId` e `occurredAt`.

## Como a solução trata falhas na mensageria

- Fila principal `enrollment.events` configurada com dead-letter routing para `enrollment.events.dlq` (via `x-dead-letter-exchange` e `x-dead-letter-routing-key`).
- O container de listeners do `notification` usa `defaultRequeueRejected=false`: mensagens rejeitadas por exceção não reentram na fila principal e vão para a DLQ.
- Idempotência no consumidor: antes de processar, o listener verifica se o `eventId` já existe em `processed_events`; se sim, descarta silenciosamente. Garante que reentregas não geram notificações duplicadas.
- Publicação de eventos pelo `academic` é melhor-esforço: a exceção de AMQP é lançada após a persistência já ter sido confirmada (risco de perda de evento sem outbox pattern — evolução prevista).

## Quais logs, health checks, métricas ou mecanismos de rastreabilidade foram implementados

- **Correlation ID / Trace ID**: cada requisição recebe um `traceId` (do header `X-Correlation-Id` ou UUID gerado automaticamente). O ID é inserido no MDC e devolvido no header de resposta e no corpo de erros (`ApiErrorResponse.traceId`).
- **Logs estruturados**: padrão de nível inclui `[traceId]` via MDC em todos os logs do `academic`.
- **Health checks**: `GET /actuator/health` em ambos os serviços; usados pelos healthchecks do Docker Compose.
- **Métricas Micrometer**: contadores `enrollment.created`, `enrollment.confirmed`, `enrollment.cancelled` e `enrollment.rejected.no_seats` incrementados no `EnrollmentService`.
- **Prometheus**: endpoint `GET /actuator/prometheus` exposto no `academic`; o serviço Prometheus sobe junto no Docker Compose (`http://localhost:9090`) e faz scraping automático.

## Quais ferramentas de IA foram utilizadas, em quais partes, o que foi revisado manualmente e quais trechos são mais críticos

**Uso de IA (GitHub Copilot):**
- Configuração de RabbitMQ, Flyway e springdoc
- Base do frontend (roteamento, páginas, consumo de API)
- Documentação da API (anotações `@Tag`, `@Operation`) e architecture.md
- Testes unitários e de integração (cenários e estrutura)
- Funcionalidades incrementais: botão de cancelamento de matrícula no frontend, remoção da validação de vagas do front (delegada ao backend), correção da rematrícula após cancelamento

**Revisão manual aplicada em:**
- Estruturação inicial das camadas (controller, service, domain, repository, DTOs)
- Regras de negócio do domínio (`EnrollmentService`, `Enrollment.cancel()`, `SeatLimit`)
- Lock pessimista e tratamento de concorrência
- SQL das migrations (V1, V2) e decisão de remover a unique constraint
- Consumo idempotente de eventos no `notification`
- Configuração da DLQ
- Fluxos de integração ponta a ponta via Docker Compose

**Trechos mais críticos:**
- `EnrollmentService.createEnrollment` — cria e confirma em uma transação só, com lock na turma
- `Enrollment.cancel()` — libera vaga apenas se o status era CONFIRMED
- `existsByStudentIdAndClassroomIdAndStatusNot` — regra de não-duplicidade ignorando cancelados
- `EnrollmentEventListener.handleEnrollmentEvent` — idempotência com `processed_events`

## Documentação de API

- Academic Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Notification Swagger UI: `http://localhost:8081/swagger-ui/index.html`

## Documentação arquitetural

Veja `docs/architecture.md`.
