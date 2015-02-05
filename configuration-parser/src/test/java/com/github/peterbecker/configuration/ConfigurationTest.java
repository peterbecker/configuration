package com.github.peterbecker.configuration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class ConfigurationTest {

    @Test
    public void testFromProperties() throws Exception {
        Path testFile = Paths.get(ConfigurationTest.class.getResource("/testConfig1.properties").toURI());
        TestInterface1 config = Configuration.fromPropertiesFile(TestInterface1.class, testFile);
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo("Two"));
    }

    @Test(
            expected = ConfigurationException.class
    )
    public void testMissingRequiredValue() throws Exception {
        Path testFile = Paths.get(ConfigurationTest.class.getResource("/missingValue.properties").toURI());
        Configuration.fromPropertiesFile(TestInterface1.class, testFile);
    }

    @Test
    public void testOptionalStringValueWhenPresent() throws Exception {
        Path testFile = Paths.get(ConfigurationTest.class.getResource("/testConfig1.properties").toURI());
        TestInterface2 config = Configuration.fromPropertiesFile(TestInterface2.class, testFile);
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo(Optional.of("Two")));
    }

    @Test
    public void testOptionalStringValueWhenAbsent() throws Exception {
        Path testFile = Paths.get(ConfigurationTest.class.getResource("/missingValue.properties").toURI());
        TestInterface2 config = Configuration.fromPropertiesFile(TestInterface2.class, testFile);
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo(Optional.empty()));
    }
}