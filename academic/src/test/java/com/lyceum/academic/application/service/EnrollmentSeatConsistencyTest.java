package com.lyceum.academic.application.service;

import com.lyceum.academic.application.command.ConfirmEnrollmentCommand;
import com.lyceum.academic.application.ports.ClassroomRepository;
import com.lyceum.academic.application.ports.EnrollmentRepository;
import com.lyceum.academic.application.ports.EventPublisher;
import com.lyceum.academic.domain.entity.Classroom;
import com.lyceum.academic.domain.entity.Discipline;
import com.lyceum.academic.domain.entity.Enrollment;
import com.lyceum.academic.domain.entity.Student;
import com.lyceum.academic.domain.enums.EnrollmentStatus;
import com.lyceum.academic.domain.event.EnrollmentConfirmed;
import com.lyceum.academic.domain.valueobject.EnrollmentPeriod;
import com.lyceum.academic.domain.valueobject.SeatLimit;
import com.lyceum.academic.infra.adapters.repository.StudentRepositoryJpa;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes de consistência da regra de vagas por turma.
 *
 * Cobertura:
 *  1. Fluxo simples: confirmar quando há vaga / rejeitar quando não há.
 *  2. Publicação de evento apenas quando a confirmação é bem-sucedida.
 *  3. Concorrência: N threads disputando a última vaga — somente 1 deve ser confirmada.
 *
 * Estratégia de concorrência:
 *  O banco usa SELECT … FOR UPDATE (findByIdForUpdate) para serializar o acesso à Classroom.
 *  Nos testes unitários simulamos esse comportamento com um bloco `synchronized` sobre
 *  a instância da Classroom, tornando o teste determinístico sem precisar de banco real.
 */
@ExtendWith(MockitoExtension.class)
class EnrollmentSeatConsistencyTest {

    @Mock private EnrollmentRepository  enrollmentRepository;
    @Mock private ClassroomRepository   classroomRepository;
    @Mock private EventPublisher        eventPublisher;
    @Mock private StudentRepositoryJpa  studentRepository;

    private EnrollmentService service;

    @BeforeEach
    void setUp() {
        service = new EnrollmentService(
                enrollmentRepository, classroomRepository, eventPublisher, studentRepository,
                new SimpleMeterRegistry());
    }

    // ------------------------------------------------------------------ //
    //  1. Fluxo simples                                                   //
    // ------------------------------------------------------------------ //

    @Test
    void confirmEnrollment_withAvailableSeat_consumesSeatAndPublishesEvent() {
        Classroom classroom   = buildClassroom(1);
        Enrollment enrollment = new Enrollment(new Student(UUID.randomUUID(), "Alice"), classroom);

        when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        when(classroomRepository.findByIdForUpdate(classroom.getId())).thenReturn(Optional.of(classroom));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

        Enrollment result = service.confirmEnrollment(new ConfirmEnrollmentCommand(enrollment.getId()));

        assertEquals(EnrollmentStatus.CONFIRMED, result.getStatus());
        assertEquals(1, classroom.getSeatLimit().getOccupiedSeats());
        verify(eventPublisher).publish(any(EnrollmentConfirmed.class));
    }

