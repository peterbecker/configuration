package com.github.peterbecker.configuration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

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
    public void testMissingValue() throws Exception {
        Path testFile = Paths.get(ConfigurationTest.class.getResource("/missingValue.properties").toURI());
        Configuration.fromPropertiesFile(TestInterface1.class, testFile);
    }
}