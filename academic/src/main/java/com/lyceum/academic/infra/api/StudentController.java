package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.service.CrudService;
import com.lyceum.academic.domain.entity.Student;
import com.lyceum.academic.infra.api.dto.StudentRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/students")
@Tag(name = "Students")
public class StudentController {
    private final CrudService crudService;

    public StudentController(CrudService crudService) {
        this.crudService = crudService;
    }

    @PostMapping
    public Student create(@Valid @RequestBody StudentRequest request) {
        return crudService.createStudent(request);
    }

    @GetMapping
    public List<Student> list() {
        return crudService.listStudents();
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable UUID id, @Valid @RequestBody StudentRequest request) {
        return crudService.updateStudent(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        crudService.deleteStudent(id);
    }
}
