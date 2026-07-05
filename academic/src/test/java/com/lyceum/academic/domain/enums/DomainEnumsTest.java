package com.lyceum.academic.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainEnumsTest {

    @Test
    void classroomStatusContainsExpectedValues() {
        assertEquals(2, ClassroomStatus.values().length);
        assertEquals(ClassroomStatus.OPEN, ClassroomStatus.valueOf("OPEN"));
        assertEquals(ClassroomStatus.CLOSED, ClassroomStatus.valueOf("CLOSED"));
    }

    @Test
    void enrollmentStatusContainsExpectedValues() {
        assertEquals(3, EnrollmentStatus.values().length);
        assertEquals(EnrollmentStatus.PENDING, EnrollmentStatus.valueOf("PENDING"));
        assertEquals(EnrollmentStatus.CONFIRMED, EnrollmentStatus.valueOf("CONFIRMED"));
        assertEquals(EnrollmentStatus.CANCELLED, EnrollmentStatus.valueOf("CANCELLED"));
    }

    @Test
    void courseTypeContainsExpectedValues() {
        assertEquals(3, CourseType.values().length);
        assertEquals(CourseType.UNDERGRADUATE, CourseType.valueOf("UNDERGRADUATE"));
        assertEquals(CourseType.POSTGRADUATE, CourseType.valueOf("POSTGRADUATE"));
        assertEquals(CourseType.EXTENSION, CourseType.valueOf("EXTENSION"));
    }

    @Test
    void notificationTypeContainsExpectedValues() {
        assertEquals(3, NotificationType.values().length);
        assertEquals(NotificationType.EMAIL, NotificationType.valueOf("EMAIL"));
        assertEquals(NotificationType.AUDIT, NotificationType.valueOf("AUDIT"));
        assertEquals(NotificationType.REPORT, NotificationType.valueOf("REPORT"));
    }
}
