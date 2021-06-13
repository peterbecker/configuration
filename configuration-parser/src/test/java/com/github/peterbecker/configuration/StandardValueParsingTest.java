package com.github.peterbecker.configuration;

import com.github.peterbecker.configuration.storage.PropertiesStore;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("ConstantConditions")
public class StandardValueParsingTest {
    @Test
    public void testStandardValueParsing() throws Exception {
        Path testFile = Paths.get(StandardValueParsingTest.class.getResource("/valueTypes.properties").toURI());
        TestInterfaceStandardValueTypes config = Configuration
                .loadInterface(TestInterfaceStandardValueTypes.class)
                .fromStore(new PropertiesStore(testFile))
                .done();
        assertThat(config.requiredString(), equalTo("Test"));
        assertThat(config.presentOptionalString(), equalTo(Optional.of("Also Test")));
        assertThat(config.absentOptionalString(), equalTo(Optional.<String>empty()));

        assertThat(config.requiredInteger(), equalTo(42));
        assertThat(config.presentOptionalInteger(), equalTo(Optional.of(23)));
        assertThat(config.absentOptionalInteger(), equalTo(Optional.<Integer>empty()));
        assertThat(config.requiredPrimitiveInt(), equalTo(66));

        assertThat(config.requiredLong(), equalTo(23456L));
        assertThat(config.presentOptionalLong(), equalTo(Optional.of(34567L)));
        assertThat(config.absentOptionalLong(), equalTo(Optional.<Long>empty()));
        assertThat(config.requiredPrimitiveLong(), equalTo(12345L));

        assertThat(config.requiredShort(), equalTo((short) 234));
        assertThat(config.presentOptionalShort(), equalTo(Optional.of((short) 345)));
        assertThat(config.absentOptionalShort(), equalTo(Optional.<Short>empty()));
        assertThat(config.requiredPrimitiveShort(), equalTo((short) 123));

        assertThat(config.requiredByte(), equalTo((byte) 12));
        assertThat(config.presentOptionalByte(), equalTo(Optional.of((byte) 21)));
        assertThat(config.absentOptionalByte(), equalTo(Optional.<Byte>empty()));
        assertThat(config.requiredPrimitiveByte(), equalTo((byte) 78));

        assertThat(config.requiredFloat(), equalTo(12.21F));
        assertThat(config.presentOptionalFloat(), equalTo(Optional.of(34.453F)));
        assertThat(config.absentOptionalFloat(), equalTo(Optional.<Float>empty()));
        assertThat(config.requiredPrimitiveFloat(), equalTo(12.34F));

        assertThat(config.requiredDouble(), equalTo(123.434D));
        assertThat(config.presentOptionalDouble(), equalTo(Optional.of(12.1221D)));
        assertThat(config.absentOptionalDouble(), equalTo(Optional.<Double>empty()));
        assertThat(config.requiredPrimitiveDouble(), equalTo(34.56D));

        assertThat(config.requiredBoolean(), equalTo(true));
        assertThat(config.presentOptionalBoolean(), equalTo(Optional.of(false)));
        assertThat(config.absentOptionalBoolean(), equalTo(Optional.<Boolean>empty()));
        assertThat(config.requiredPrimitiveBoolean(), equalTo(true));

        assertThat(config.requiredCharacter(), equalTo('\u263A'));
        assertThat(config.presentOptionalCharacter(), equalTo(Optional.of('\u2603')));
        assertThat(config.absentOptionalCharacter(), equalTo(Optional.<Character>empty()));
        assertThat(config.requiredPrimitiveChar(), equalTo('\u270E'));

        assertThat(config.requiredBigInteger(), equalTo(new BigInteger("1234567890")));
        assertThat(config.presentOptionalBigInteger(), equalTo(Optional.of(new BigInteger("987654321"))));
        assertThat(config.absentOptionalBigInteger(), equalTo(Optional.<BigInteger>empty()));

        assertThat(config.requiredBigDecimal(), equalTo(new BigDecimal("1234567.987654")));
        assertThat(config.presentOptionalBigDecimal(), equalTo(Optional.of(new BigDecimal("9876.12345"))));
        assertThat(config.absentOptionalBigDecimal(), equalTo(Optional.<BigDecimal>empty()));
    }

    /**
     * Tests the types of JSR 310.
     * <p>
     * This is testing one format each to ensure the wiring of the corresponding parse function is ok. We are not trying
     * to test all parse alternatives offered by the JSR 310 APIs. In some cases we are lazy enough to just parse the
     * expected value as well since other constructions are complicated.
     */
    @Test
    public void testDateTime() throws Exception {
        Path testFile = Paths.get(StandardValueParsingTest.class.getResource("/dateTime.properties").toURI());
        TestInterfaceDateAndTime config = Configuration
                .loadInterface(TestInterfaceDateAndTime.class)
                .fromStore(new PropertiesStore(testFile))
                .done();
        assertThat(config.duration(), equalTo(
                Duration.ofMinutes(
                        TimeUnit.DAYS.toMinutes(2) + TimeUnit.HOURS.toMinutes(3) + 4
                )
        ));
        assertThat(config.instant(), equalTo(Instant.parse("2007-12-03T10:15:30.00Z")));
        assertThat(config.localDate(), equalTo(LocalDate.of(2007, 12, 3)));
        assertThat(config.localDateTime(), equalTo(LocalDateTime.of(2007, 12, 3, 10, 15, 30)));
        assertThat(config.localTime(), equalTo(LocalTime.of(10, 15, 30)));
        assertThat(config.monthDay(), equalTo(MonthDay.of(Month.DECEMBER, 3)));
        assertThat(config.offsetDateTime(), equalTo(OffsetDateTime.of(2007, 12, 3, 10, 15, 30, 0, ZoneOffset.ofHours(1))));
        assertThat(config.offsetTime(), equalTo(OffsetTime.of(10, 15, 30, 0, ZoneOffset.ofHours(1))));
        assertThat(config.period(), equalTo(Period.of(1, 2, 25)));
        assertThat(config.year(), equalTo(Year.of(2015)));
        assertThat(config.yearMonth(), equalTo(YearMonth.of(2015, 2)));
        assertThat(config.zonedDateTime(), equalTo(ZonedDateTime.of(2007, 12, 3, 10, 15, 30, 0, ZoneId.of("Europe/Paris"))));
        assertThat(config.zoneId(), equalTo(ZoneId.of("Australia/Brisbane")));
        assertThat(config.zoneOffset(), equalTo(ZoneOffset.ofHoursMinutes(-9, -30)));
    }
}
