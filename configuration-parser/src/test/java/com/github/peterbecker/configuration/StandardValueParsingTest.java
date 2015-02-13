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

        assertThat(config.requiredPrimitiveLong(), equalTo(12345L));
        assertThat(config.requiredPrimitiveShort(), equalTo((short)123));
        assertThat(config.requiredPrimitiveByte(), equalTo((byte)78));
        assertThat(config.requiredPrimitiveFloat(), equalTo(12.34F));
        assertThat(config.requiredPrimitiveDouble(), equalTo(34.56D));
        assertThat(config.requiredPrimitiveBoolean(), equalTo(true));
    }
}
