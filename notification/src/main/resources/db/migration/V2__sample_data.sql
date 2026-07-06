-- =====================================================
-- Sample data for notificationdb
-- =====================================================

INSERT INTO notifications (id, message, type, created_at) VALUES
    ('ffffffff-0006-0006-0006-000000000001', 'Matrícula confirmada na disciplina Algoritmos e Estruturas de Dados.',     'INFO',  CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('ffffffff-0006-0006-0006-000000000002', 'Matrícula confirmada na disciplina Algoritmos e Estruturas de Dados.',     'EMAIL', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('ffffffff-0006-0006-0006-000000000003', 'Matrícula confirmada na disciplina Engenharia de Requisitos.',             'INFO',  CURRENT_TIMESTAMP - INTERVAL '1 day'),
    ('ffffffff-0006-0006-0006-000000000004', 'Matrícula cancelada na disciplina Banco de Dados.',                       'INFO',  CURRENT_TIMESTAMP - INTERVAL '1 day'),
    ('ffffffff-0006-0006-0006-000000000005', 'Matrícula cancelada na disciplina Banco de Dados.',                       'PUSH',  CURRENT_TIMESTAMP - INTERVAL '1 day'),
    ('ffffffff-0006-0006-0006-000000000006', 'Período de matrículas para Arquitetura de Software aberto até daqui 90 dias.', 'INFO', CURRENT_TIMESTAMP);
