package com.lyceum.academic.domain.entity;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Represents a discipline in the academic system.
 * Each discipline has a unique identifier, a name, and belongs to a course.
 */
@Entity
@Table(name = "disciplines")
public class Discipline {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    protected Discipline() {
    }

    public Discipline(UUID id, String name) {
        this(id, name, new Course(UUID.randomUUID(), "General"));
    }

    public Discipline(UUID id, String name, Course course) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Discipline name cannot be empty");
        }
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = name.trim();
        this.course = course;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public Course getCourse() { return course; }
    public void setName(String name) { this.name = name; }
    public void setCourse(Course course) { this.course = course; }
}
