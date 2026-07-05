package com.lyceum.academic.domain.entity;

import java.util.UUID;

/**
 * Represents a discipline in the academic system.
 * Each discipline has a unique identifier and a name.
 */
public class Discipline {
    private final UUID id;
    private final String name;

    public Discipline(UUID id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Discipline name cannot be empty");
        }
        this.id = id;
        this.name = name.trim();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}
