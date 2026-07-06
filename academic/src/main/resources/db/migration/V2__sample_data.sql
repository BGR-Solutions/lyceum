-- =====================================================
-- Sample data for academicdb
-- =====================================================

-- Courses
INSERT INTO courses (id, name) VALUES
    ('aaaaaaaa-0001-0001-0001-000000000001', 'Engenharia de Software'),
    ('aaaaaaaa-0001-0001-0001-000000000002', 'Ciência da Computação');

-- Disciplines
INSERT INTO disciplines (id, name, course_id) VALUES
    ('bbbbbbbb-0002-0002-0002-000000000001', 'Algoritmos e Estruturas de Dados', 'aaaaaaaa-0001-0001-0001-000000000002'),
    ('bbbbbbbb-0002-0002-0002-000000000002', 'Banco de Dados',                   'aaaaaaaa-0001-0001-0001-000000000002'),
    ('bbbbbbbb-0002-0002-0002-000000000003', 'Engenharia de Requisitos',          'aaaaaaaa-0001-0001-0001-000000000001'),
    ('bbbbbbbb-0002-0002-0002-000000000004', 'Arquitetura de Software',           'aaaaaaaa-0001-0001-0001-000000000001');

-- Classrooms
INSERT INTO classrooms (id, discipline_id, status, max_seats, occupied_seats, enrollment_start, enrollment_end, version) VALUES
    ('cccccccc-0003-0003-0003-000000000001', 'bbbbbbbb-0002-0002-0002-000000000001', 'OPEN',   30, 2, CURRENT_DATE - 30, CURRENT_DATE + 60, 0),
    ('cccccccc-0003-0003-0003-000000000002', 'bbbbbbbb-0002-0002-0002-000000000003', 'OPEN',   25, 1, CURRENT_DATE - 15, CURRENT_DATE + 45, 0),
    ('cccccccc-0003-0003-0003-000000000003', 'bbbbbbbb-0002-0002-0002-000000000002', 'CLOSED', 20, 0, CURRENT_DATE - 120, CURRENT_DATE - 30, 0),
    ('cccccccc-0003-0003-0003-000000000004', 'bbbbbbbb-0002-0002-0002-000000000004', 'OPEN',   40, 0, CURRENT_DATE - 5,  CURRENT_DATE + 90, 0);

-- Students
INSERT INTO students (id, name) VALUES
    ('dddddddd-0004-0004-0004-000000000001', 'João Silva'),
    ('dddddddd-0004-0004-0004-000000000002', 'Maria Santos'),
    ('dddddddd-0004-0004-0004-000000000003', 'Pedro Oliveira');

-- Enrollments
INSERT INTO enrollments (id, student_id, classroom_id, status) VALUES
    ('eeeeeeee-0005-0005-0005-000000000001', 'dddddddd-0004-0004-0004-000000000001', 'cccccccc-0003-0003-0003-000000000001', 'CONFIRMED'),
    ('eeeeeeee-0005-0005-0005-000000000002', 'dddddddd-0004-0004-0004-000000000002', 'cccccccc-0003-0003-0003-000000000001', 'CONFIRMED'),
    ('eeeeeeee-0005-0005-0005-000000000003', 'dddddddd-0004-0004-0004-000000000003', 'cccccccc-0003-0003-0003-000000000002', 'PENDING'),
    ('eeeeeeee-0005-0005-0005-000000000004', 'dddddddd-0004-0004-0004-000000000001', 'cccccccc-0003-0003-0003-000000000003', 'CANCELLED');
