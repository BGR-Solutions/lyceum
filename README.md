# lyceum

Sistema acadêmico de matrículas com separação de contextos (`academic` e `notification`), mensageria via RabbitMQ, controle de concorrência para vagas, observabilidade básica e frontend web.

## Como rodar o projeto localmente

### Pré-requisitos
- Java 21
- Maven 3.9+
- Node 22+
- Docker + Docker Compose

### Backend sem Docker
1. Suba infraestrutura:
   ```bash
   docker compose up -d postgres rabbitmq
   ```
2. Execute o módulo acadêmico:
   ```bash
   mvn -pl academic spring-boot:run
   ```
3. Execute o módulo de notificações:
   ```bash
   mvn -pl notification spring-boot:run
   ```
4. Execute o frontend:
   ```bash
   cd frontend
   npm ci
   npm run dev
   ```

## Como subir a solução com Docker Compose

```bash
docker compose up --build
```

Serviços:
- Academic API: `http://localhost:8080`
- Notification API: `http://localhost:8081`
- Frontend: `http://localhost:4173`
- RabbitMQ Management: `http://localhost:15672` (`guest/guest`)

## Como executar os testes automatizados

```bash
mvn -pl academic test
mvn -pl notification test
```

## Quais tecnologias foram usadas

- Java 21
- Spring Boot (Web, JPA, AMQP, Actuator)
- PostgreSQL
- RabbitMQ
- Flyway
- OpenAPI/Swagger (springdoc)
- Frontend com Vite + TypeScript
- Docker Compose

## Quais foram as principais decisões técnicas

- Separação por contexto em dois módulos: `academic` e `notification`.
- Regras críticas de matrícula concentradas no `EnrollmentService`.
- Publicação de eventos de domínio padronizados para integração assíncrona.
- Persistência relacional com Flyway e constraints de integridade.
- Frontend simples para fluxo principal do desafio.

## Como a regra de vagas foi protegida

- Consumo de vagas ocorre somente na confirmação da matrícula.
- Cancelamento confirmado devolve vaga.
- Lock pessimista no carregamento da turma para confirmação/cancelamento.
- Controle de limite de vagas encapsulado em `SeatLimit`.

## Como a solução trata concorrência na matrícula

- `@Transactional` em casos de uso críticos.
- Busca da turma com `PESSIMISTIC_WRITE` (`findByIdForUpdate`), serializando o acesso ao contador de vagas.
- Validação de duplicidade ativa no serviço via `existsByStudentIdAndClassroomIdAndStatusNot`, ignorando matrículas canceladas e permitindo rematrícula.

## Como os eventos de domínio são publicados e consumidos

- Eventos `MatriculaCriada`, `MatriculaConfirmada`, `MatriculaCancelada` são publicados no exchange `enrollment.events.exchange`.
- `notification` consome da fila `enrollment.events` e gera notificações persistidas.
- Contrato de evento unificado em payload JSON com `eventId`, `eventType`, `occurredAt` e chaves de negócio.

## Como a solução trata falhas na mensageria

- Fila principal com dead-letter routing para `enrollment.events.dlq`.
- Consumidor configurado com `defaultRequeueRejected=false`.
- Idempotência via tabela `processed_events` no serviço de notificação.

## Quais logs, health checks, métricas ou mecanismos de rastreabilidade foram implementados

- Correlation ID por request (`X-Correlation-Id`) com fallback para UUID.
- Inclusão do traceId no padrão de logs via MDC.
- Actuator habilitado para health e métricas.

## Quais ferramentas de IA foram utilizadas, em quais partes, o que foi revisado manualmente e quais trechos são mais críticos

- IA usada para acelerar estruturação inicial de camadas, DTOs, documentação e fluxo base do frontend.
- Revisão manual aplicada nas regras críticas de negócio, concorrência, eventos e SQL de migrations.
- Trechos críticos: confirmação/cancelamento de matrícula, lock de concorrência, constraints de unicidade e consumo idempotente de eventos.

## Documentação de API

- Academic Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Documentação arquitetural

Veja `docs/architecture.md`.
