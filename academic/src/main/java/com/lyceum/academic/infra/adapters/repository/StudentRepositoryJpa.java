package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepositoryJpa extends JpaRepository<Student, UUID> {
}
