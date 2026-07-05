-- V1__init_schema.sql
-- Initial schema for the academic service

CREATE TABLE IF NOT EXISTS students (
    id   UUID        NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_students PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS courses (
    id   UUID        NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_courses PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS disciplines (
    id   UUID        NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_disciplines PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS classrooms (
    id                UUID        NOT NULL,
    discipline_id     UUID        NOT NULL,
    status            VARCHAR(50) NOT NULL,
    max_seats         INTEGER     NOT NULL,
    occupied_seats    INTEGER     NOT NULL DEFAULT 0,
    enrollment_start  DATE        NOT NULL,
    enrollment_end    DATE        NOT NULL,
    CONSTRAINT pk_classrooms       PRIMARY KEY (id),
    CONSTRAINT fk_classrooms_discipline FOREIGN KEY (discipline_id) REFERENCES disciplines (id)
);

CREATE TABLE IF NOT EXISTS enrollments (
    id           UUID        NOT NULL,
    student_id   UUID        NOT NULL,
    classroom_id UUID        NOT NULL,
    status       VARCHAR(50) NOT NULL,
    CONSTRAINT pk_enrollments          PRIMARY KEY (id),
    CONSTRAINT fk_enrollments_student  FOREIGN KEY (student_id)   REFERENCES students   (id),
    CONSTRAINT fk_enrollments_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms (id)
);
