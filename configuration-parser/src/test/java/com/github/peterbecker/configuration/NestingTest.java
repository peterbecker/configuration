package com.github.peterbecker.configuration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class NestingTest {
    @Test
    public void testNestedInterfaces() throws Exception {
        Path testFile = Paths.get(StandardValueParsingTest.class.getResource("/nesting.properties").toURI());
        NestingTestInterface config = Configuration.fromPropertiesFile(NestingTestInterface.class, testFile);
        assertThat(config.toplevelInt(), equalTo(12));
        assertThat(config.toplevelDate(), equalTo(LocalDate.of(2012, 2, 3)));
        assertThat(config.nested().nestedInt(), equalTo(33));
        assertThat(config.nested().nestedDate(), equalTo(LocalDate.of(2011, 11, 11)));
        assertThat(config.nested().nestedOptionalTruth(), equalTo(Optional.of(true)));
    }
}
