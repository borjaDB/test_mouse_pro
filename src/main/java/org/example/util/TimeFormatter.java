package org.example.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utilidades para formateo de tiempo y fechas.
 */
public class TimeFormatter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE d 'de' MMMM");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("EEEE d 'de' MMMM HH:mm");

    public static String formatElapsedTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getFormattedDate() {
        return DATE_FORMAT.format(new Date());
    }

    public static String getFormattedTime() {
        return TIME_FORMAT.format(new Date());
    }

    public static String getFormattedDateTime() {
        return DATETIME_FORMAT.format(new Date());
    }
}

