package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.command.CancelEnrollmentCommand;
import com.lyceum.academic.application.command.ConfirmEnrollmentCommand;
import com.lyceum.academic.application.command.CreateEnrollmentCommand;
import com.lyceum.academic.application.service.EnrollmentService;
import com.lyceum.academic.domain.entity.Enrollment;
import com.lyceum.academic.infra.api.dto.CreateEnrollmentRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing Enrollments.
 * Provides endpoints to create, confirm, cancel, and query enrollments.
 */
@RestController
@RequestMapping("/enrollments")
@Tag(name = "Enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public Enrollment createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        return enrollmentService.createEnrollment(
                new CreateEnrollmentCommand(request.studentId(), request.classroomId())
        );
    }

    public Enrollment createEnrollment(UUID studentId, UUID classroomId) {
        return enrollmentService.createEnrollment(
                new CreateEnrollmentCommand(studentId, classroomId)
        );
    }

    @PostMapping("/{id}/confirm")
    public Enrollment confirmEnrollment(@PathVariable UUID id) {
        return enrollmentService.confirmEnrollment(new ConfirmEnrollmentCommand(id));
    }

    @PostMapping("/{id}/cancel")
    public Enrollment cancelEnrollment(@PathVariable UUID id) {
        return enrollmentService.cancelEnrollment(new CancelEnrollmentCommand(id));
    }

    @GetMapping("/by-student/{studentId}")
    public List<Enrollment> getByStudent(@PathVariable UUID studentId) {
        return enrollmentService.findByStudent(studentId);
    }

    @GetMapping("/by-classroom/{classroomId}")
    public List<Enrollment> getByClassroom(@PathVariable UUID classroomId) {
        return enrollmentService.findByClassroom(classroomId);
    }
}
