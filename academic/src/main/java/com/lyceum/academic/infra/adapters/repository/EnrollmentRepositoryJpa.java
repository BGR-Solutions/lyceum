package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.application.ports.EnrollmentRepository;
import com.lyceum.academic.domain.entity.Enrollment;
import com.lyceum.academic.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing Enrollment entities.
 * This interface extends JpaRepository to provide CRUD operations and custom query methods for Enrollment entities.
 * It also implements the EnrollmentRepository interface to adhere to the application's domain-driven design principles.
 */
@Repository
public interface EnrollmentRepositoryJpa extends JpaRepository<Enrollment, UUID>, EnrollmentRepository {
    boolean existsByStudentIdAndClassroomId(UUID studentId, UUID classroomId);
    boolean existsByStudentIdAndClassroomIdAndStatusNot(UUID studentId, UUID classroomId, EnrollmentStatus status);
    List<Enrollment> findByStudentId(UUID studentId);
    List<Enrollment> findByClassroomId(UUID classroomId);
}
