# Arquitetura (resumo)

## Contextos

- `academic`: cadastro acadêmico, turmas e matrículas.
- `notification`: consumo de eventos e geração de notificações.

## Decisões e trade-offs

- **Modularização em monorepo (Maven multi-module)**: simplifica desenvolvimento e mantém separação lógica.
- **Mensageria RabbitMQ**: integração assíncrona desacoplada entre contextos.
- **Lock pessimista para vagas**: prioriza consistência em concorrência alta, com custo de menor paralelismo.
- **Idempotência no consumidor**: reduz risco de duplicidade em reentregas.

## Riscos conhecidos

- Frontend foi implementado com foco em fluxo principal e pode evoluir para UX mais robusta.
- Estratégia atual não usa outbox; em produção, publicar evento após persistência pode exigir hardening adicional.

## Evolução prevista

- Introdução de outbox pattern.
- Retry com fila de atraso e políticas operacionais por tipo de falha.
- Testes de carga e concorrência avançados.
- Segurança (autenticação/autorização) e CI/CD.

## Falhas na mensageria

- Dead-letter queue (`enrollment.events.dlq`) para mensagens não processadas.
- Consumidor sem requeue automático e com controle de deduplicação por `eventId`.
