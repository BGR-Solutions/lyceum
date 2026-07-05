package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.application.service.CrudService;
import com.lyceum.academic.domain.entity.Classroom;
import com.lyceum.academic.infra.api.dto.ClassroomRequest;
import com.lyceum.academic.infra.api.dto.ClassroomStatusRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/classrooms")
@Tag(name = "Classrooms")
public class ClassroomController {

    private final ClassroomRepository classroomRepository;
    private final CrudService crudService;

    public ClassroomController(ClassroomRepository classroomRepository, CrudService crudService) {
        this.classroomRepository = classroomRepository;
        this.crudService = crudService;
    }

    @GetMapping("/{id}")
    public Optional<Classroom> getClassroom(@PathVariable UUID id) {
        return classroomRepository.findById(id);
    }

    @GetMapping
    public List<Classroom> listClassrooms() {
        return crudService.listClassrooms();
    }

    @PostMapping
    public Classroom createClassroom(@Valid @RequestBody ClassroomRequest classroomRequest) {
        return crudService.createClassroom(classroomRequest);
    }

    public Classroom createClassroom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    @PatchMapping("/{id}/status")
    public Classroom updateStatus(@PathVariable UUID id, @Valid @RequestBody ClassroomStatusRequest request) {
        return crudService.updateClassroomStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        crudService.deleteClassroom(id);
    }
}
