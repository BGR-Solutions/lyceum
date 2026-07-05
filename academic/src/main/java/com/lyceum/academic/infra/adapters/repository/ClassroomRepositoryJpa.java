package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.domain.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing Classroom entities.
 * This interface extends JpaRepository to provide CRUD operations and custom query methods for Classroom entities.
 * It also implements the ClassroomRepository interface to adhere to the application's domain-driven design principles.
 */
@Repository
public interface ClassroomRepositoryJpa extends JpaRepository<Classroom, UUID>, ClassroomRepository {
    // JpaRepository já fornece save e findById
}
