package com.github.peterbecker.configuration;

import com.github.peterbecker.configuration.storage.PropertiesStore;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SpecialValueTypeParsingTest {
    @Test
    public void testSpecialValueType() throws Exception {
        Path testFile = Paths.get(StandardValueParsingTest.class.getResource("/specialValueType.properties").toURI());
        SpecialValueTypeTestInterface config =
                Configuration.
                        loadInterface(SpecialValueTypeTestInterface.class).
                        fromStore(new PropertiesStore(testFile)).
                        withValueParser(SpecialValueType.class, s -> SpecialValueType.fromInt(Integer.parseInt(s))).
                        done();
        assertThat(config.test1(), equalTo(SpecialValueType.NOT_SO_MANY));
        assertThat(config.test2(), equalTo(SpecialValueType.ONE));
        assertThat(config.test3(), equalTo(Optional.of(SpecialValueType.TWO)));
        assertThat(config.test4(), equalTo(SpecialValueType.THREE));
        assertThat(config.test5(), equalTo(SpecialValueType.MANY));
        assertThat(config.test6(), equalTo(Optional.empty()));
    }
}
