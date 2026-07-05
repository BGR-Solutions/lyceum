package com.lyceum.academic.domain.entity;

import java.util.UUID;

/**
 * Represents a course in the academic system.
 * Each course has a unique identifier and a name.
 */
public class Course {
    private final UUID id;
    private final String name;

    public Course(UUID id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        this.id = id;
        this.name = name.trim();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}
