package com.lyceum.academic.infra.adapters.repository;

import com.lyceum.academic.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepositoryJpa extends JpaRepository<Course, UUID> {
}
