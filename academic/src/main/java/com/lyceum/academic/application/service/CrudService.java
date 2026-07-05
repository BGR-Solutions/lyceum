package com.lyceum.academic.application.service;

import com.lyceum.academic.domain.entity.Classroom;
import com.lyceum.academic.domain.entity.Course;
import com.lyceum.academic.domain.entity.Discipline;
import com.lyceum.academic.domain.entity.Student;
import com.lyceum.academic.domain.enums.ClassroomStatus;
import com.lyceum.academic.domain.valueobject.EnrollmentPeriod;
import com.lyceum.academic.domain.valueobject.SeatLimit;
import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.infra.adapters.repository.CourseRepositoryJpa;
import com.lyceum.academic.infra.adapters.repository.DisciplineRepositoryJpa;
import com.lyceum.academic.infra.adapters.repository.StudentRepositoryJpa;
import com.lyceum.academic.infra.api.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CrudService {
    private final StudentRepositoryJpa studentRepository;
    private final CourseRepositoryJpa courseRepository;
    private final DisciplineRepositoryJpa disciplineRepository;
    private final ClassroomRepository classroomRepository;

    public CrudService(StudentRepositoryJpa studentRepository,
                       CourseRepositoryJpa courseRepository,
                       DisciplineRepositoryJpa disciplineRepository,
    ClassroomRepository classroomRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.disciplineRepository = disciplineRepository;
        this.classroomRepository = classroomRepository;
    }

    @Transactional
    public Student createStudent(StudentRequest request) {
        return studentRepository.save(new Student(UUID.randomUUID(), request.name()));
    }

    @Transactional(readOnly = true)
    public List<Student> listStudents() {
        return studentRepository.findAll();
    }

    @Transactional
    public Student updateStudent(UUID id, StudentRequest request) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found"));
        student.setName(request.name());
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(UUID id) {
        studentRepository.deleteById(id);
    }

    @Transactional
    public Course createCourse(CourseRequest request) {
        return courseRepository.save(new Course(UUID.randomUUID(), request.name()));
    }

    @Transactional(readOnly = true)
    public List<Course> listCourses() {
        return courseRepository.findAll();
    }

    @Transactional
    public Course updateCourse(UUID id, CourseRequest request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        course.setName(request.name());
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(UUID id) {
        courseRepository.deleteById(id);
    }

    @Transactional
    public Discipline createDiscipline(DisciplineRequest request) {
        Course course = courseRepository.findById(request.courseId()).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return disciplineRepository.save(new Discipline(UUID.randomUUID(), request.name(), course));
    }

    @Transactional(readOnly = true)
    public List<Discipline> listDisciplines() {
        return disciplineRepository.findAll();
    }

    @Transactional
    public Discipline updateDiscipline(UUID id, DisciplineRequest request) {
        Discipline discipline = disciplineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Discipline not found"));
        Course course = courseRepository.findById(request.courseId()).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        discipline.setName(request.name());
        discipline.setCourse(course);
        return disciplineRepository.save(discipline);
    }

    @Transactional
    public void deleteDiscipline(UUID id) {
        disciplineRepository.deleteById(id);
    }

    @Transactional
    public Classroom createClassroom(ClassroomRequest request) {
        Discipline discipline = disciplineRepository.findById(request.disciplineId()).orElseThrow(() -> new IllegalArgumentException("Discipline not found"));
        Classroom classroom = new Classroom(
                UUID.randomUUID(),
                discipline,
                new SeatLimit(request.maxSeats()),
                new EnrollmentPeriod(request.enrollmentStart(), request.enrollmentEnd())
        );
        return classroomRepository.save(classroom);
    }

    @Transactional(readOnly = true)
    public List<Classroom> listClassrooms() {
        return classroomRepository.findAll();
    }

    @Transactional
    public Classroom updateClassroomStatus(UUID id, ClassroomStatusRequest request) {
        Classroom classroom = classroomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Classroom not found"));
        classroom.setStatus(ClassroomStatus.valueOf(request.status()));
        return classroomRepository.save(classroom);
    }

    @Transactional
    public void deleteClassroom(UUID id) {
        classroomRepository.deleteById(id);
    }
}
