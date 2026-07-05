package com.lyceum.academic.application.ports;

import java.util.Optional;
import java.util.UUID;

import com.lyceum.academic.domain.entity.Enrollment;

/**
 * Port to manage persistence of Enrollment entities.
 */
public interface EnrollmentRepository {
    Enrollment save(Enrollment enrollment);
    Optional<Enrollment> findById(UUID enrollmentId);
}
