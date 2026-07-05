package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.application.service.CrudService;
import com.lyceum.academic.domain.entity.Classroom;
import com.lyceum.academic.domain.entity.Discipline;
import com.lyceum.academic.domain.valueobject.EnrollmentPeriod;
import com.lyceum.academic.domain.valueobject.SeatLimit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassroomControllerTest {

    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private CrudService crudService;

    @InjectMocks
    private ClassroomController controller;

    @Test
    void getClassroomDelegatesToRepository() {
        UUID classroomId = UUID.randomUUID();
        Classroom classroom = buildClassroom();
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));

        Optional<Classroom> result = controller.getClassroom(classroomId);

        assertEquals(Optional.of(classroom), result);
        verify(classroomRepository).findById(classroomId);
    }

    @Test
    void createClassroomDelegatesToRepository() {
        Classroom classroom = buildClassroom();
        when(classroomRepository.save(classroom)).thenReturn(classroom);

        Classroom result = controller.createClassroom(classroom);

        assertEquals(classroom, result);
        verify(classroomRepository).save(classroom);
    }

    private static Classroom buildClassroom() {
        return new Classroom(
                UUID.randomUUID(),
                new Discipline(UUID.randomUUID(), "History"),
                new SeatLimit(3),
                new EnrollmentPeriod(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
        );
    }
}
