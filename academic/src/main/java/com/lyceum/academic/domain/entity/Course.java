package com.lyceum.academic.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    protected Course() {
    }

    public Course(UUID id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = name.trim();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
