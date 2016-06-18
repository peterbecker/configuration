package com.github.peterbecker.configuration.storage;

import com.github.peterbecker.configuration.*;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Base class for testing a store.
 *
 * To use implement the abstract methods and provide an input file for every call to the
 * {@linkplain #getStore(String)} method, using an extension matching your store type.
 */
public abstract class AbstractStoreTest {
    protected Path getResource(String baseName) {
        try {
            return Paths.get(
                    AbstractStoreTest.class.getResource(
                            "/" + baseName + "." + getExtension()
                    ).toURI()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unexpected problem with URI parsing", e);
        }
    }

    protected abstract String getExtension();

    protected abstract StoreFactory getStoreFactory();

    protected Store getStore(String resourceBaseName) throws IOException {
        return getStoreFactory().getStore(getResource(resourceBaseName));
    }

    @Test
    public void testBasicLoad() throws Exception {
        TestInterface1 config = Configuration
                .loadInterface(TestInterface1.class)
                .fromStore(getStore("basic"))
                .done();
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo("Two"));
    }

    @Test(
            expected = ConfigurationException.class
    )
    public void testMissingRequiredValue() throws Exception {
        Configuration
                .loadInterface(TestInterface1.class)
                .fromStore(getStore("missingValue"))
                .done();
    }

    @Test
    public void testOptionalStringValueWhenPresent() throws Exception {
        TestInterface2 config = Configuration
                .loadInterface(TestInterface2.class)
                .fromStore(getStore("basic"))
                .done();
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo(Optional.of("Two")));
    }

    @Test
    public void testOptionalStringValueWhenAbsent() throws Exception {
        TestInterface2 config = Configuration
                .loadInterface(TestInterface2.class)
                .fromStore(getStore("missingValue"))
                .done();
        assertThat(config.someValue(), equalTo("One"));
        assertThat(config.anotherValue(), equalTo(Optional.empty()));
    }

    @Test
    public void testDefaulting() throws Exception {
        TestInterfaceWithDefaults config =
                Configuration
                        .loadInterface(TestInterfaceWithDefaults.class)
                        .fromStore(getStore("defaults"))
                        .withValueParser(SpecialValueType.class, s -> SpecialValueType.fromInt(Integer.parseInt(s)))
                        .done();
        assertThat(config.defaultedString(), equalTo("text"));
        assertThat(config.setString(), equalTo("other text"));
        assertThat(config.defaultedInt(), equalTo(7));
        assertThat(config.setInt(), equalTo(8));
        assertThat(config.defaultedDate(), equalTo(LocalDate.of(2015, 2, 19)));
        assertThat(config.setDate(), equalTo(LocalDate.of(1971, 11, 22)));
        assertThat(config.defaultedSpecialValue(), equalTo(SpecialValueType.ONE));
        assertThat(config.setSpecialValue(), equalTo(SpecialValueType.THREE));
    }

    @Test
    public void testNestedInterfaces() throws Exception {
        NestingTestInterface config =
                Configuration
                        .loadInterface(NestingTestInterface.class)
                        .fromStore(getStore("nesting"))
                        .done();
        assertThat(config.toplevelInt(), CoreMatchers.equalTo(12));
        assertThat(config.toplevelDate(), CoreMatchers.equalTo(LocalDate.of(2012, 2, 3)));
        assertThat(config.nested().nestedInt(), CoreMatchers.equalTo(33));
        assertThat(config.nested().nestedDate(), CoreMatchers.equalTo(LocalDate.of(2011, 11, 11)));
        assertThat(config.nested().nestedOptionalTruth(), CoreMatchers.equalTo(Optional.of(true)));
    }
}