package com.lyceum.academic.application.ports;

import com.lyceum.academic.domain.entity.Classroom;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassroomRepository {
    Optional<Classroom> findById(UUID classroomId);
    Classroom save(Classroom classroom);
    List<Classroom> findAll();
    void deleteById(UUID classroomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Classroom c where c.id = :id")
    Optional<Classroom> findByIdForUpdate(@Param("id") UUID id);
}
