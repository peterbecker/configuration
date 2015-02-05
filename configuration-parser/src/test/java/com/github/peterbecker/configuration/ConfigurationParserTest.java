package com.github.peterbecker.configuration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class ConfigurationParserTest {

    @Test
    public void testFromProperties() throws Exception {
        Path testFile = Paths.get(ConfigurationParserTest.class.getResource("/testConfig1.properties").toURI());
        TestInterface1 config = ConfigurationParser.fromPropertiesFile(TestInterface1.class, testFile);
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo("Two"));
    }
}