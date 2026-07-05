package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.service.CrudService;
import com.lyceum.academic.domain.entity.Course;
import com.lyceum.academic.infra.api.dto.CourseRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing Courses.
 * Provides endpoints to create, retrieve, update, and delete courses.
 */
@RestController
@RequestMapping("/courses")
@Tag(name = "Courses")
public class CourseController {
    private final CrudService crudService;

    public CourseController(CrudService crudService) {
        this.crudService = crudService;
    }

    @PostMapping
    public Course create(@Valid @RequestBody CourseRequest request) {
        return crudService.createCourse(request);
    }

    @GetMapping
    public List<Course> list() {
        return crudService.listCourses();
    }

    @PutMapping("/{id}")
    public Course update(@PathVariable UUID id, @Valid @RequestBody CourseRequest request) {
        return crudService.updateCourse(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        crudService.deleteCourse(id);
    }
}
