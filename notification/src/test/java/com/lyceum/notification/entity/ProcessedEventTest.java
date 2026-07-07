package com.lyceum.notification.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProcessedEventTest {

    @Test
    void constructorSetsEventId() {
        UUID eventId = UUID.randomUUID();
        ProcessedEvent event = new ProcessedEvent(eventId);

        assertEquals(eventId, event.getEventId());
    }

    @Test
    void twoEventsWithDifferentIdsAreDistinct() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        ProcessedEvent event1 = new ProcessedEvent(id1);
        ProcessedEvent event2 = new ProcessedEvent(id2);

        assertNotEquals(event1.getEventId(), event2.getEventId());
    }
}
