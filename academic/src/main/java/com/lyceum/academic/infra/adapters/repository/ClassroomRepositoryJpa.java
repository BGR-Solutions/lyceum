package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.domain.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassroomRepositoryJpa extends JpaRepository<Classroom, UUID>, ClassroomRepository {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Classroom c where c.id = :id")
    Optional<Classroom> findByIdForUpdate(@Param("id") UUID id);
}
