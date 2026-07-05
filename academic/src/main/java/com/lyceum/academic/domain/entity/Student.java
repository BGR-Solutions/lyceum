package com.lyceum.academic.domain.entity;

import java.util.UUID;

/**
 * Represents a student in the academic system.
 * Each student has a unique identifier and a name.
 */
public class Student {
    private final UUID id;
    private final String name;

    public Student(UUID id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        this.id = id;
        this.name = name.trim();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}
