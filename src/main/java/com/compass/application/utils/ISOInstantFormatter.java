package com.compass.application.utils;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class ISOInstantFormatter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    public static Instant createISOInstant() {
        String formattedInstant = formatter.format(Instant.now());
        return Instant.parse(formattedInstant);
    }
}
