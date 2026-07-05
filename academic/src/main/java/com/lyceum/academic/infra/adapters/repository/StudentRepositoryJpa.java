package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for managing Student entities.
 * This interface extends JpaRepository to provide CRUD operations for Student entities.
 */
public interface StudentRepositoryJpa extends JpaRepository<Student, UUID> {
}
