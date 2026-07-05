package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.application.ports.EnrollmentRepository;
import com.lyceum.academic.domain.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnrollmentRepositoryJpa extends JpaRepository<Enrollment, UUID>, EnrollmentRepository {
    boolean existsByStudentIdAndClassroomId(UUID studentId, UUID classroomId);
    List<Enrollment> findByStudentId(UUID studentId);
    List<Enrollment> findByClassroomId(UUID classroomId);
}
