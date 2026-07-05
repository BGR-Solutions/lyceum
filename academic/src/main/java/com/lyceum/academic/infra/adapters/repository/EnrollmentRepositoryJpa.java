package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.application.ports.EnrollmentRepository;
import com.lyceum.academic.domain.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing Enrollment entities.
 * This interface extends JpaRepository to provide CRUD operations and custom query methods for Enrollment entities.
 * It also implements the EnrollmentRepository interface to adhere to the application's domain-driven design principles.
 */
@Repository
public interface EnrollmentRepositoryJpa extends JpaRepository<Enrollment, UUID>, EnrollmentRepository {
    // JpaRepository já fornece save e findById
}
