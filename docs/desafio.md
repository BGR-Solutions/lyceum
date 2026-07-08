# DESAFIO TÉCNICO

| Stack obrigatória |
|-------------------|
| Backend em Java com Spring Boot. Frontend em Angular, JavaScript, TypeScript ou framework web equivalente. |

## 01 Instruções gerais
---

1. O repositório deve conter um README contendo instruções de execução, testes. tecnologias utilizadas, principais decisôes técnicas e uso de IA (será o último item).
2. A solução precisa ser executável de forma reproduzível. O Docker Compose deve subir aplicação, banco e mensageria (funcionando via Docker Composer).

## 02 Desafio funcional
---

Desenvolva uma solução para gestão de matrículas acadêmicas. A solução deve permitir cadastrar alunos, cursos, disciplinas e turmas, além de controlar matrículas com regras de negócio claras.

### Entidades sugeridas
- Aluno (Ok)
- Curso (Ok)
- Disciplina (Ok)
- Turma (Ok)
- Matrícula (Ok)

### Regras de negócio mínimas
- Um aluno só pode ser matriculado em turmas abertas.
- Uma turma possui limite de vagas.
- Um aluno não pode se matricular duas vezes na mesma turma.
- Uma matrícula possui status: PENDENTE, CONFIRMADA ou CANCELADA.
- Ao confirmar uma matrícula, a vaga da turma deve ser consumida.
- Ao cancelar uma matrícula confirmada, a vaga deve ser liberada.
- Deve haver consulta de matrículas por aluno e por turma.

## 03 Base técnica obrigatória
---

A entrega deve contemplar os requisitos funcionais e técnicos de base esperados para uma aplicação sustentável.

| Área | Expectativa |
|---|---|
| Backend | Java com Spring Boot; API REST funcional; separação clara entre controller,service/application, domain/model, repository/persistence e DTOs. |
| Persistência | Banco de dados relacional com JPA/Hibernate e migrations com Flyway ou Liquibase. |
| Funcionalidades | Cadastro, edição, listagem e exclusão de alunos, cursos, disciplinas e turmas; matrícula de aluno em turma; consultas de matrículas por aluno e por turma. |
| Validações e erros | Validação de entrada nas APIs e tratamento padronizado de erros. |
| Testes | Testes automatizados cobrindo as principais regras de matrícula, incluindo testes unitários e testes de integração ou testes de API. |
| Frontend | Frontend estruturado com componentes, telas separadas, tratamento de erros e consumo organizado da API. |
| Documentação | Documentação da API com Swagger/OpenAPI e README com execução, testes, decisões técnicas, tecnologias usadas e uso de IA. |
| Ambiente | Docker Compose com banco de dados, mensageria e aplicação. |

## 04 Requisitos específicos
---

Objetivo: avaliar arquitetura, desacoplamento, mensageria, consistência, observabilidade, testes, tomada de decisão e capacidade de evolução técnica.
- Solução modular ou baseada em mais de um serviço.
- Separação clara entre contexto acadêmico e contexto de notificações, auditoria ou relatórios.
- Comunicação assíncrona usando RabbitMQ, Kafka, Redis ou tecnologia equivalente.
- Publicação de eventos de domínio, como MatriculaCriada, MatriculaConfirmada e MatriculaCancelada.
- Consumidor de eventos em outro módulo ou serviço.
- Garantia de consistência na regra de vagas.
- Preocupação explícita com concorrência na matrícula.
- Testes unitários e de integração nas regras críticas.
- Docker Compose subindo aplicação, banco de dados e mensageria.
- Observabilidade mínima: logs estruturados, correlation ID ou trace ID, health checks e métricas básicas quando possível.
- Documentação arquitetural curta explicando decisões, trade-offs, riscos conhecidos, evolução da solução e tratamento de falhas na mensageria.
- README completo.

### Resumo do que é esperado no repositório
- README com instruções de execução.
- Testes unitários e de integração.
- Docker Compose completo.
- Eventos de domínio publicados e consumidos.
- Controle de concorrência nas vagas.
- Logs estruturados e correlation ID ou trace ID.
- Documentação arquitetural com decisões e trade-offs.
- Arquitetura coesa e justificada.
- Separação real de contextos.
- Tratamento de falhas na mensageria.
- Consistência entre README, arquitetura e implementação.
- Clareza na evolução prevista da solução.

## 05 README esperado
---

O README é parte essencial da avaliação.
- Como rodar o projeto localmente.
- Como subir a solução com Docker Compose.
- Como executar os testes automatizados.
- Quais tecnologias foram usadas.
- Quais foram as principais decisões técnicas.
- Como a regra de vagas foi protegida.
- Como a solução trata concorrência na matrícula.
- Como os eventos de domínio são publicados e consumidos.
- Como a solução trata falhas na mensageria.
- Quais logs, health checks, métricas ou mecanismos de rastreabilidade foram implementados.
- Quais ferramentas de IA foram utilizadas, em quais partes, o que foi revisado manualmente e quais trechos são mais críticos.

## 06 Critérios de aceite
---

| Tema | Ponto crítico |
|---|---|
| Execução | A aplicação deve ser executada de forma reproduzível; com instrução clara de execução; o projeto deve rodar. |
| Stack | O backend deve ser desenvolvido em Spring Boot. |
| Persistência | Deve haver persistência de dados. |
| Regras de negócio | A regra de matrícula deve estar implementada e a regra de vagas não pode ser quebrada facilmente. |
| Testes | Cobertura das regras críticas e qualidade dos cenários. |
| Erros | Tratamento adequado e padronizado de erros. |
| Mensageria | Mensageria real e funcional. |
| Arquitetura | Separação de responsabilidades, clareza de camadas e modelagem com ganho arquitetural. Modularização, separação de contextos e decisões justificadas. |
| Consistência | Deve ser clara a preocupação com consistência ou concorrência. |
| Documentação | README suficiente para executar e validar a solução, documentação arquitetural e da API, como Swagger. |
| Funcionalidade | Fluxos principais funcionando e aderentes às regras de negócio. |
| Regras de negócio | Consistência da matrícula, controle de vagas e cancelamento. |
| Frontend | Deve estar organizado, com fluxo de uso e tratamento de erros. |
| Mensageria/eventos | Eventos de domínio, consumidores, tratamento de falhas e consistência. |
| Observabilidade/operação | Logs, rastreabilidade, health checks e métricas básicas. |

## 07 Diferenciais
---

Itens não obrigatórios, mas valorizados.
- Outbox Pattern.
- Idempotência no consumo de mensagens.
- Retry e dead letter queue.
- Tracing distribuído.
- Autenticação e autorização.
- CI/CD.
- ADRs curtos.
- Estratégia clara para refatoração de legado.
- Paginação e filtros.
- Boa organização do frontend.

