package com.webscraping.testask.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateUtil {

    private static final DateTimeFormatter ABSOLUTE_FORMATTER =
            DateTimeFormatter.ofPattern("'Posted on' MMM d, yyyy", Locale.ENGLISH);

    private static final Pattern RELATIVE_PATTERN = Pattern.compile(
            "Posted (\\d+)\\+? (day|week|month|year)s? ago", Pattern.CASE_INSENSITIVE);

    public static long localDateToUnix(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
    }

    public static long dateToUnix(String text) {
        Matcher matcher = RELATIVE_PATTERN.matcher(text);
        if (matcher.matches()) {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            LocalDate now = LocalDate.now();

            return switch (unit) {
                case "day" -> localDateToUnix(now.minusDays(amount));
                case "week" -> localDateToUnix(now.minusWeeks(amount));
                case "month" -> localDateToUnix(now.minusMonths(amount));
                case "year" -> localDateToUnix(now.minusYears(amount));
                default -> throw new IllegalArgumentException("Unsupported time unit: " + unit);
            };
        }

        try {
            return localDateToUnix(LocalDate.parse(text, ABSOLUTE_FORMATTER));
        } catch (Exception e) {
            throw new IllegalArgumentException("Date format not recognized: " + text);
        }
    }
}
