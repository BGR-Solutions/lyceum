package com.lyceum.academic.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BasicDomainEntitiesTest {

    @Test
    void studentTrimsNameAndStoresValues() {
        UUID id = UUID.randomUUID();

        Student student = new Student(id, "  Alice  ");

        assertEquals(id, student.getId());
        assertEquals("Alice", student.getName());
    }

    @Test
    void studentRejectsBlankName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Student(UUID.randomUUID(), "   "));

        assertEquals("Student name cannot be empty", ex.getMessage());
    }

    @Test
    void disciplineTrimsNameAndStoresValues() {
        UUID id = UUID.randomUUID();

        Discipline discipline = new Discipline(id, "  Math  ");

        assertEquals(id, discipline.getId());
        assertEquals("Math", discipline.getName());
    }

    @Test
    void disciplineRejectsBlankName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Discipline(UUID.randomUUID(), ""));

        assertEquals("Discipline name cannot be empty", ex.getMessage());
    }

    @Test
    void courseTrimsNameAndStoresValues() {
        UUID id = UUID.randomUUID();

        Course course = new Course(id, "  Biology  ");

        assertEquals(id, course.getId());
        assertEquals("Biology", course.getName());
    }

    @Test
    void courseRejectsBlankName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Course(UUID.randomUUID(), null));

        assertEquals("Course name cannot be empty", ex.getMessage());
    }
}
