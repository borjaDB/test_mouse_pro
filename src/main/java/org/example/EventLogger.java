package org.example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventLogger {
    private final List<String> eventLog = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public void logEvent(String msg) {
        String time = timeFormat.format(System.currentTimeMillis());
        eventLog.add("[" + time + "] " + msg);
    }

    public List<String> getEvents() {
        return new ArrayList<>(eventLog);
    }
}

