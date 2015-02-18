package com.github.peterbecker.configuration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
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
}