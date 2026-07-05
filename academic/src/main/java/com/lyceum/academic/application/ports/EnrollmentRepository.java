package com.lyceum.academic.application.ports;

import com.lyceum.academic.domain.entity.Enrollment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository {
    Enrollment save(Enrollment enrollment);
    Optional<Enrollment> findById(UUID enrollmentId);
    boolean existsByStudentIdAndClassroomId(UUID studentId, UUID classroomId);
    List<Enrollment> findByStudentId(UUID studentId);
    List<Enrollment> findByClassroomId(UUID classroomId);
}
