package com.lyceum.academic.infra.api;

import com.lyceum.academic.application.ports.EventPublisher;
import com.lyceum.academic.infra.adapters.repository.ClassroomRepositoryJpa;
import com.lyceum.academic.infra.adapters.repository.CourseRepositoryJpa;
import com.lyceum.academic.infra.adapters.repository.DisciplineRepositoryJpa;
import com.lyceum.academic.infra.adapters.repository.EnrollmentRepositoryJpa;
import com.lyceum.academic.infra.adapters.repository.StudentRepositoryJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the enrollment API endpoints.
 *
 * Uses H2 in-memory database (PostgreSQL compatibility mode) and mocks the
 * EventPublisher port so no real RabbitMQ connection is needed.
 * Entities are created via the REST API to exercise the full stack.
 *
 * Covers:
 *  - Full HTTP lifecycle: create, confirm, cancel enrollment
 *  - Business rules: duplicate rejection, re-enrollment after cancel, full classroom
 *  - Query endpoints: by-student and by-classroom
 *  - Closed enrollment period rejection
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:academictest;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.flyway.enabled=false",
                "management.health.rabbit.enabled=false"
        }
)
class EnrollmentApiIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @MockBean
    private EventPublisher eventPublisher;

    // Used only for cleanup — deleteAll() is unambiguous (only in JpaRepository, not in the domain port)
    @Autowired private EnrollmentRepositoryJpa enrollmentRepository;
    @Autowired private ClassroomRepositoryJpa classroomRepository;
    @Autowired private DisciplineRepositoryJpa disciplineRepository;
    @Autowired private StudentRepositoryJpa studentRepository;
    @Autowired private CourseRepositoryJpa courseRepository;

    private String studentId;
    private String classroomId;

    // ── helpers ────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private ResponseEntity<Map> post(String url, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return rest.postForEntity(url, new HttpEntity<>(body, headers), Map.class);
    }

    private String id(ResponseEntity<Map> response) {
        return (String) response.getBody().get("id");
    }

    private String createEnrollment(String sId, String cId) {
        ResponseEntity<Map> resp = post("/enrollments",
                Map.of("studentId", sId, "classroomId", cId));
        assertEquals(HttpStatus.OK, resp.getStatusCode(),
                "Expected enrollment creation to succeed");
        return id(resp);
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        // Clean up from previous tests (FK order: enrollments → classrooms → disciplines → students/courses)
        enrollmentRepository.deleteAll();
        classroomRepository.deleteAll();
        disciplineRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();

        String courseId   = id(post("/courses", Map.of("name", "Ci\u00eancia da Computa\u00e7\u00e3o")));
        String disciplineId = id(post("/disciplines",
                Map.of("name", "Algoritmos", "courseId", courseId)));

        LocalDate today = LocalDate.now();
        classroomId = id(post("/classrooms", Map.of(
                "disciplineId", disciplineId,
                "maxSeats", 2,
                "enrollmentStart", today.minusDays(1).toString(),
                "enrollmentEnd",   today.plusDays(30).toString()
        )));

        studentId = id(post("/students", Map.of("name", "Alice")));
    }

    // ── tests ──────────────────────────────────────────────────────────────────

    @Test
    void createEnrollment_returnsEnrollmentWithConfirmedStatus() {
        ResponseEntity<Map> resp = post("/enrollments",
                Map.of("studentId", studentId, "classroomId", classroomId));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody().get("id"));
        assertEquals("CONFIRMED", resp.getBody().get("status"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void confirmEnrollment_changesStatusToConfirmed() {
        String enrollmentId = createEnrollment(studentId, classroomId);

        ResponseEntity<Map> resp = post("/enrollments/" + enrollmentId + "/confirm", Map.of());

        assertEquals(HttpStatus.OK, resp.getStatusCode(),
                "confirm failed — enrollmentId=" + enrollmentId + " body=" + resp.getBody());
        assertEquals("CONFIRMED", resp.getBody().get("status"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void cancelConfirmedEnrollment_changesStatusToCancelled() {
        String enrollmentId = createEnrollment(studentId, classroomId);
        post("/enrollments/" + enrollmentId + "/confirm", Map.of());

        ResponseEntity<Map> resp = post("/enrollments/" + enrollmentId + "/cancel", Map.of());

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("CANCELLED", resp.getBody().get("status"));
    }

    @Test
    void cancelPendingEnrollment_changesStatusToCancelled() {
        String enrollmentId = createEnrollment(studentId, classroomId);

        ResponseEntity<Map> resp = post("/enrollments/" + enrollmentId + "/cancel", Map.of());

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("CANCELLED", resp.getBody().get("status"));
    }

    @Test
    void createDuplicateActiveEnrollment_returns409Conflict() {
        createEnrollment(studentId, classroomId);

        ResponseEntity<Map> resp = post("/enrollments",
                Map.of("studentId", studentId, "classroomId", classroomId));

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void createEnrollmentAfterCancellation_allowsReenrollment() {
        String firstId = createEnrollment(studentId, classroomId);
        post("/enrollments/" + firstId + "/cancel", Map.of());

        ResponseEntity<Map> resp = post("/enrollments",
                Map.of("studentId", studentId, "classroomId", classroomId));

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("CONFIRMED", resp.getBody().get("status"));
        assertNotEquals(firstId, resp.getBody().get("id"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void createEnrollmentWhenClassroomFull_returns409Conflict() {
        // Fill both seats (maxSeats = 2)
        String student2 = id(post("/students", Map.of("name", "Bob")));
        String student3 = id(post("/students", Map.of("name", "Carol")));

        String e1 = createEnrollment(studentId, classroomId);
        String e2 = createEnrollment(student2, classroomId);
        post("/enrollments/" + e1 + "/confirm", Map.of());
        post("/enrollments/" + e2 + "/confirm", Map.of());

        // Third student tries to enroll — no seats left
        ResponseEntity<Map> resp = post("/enrollments",
                Map.of("studentId", student3, "classroomId", classroomId));

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void createEnrollmentWithClosedPeriod_returns409Conflict() {
        String courseId = id(post("/courses", Map.of("name", "Hist\u00f3ria")));
        String disciplineId = id(post("/disciplines",
                Map.of("name", "Hist\u00f3ria Geral", "courseId", courseId)));
        LocalDate past = LocalDate.now().minusYears(1);
        String closedClassroomId = id(post("/classrooms", Map.of(
                "disciplineId", disciplineId,
                "maxSeats", 10,
                "enrollmentStart", past.minusDays(30).toString(),
                "enrollmentEnd",   past.toString()
        )));

        ResponseEntity<Map> resp = post("/enrollments",
                Map.of("studentId", studentId, "classroomId", closedClassroomId));

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void listEnrollmentsByStudent_returnsEnrollmentsForThatStudent() {
        createEnrollment(studentId, classroomId);

        ResponseEntity<Object[]> resp = rest.getForEntity(
                "/enrollments/by-student/" + studentId, Object[].class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().length);
    }

    @Test
    void listEnrollmentsByClassroom_returnsEnrollmentsForThatClassroom() {
        createEnrollment(studentId, classroomId);

        ResponseEntity<Object[]> resp = rest.getForEntity(
                "/enrollments/by-classroom/" + classroomId, Object[].class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().length);
    }
}

