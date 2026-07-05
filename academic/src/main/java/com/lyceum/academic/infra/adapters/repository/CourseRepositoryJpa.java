package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for managing Course entities.
 * This interface extends JpaRepository to provide CRUD operations for Course entities.
 */
public interface CourseRepositoryJpa extends JpaRepository<Course, UUID> {
}
