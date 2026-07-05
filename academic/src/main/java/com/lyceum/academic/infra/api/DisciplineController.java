package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.service.CrudService;
import com.lyceum.academic.domain.entity.Discipline;
import com.lyceum.academic.infra.api.dto.DisciplineRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/disciplines")
@Tag(name = "Disciplines")
public class DisciplineController {
    private final CrudService crudService;

    public DisciplineController(CrudService crudService) {
        this.crudService = crudService;
    }

    @PostMapping
    public Discipline create(@Valid @RequestBody DisciplineRequest request) {
        return crudService.createDiscipline(request);
    }

    @GetMapping
    public List<Discipline> list() {
        return crudService.listDisciplines();
    }

    @PutMapping("/{id}")
    public Discipline update(@PathVariable UUID id, @Valid @RequestBody DisciplineRequest request) {
        return crudService.updateDiscipline(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        crudService.deleteDiscipline(id);
    }
}
