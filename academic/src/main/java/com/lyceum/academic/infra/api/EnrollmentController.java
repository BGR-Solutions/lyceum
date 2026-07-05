package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.command.CreateEnrollmentCommand;
import com.lyceum.academic.application.command.ConfirmEnrollmentCommand;
import com.lyceum.academic.application.command.CancelEnrollmentCommand;
import com.lyceum.academic.application.service.EnrollmentService;
import com.lyceum.academic.domain.entity.Enrollment;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * REST controller for managing enrollments.
 * This controller provides endpoints to create, confirm, and cancel enrollments.
 */
@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public Enrollment createEnrollment(@RequestParam UUID studentId,
                                       @RequestParam UUID classroomId) {
        return enrollmentService.createEnrollment(
                new CreateEnrollmentCommand(studentId, classroomId)
        );
    }

    @PostMapping("/{id}/confirm")
    public Enrollment confirmEnrollment(@PathVariable UUID id) {
        return enrollmentService.confirmEnrollment(
                new ConfirmEnrollmentCommand(id)
        );
    }

    @PostMapping("/{id}/cancel")
    public Enrollment cancelEnrollment(@PathVariable UUID id) {
        return enrollmentService.cancelEnrollment(
                new CancelEnrollmentCommand(id)
        );
    }
}
