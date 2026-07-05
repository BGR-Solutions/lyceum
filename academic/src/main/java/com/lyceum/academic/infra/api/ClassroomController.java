package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.domain.entity.Classroom;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing classrooms.
 * This controller provides endpoints to retrieve and create classrooms.
 */
@RestController
@RequestMapping("/classrooms")
public class ClassroomController {

    private final ClassroomRepository classroomRepository;

    public ClassroomController(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @GetMapping("/{id}")
    public Optional<Classroom> getClassroom(@PathVariable UUID id) {
        return classroomRepository.findById(id);
    }

    @PostMapping
    public Classroom createClassroom(@RequestBody Classroom classroom) {
        return classroomRepository.save(classroom);
    }
}
