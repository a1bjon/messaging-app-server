package com.xepr.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    private DateUtil() {
    }

    public static String getCurrentDateTime() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy").format(LocalDateTime.now());
    }

    public static String getCurrentDate() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now());
    }

    public static String getCurrentTime() {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
    }

    public static long getUnixTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static long getTimestampFromDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .getEpochSecond();
    }
}
