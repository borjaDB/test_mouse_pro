package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventLoggerTest {
    @Test
    void testLogEventAndGetEvents() {
        EventLogger logger = new EventLogger();
        logger.logEvent("Inicio");
        logger.logEvent("Parada");
        var events = logger.getEvents();
        assertEquals(2, events.size());
        assertTrue(events.get(0).contains("Inicio"));
        assertTrue(events.get(1).contains("Parada"));
    }
}

