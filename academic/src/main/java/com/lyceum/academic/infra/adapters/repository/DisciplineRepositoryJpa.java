package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.domain.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for managing Discipline entities.
 * This interface extends JpaRepository to provide CRUD operations for Discipline entities.
 */
public interface DisciplineRepositoryJpa extends JpaRepository<Discipline, UUID> {
}
