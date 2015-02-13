package com.github.peterbecker.configuration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class StandardValueParsingTest {
    @Test
    public void testStandardValueParsing() throws Exception {
        Path testFile = Paths.get(CoreStuctureParsingTest.class.getResource("/valueTypes.properties").toURI());
        TestInterfaceStandardValueTypes config = Configuration.fromPropertiesFile(TestInterfaceStandardValueTypes.class, testFile);
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

        assertThat(config.requiredShort(), equalTo((short)234));
        assertThat(config.presentOptionalShort(), equalTo(Optional.of((short)345)));
        assertThat(config.absentOptionalShort(), equalTo(Optional.<Short>empty()));
        assertThat(config.requiredPrimitiveShort(), equalTo((short)123));

        assertThat(config.requiredByte(), equalTo((byte)12));
        assertThat(config.presentOptionalByte(), equalTo(Optional.of((byte)21)));
        assertThat(config.absentOptionalByte(), equalTo(Optional.<Byte>empty()));
        assertThat(config.requiredPrimitiveByte(), equalTo((byte)78));

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
    }
}
