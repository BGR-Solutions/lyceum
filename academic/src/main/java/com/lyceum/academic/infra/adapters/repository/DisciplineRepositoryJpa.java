package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.domain.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DisciplineRepositoryJpa extends JpaRepository<Discipline, UUID> {
}