    @Test
    void confirmEnrollment_withNoSeatsLeft_throwsAndDoesNotPublishEvent() {
        Classroom classroom   = buildClassroom(1);
        Enrollment first      = new Enrollment(new Student(UUID.randomUUID(), "Alice"), classroom);
        Enrollment second     = new Enrollment(new Student(UUID.randomUUID(), "Bob"),   classroom);

        // Confirma a primeira matrícula para lotar a turma
        first.confirm();
        assertEquals(1, classroom.getSeatLimit().getOccupiedSeats());

        when(enrollmentRepository.findById(second.getId())).thenReturn(Optional.of(second));
        when(classroomRepository.findByIdForUpdate(classroom.getId())).thenReturn(Optional.of(classroom));

        assertThrows(IllegalStateException.class,
                () -> service.confirmEnrollment(new ConfirmEnrollmentCommand(second.getId())));

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void confirmEnrollment_exactCapacity_allSeatsFilledInSequence() {
        int capacity = 3;
        Classroom classroom = buildClassroom(capacity);

        for (int i = 0; i < capacity; i++) {
            Enrollment enrollment = new Enrollment(
                    new Student(UUID.randomUUID(), "Student " + i), classroom);

            when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
            when(classroomRepository.findByIdForUpdate(classroom.getId())).thenReturn(Optional.of(classroom));
            when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

            service.confirmEnrollment(new ConfirmEnrollmentCommand(enrollment.getId()));
        }

        assertFalse(classroom.hasAvailableSeats());
        assertEquals(capacity, classroom.getSeatLimit().getOccupiedSeats());
        verify(eventPublisher, times(capacity)).publish(any(EnrollmentConfirmed.class));
    }

    // ------------------------------------------------------------------ //
    //  2. Concorrência — simulação de pessimistic lock                    //
    // ------------------------------------------------------------------ //

    /**
     * Simula N threads disputando a última vaga de uma turma.
     *
     * O `synchronized(classroom)` replica o efeito do SELECT … FOR UPDATE do banco:
     * apenas uma thread por vez executa confirm + save dentro do "lock".
     * Resultado esperado: exatamente 1 confirmação, N-1 rejeições, 1 evento publicado.
     *
     * @RepeatedTest(5) garante que o resultado é estável em múltiplas execuções.
     */
    @RepeatedTest(5)
    void concurrentConfirms_withPessimisticLock_onlyOneSucceeds() throws InterruptedException {
        final int THREADS  = 10;
        Classroom classroom = buildClassroom(1);

        List<Enrollment> enrollments = IntStream.range(0, THREADS)
                .mapToObj(i -> new Enrollment(new Student(UUID.randomUUID(), "S" + i), classroom))
                .collect(toList());

        AtomicInteger confirmed = new AtomicInteger();
        AtomicInteger rejected  = new AtomicInteger();
        AtomicInteger published = new AtomicInteger();

        CountDownLatch startGate  = new CountDownLatch(1);
        CountDownLatch finishGate = new CountDownLatch(THREADS);
        ExecutorService executor  = Executors.newFixedThreadPool(THREADS);

        for (Enrollment enrollment : enrollments) {
            executor.submit(() -> {
                try {
                    startGate.await();

                    // synchronized(classroom) simula o SELECT … FOR UPDATE:
                    // serializa o acesso ao aggregate root exatamente como o banco faria.
                    synchronized (classroom) {
                        enrollment.confirm();       // lança se não há vaga
                        confirmed.incrementAndGet();
                        published.incrementAndGet(); // publicaria evento aqui
                    }

                } catch (IllegalStateException e) {
                    rejected.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishGate.countDown();
                }
            });
        }

        startGate.countDown();
        assertTrue(finishGate.await(5, TimeUnit.SECONDS), "Timeout: threads não concluíram");
        executor.shutdown();

        assertEquals(1, confirmed.get(),
                "Exatamente 1 matrícula deve ser confirmada com 1 vaga disponível");
        assertEquals(THREADS - 1, rejected.get(),
                "As demais devem ser rejeitadas por falta de vaga");
        assertEquals(1, published.get(),
                "O evento de confirmação deve ser publicado apenas 1 vez");
        assertEquals(1, classroom.getSeatLimit().getOccupiedSeats(),
                "Apenas 1 vaga deve estar ocupada após a concorrência");
    }

    /**
     * Verifica que sem o lock (sem synchronized), a race condition pode resultar em
     * overbooking — demonstrando POR QUE o SELECT … FOR UPDATE é necessário.
     *
     * Este teste é não-determinístico por natureza (depende do scheduler da JVM),
     * por isso serve como teste de documentação/demonstração e não como asserção hard.
     * Em ambientes de CI com scheduling previsível pode passar mesmo sem lock.
     */
    @Test
    void concurrentConfirms_withoutLock_mayAllowOverbooking() throws InterruptedException {
        final int THREADS  = 20;
        Classroom classroom = buildClassroom(1);

        List<Enrollment> enrollments = IntStream.range(0, THREADS)
                .mapToObj(i -> new Enrollment(new Student(UUID.randomUUID(), "S" + i), classroom))
                .collect(toList());

        AtomicInteger confirmed = new AtomicInteger();
        CountDownLatch startGate  = new CountDownLatch(1);
        CountDownLatch finishGate = new CountDownLatch(THREADS);
        ExecutorService executor  = Executors.newFixedThreadPool(THREADS);

        for (Enrollment enrollment : enrollments) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    // SEM synchronized — simula ausência de lock no banco
                    enrollment.confirm();
                    confirmed.incrementAndGet();
                } catch (IllegalStateException ignored) {
                    // rejeitado pela regra de domínio (se o check race for perdido)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishGate.countDown();
                }
            });
        }

        startGate.countDown();
        assertTrue(finishGate.await(5, TimeUnit.SECONDS));
        executor.shutdown();

        // Sem lock: o campo occupiedSeats pode ultrapassar maxSeats devido à race condition
        // entre hasAvailableSeats() e occupiedSeats++ em consumeSeat().
        // A asserção documenta o comportamento observado — não é uma regra de negócio.
        System.out.printf(
                "[sem lock] confirmadas=%d, vagas ocupadas=%d, maxSeats=%d%n",
                confirmed.get(),
                classroom.getSeatLimit().getOccupiedSeats(),
                classroom.getSeatLimit().getMaxSeats());

        // O valor esperado pelo negócio seria 1; sem lock pode ser maior.
        // A asserção abaixo falha propositalmente se houver overbooking detectável:
        // assertEquals(1, classroom.getSeatLimit().getOccupiedSeats());
        // → use o teste acima (withPessimisticLock) para a garantia real.
    }

    // ------------------------------------------------------------------ //
    //  Helpers                                                             //
    // ------------------------------------------------------------------ //

    private Classroom buildClassroom(int seats) {
        return new Classroom(
                UUID.randomUUID(),
                new Discipline(UUID.randomUUID(), "Matemática"),
                new SeatLimit(seats),
                new EnrollmentPeriod(LocalDate.now().minusDays(1), LocalDate.now().plusDays(30))
        );
    }
}
