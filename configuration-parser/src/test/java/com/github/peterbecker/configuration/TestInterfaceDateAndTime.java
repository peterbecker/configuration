package com.github.peterbecker.configuration;

import java.time.*;

/**
 * A test interface for types of the Java 8 date and time API (JSR 310).
 *
 * This should be every type in java.time that has a parse(CharSequence) method. Plus ZoneId and ZoneOffset, which have
 * of(CharSequence) in the same role.
 *
 * For simplicity we don't bother with optional values here since that is very much the same as any other optional of
 * a class.
 */
public interface TestInterfaceDateAndTime {
    Duration duration();
    Instant instant();
    LocalDate localDate();
    LocalDateTime localDateTime();
    LocalTime localTime();
    MonthDay monthDay();
    OffsetDateTime offsetDateTime();
    OffsetTime offsetTime();
    Period period();
    Year year();
    YearMonth yearMonth();
    ZonedDateTime zonedDateTime();
    ZoneId zoneId();
    ZoneOffset zoneOffset();
}
