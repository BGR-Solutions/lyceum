package com.lyceum.academic.application.ports;

import java.util.Optional;
import java.util.UUID;

import com.lyceum.academic.domain.entity.Classroom;

/**
 * Port to manage persistence of Classroom entities.
 */
public interface ClassroomRepository {
    Optional<Classroom> findById(UUID classroomId);
    Classroom save(Classroom classroom);
}
