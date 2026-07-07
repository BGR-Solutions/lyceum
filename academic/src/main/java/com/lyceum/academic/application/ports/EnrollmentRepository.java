package com.lyceum.academic.application.ports;

import com.lyceum.academic.domain.entity.Enrollment;
import com.lyceum.academic.domain.enums.EnrollmentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port to manage persistence of Enrollment entities.
 */
public interface EnrollmentRepository {
    Enrollment save(Enrollment enrollment);
    Optional<Enrollment> findById(UUID enrollmentId);
    boolean existsByStudentIdAndClassroomId(UUID studentId, UUID classroomId);
    boolean existsByStudentIdAndClassroomIdAndStatusNot(UUID studentId, UUID classroomId, EnrollmentStatus status);
    List<Enrollment> findByStudentId(UUID studentId);
    List<Enrollment> findByClassroomId(UUID classroomId);
}
