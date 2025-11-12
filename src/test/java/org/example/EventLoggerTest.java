package org.example;

import org.example.util.EventLogger;
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

    @Test
    void testClearEvents() {
        EventLogger logger = new EventLogger();
        logger.logEvent("Event 1");
        logger.logEvent("Event 2");
        assertEquals(2, logger.getEvents().size());

        logger.clearEvents();
        assertEquals(0, logger.getEvents().size());
    }

    @Test
    void testMultipleEvents() {
        EventLogger logger = new EventLogger();
        logger.logEvent("First");
        logger.logEvent("Second");
        logger.logEvent("Third");

        assertEquals(3, logger.getEvents().size());
        assertTrue(logger.getEvents().get(0).contains("First"));
        assertTrue(logger.getEvents().get(1).contains("Second"));
        assertTrue(logger.getEvents().get(2).contains("Third"));
    }
}

