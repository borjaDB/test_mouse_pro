package org.example.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sistema de registro de eventos de la aplicación.
 */
public class EventLogger {
    private final List<String> events = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void logEvent(String message) {
        String timestamp = sdf.format(new Date());
        String logEntry = "[" + timestamp + "] " + message;
        events.add(logEntry);
        System.out.println(logEntry);
    }

    public List<String> getEvents() {
        return new ArrayList<>(events);
    }

    public void clearEvents() {
        events.clear();
    }
}

