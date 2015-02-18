package com.github.peterbecker.configuration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class EnumTest {
    @Test
    public void testEnumParsing() throws Exception {
        Path testFile = Paths.get(StandardValueParsingTest.class.getResource("/enum.properties").toURI());
        EnumTestInterface config = Configuration.loadInterface(EnumTestInterface.class).fromPropertiesFile(testFile).done();
        assertThat(config.enum1(), equalTo(TestEnum1.TWO));
        assertThat(config.enum2(), equalTo(TestEnum2.ALPHA));
        assertThat(config.enum2b(), equalTo(TestEnum2.BETA));
        assertThat(config.optionalPresentEnum(), equalTo(Optional.of(TestEnum1.ONE)));
        assertThat(config.optionalAbsentEnum(), equalTo(Optional.empty()));
    }
}
