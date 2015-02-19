package com.github.peterbecker.configuration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class CoreStuctureParsingTest {

    @Test
    public void testFromProperties() throws Exception {
        Path testFile = Paths.get(CoreStuctureParsingTest.class.getResource("/testConfig1.properties").toURI());
        TestInterface1 config = Configuration.loadInterface(TestInterface1.class).fromPropertiesFile(testFile).done();
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo("Two"));
    }

    @Test(
            expected = ConfigurationException.class
    )
    public void testMissingRequiredValue() throws Exception {
        Path testFile = Paths.get(CoreStuctureParsingTest.class.getResource("/missingValue.properties").toURI());
        Configuration.loadInterface(TestInterface1.class).fromPropertiesFile(testFile).done();
    }

    @Test
    public void testOptionalStringValueWhenPresent() throws Exception {
        Path testFile = Paths.get(CoreStuctureParsingTest.class.getResource("/testConfig1.properties").toURI());
        TestInterface2 config = Configuration.loadInterface(TestInterface2.class).fromPropertiesFile(testFile).done();
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo(Optional.of("Two")));
    }

    @Test
    public void testOptionalStringValueWhenAbsent() throws Exception {
        Path testFile = Paths.get(CoreStuctureParsingTest.class.getResource("/missingValue.properties").toURI());
        TestInterface2 config = Configuration.loadInterface(TestInterface2.class).fromPropertiesFile(testFile).done();
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo(Optional.empty()));
    }

    @Test
    public void testDefaulting() throws Exception {
        Path testFile = Paths.get(CoreStuctureParsingTest.class.getResource("/defaults.properties").toURI());
        TestInterfaceWithDefaults config =
                Configuration.
                        loadInterface(TestInterfaceWithDefaults.class).
                        fromPropertiesFile(testFile).
                        withValueParser(SpecialValueType.class, s -> SpecialValueType.fromInt(Integer.parseInt(s))).
                        done();
        assertThat(config.defaultedString(), equalTo("text"));
        assertThat(config.setString(), equalTo("other text"));
        assertThat(config.defaultedInt(), equalTo(7));
        assertThat(config.setInt(), equalTo(8));
        assertThat(config.defaultedDate(), equalTo(LocalDate.of(2015,2,19)));
        assertThat(config.setDate(), equalTo(LocalDate.of(1971,11,22)));
        assertThat(config.defaultedSpecialValue(), equalTo(SpecialValueType.ONE));
        assertThat(config.setSpecialValue(), equalTo(SpecialValueType.THREE));
    }
}