package com.github.peterbecker.configuration.storage;

import com.github.peterbecker.configuration.*;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Base class for testing a store.
 * <p>
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
        assertThat(config.someValue()).isEqualTo("One");
        assertThat(config.anotherValue()).isEqualTo("Two");
    }

    @Test(
            expected = ConfigurationException.class
    )
    public void testMissingRequiredValue() throws Exception {
        try {
            Configuration
                    .loadInterface(TestInterface1.class)
                    .fromStore(getStore("missingValue"))
                    .done();
        } catch (ConfigurationException e) {
            assertThat(e.getMessage()).isEqualTo("No value provided for mandatory option anotherValue");
            throw e;
        }
    }

    @Test
    public void testOptionalStringValueWhenPresent() throws Exception {
        TestInterface2 config = Configuration
                .loadInterface(TestInterface2.class)
                .fromStore(getStore("basic"))
                .done();
        assertThat(config.someValue()).isEqualTo("One");
        assertThat(config.anotherValue()).isEqualTo(Optional.of("Two"));
    }

    @Test
    public void testOptionalStringValueWhenAbsent() throws Exception {
        TestInterface2 config = Configuration
                .loadInterface(TestInterface2.class)
                .fromStore(getStore("missingValue"))
                .done();
        assertThat(config.someValue()).isEqualTo("One");
        assertThat(config.anotherValue()).isEqualTo(Optional.empty());
    }

    @Test
    public void testDefaulting() throws Exception {
        TestInterfaceWithDefaults config =
                Configuration
                        .loadInterface(TestInterfaceWithDefaults.class)
                        .fromStore(getStore("defaults"))
                        .withValueParser(SpecialValueType.class, s -> SpecialValueType.fromInt(Integer.parseInt(s)))
                        .done();
        assertThat(config.defaultedString()).isEqualTo("text");
        assertThat(config.setString()).isEqualTo("other text");
        assertThat(config.defaultedInt()).isEqualTo(7);
        assertThat(config.setInt()).isEqualTo(8);
        assertThat(config.defaultedDate()).isEqualTo(LocalDate.of(2015, 2, 19));
        assertThat(config.setDate()).isEqualTo(LocalDate.of(1971, 11, 22));
        assertThat(config.defaultedSpecialValue()).isEqualTo(SpecialValueType.ONE);
        assertThat(config.setSpecialValue()).isEqualTo(SpecialValueType.THREE);
    }

    @Test
    public void testNestedInterfaces() throws Exception {
        NestingTestInterface config =
                Configuration
                        .loadInterface(NestingTestInterface.class)
                        .fromStore(getStore("nesting"))
                        .done();
        assertThat(config.toplevelInt()).isEqualTo(12);
        assertThat(config.toplevelDate()).isEqualTo(LocalDate.of(2012, 2, 3));
        assertThat(config.nested().nestedInt()).isEqualTo(33);
        assertThat(config.nested().nestedDate()).isEqualTo(LocalDate.of(2011, 11, 11));
        assertThat(config.nested().nestedOptionalTruth()).isEqualTo(Optional.of(true));
    }

    @Test(
            expected = ConfigurationException.class
    )
    public void testMissingValueNested() throws Exception {
        try {
            Configuration
                    .loadInterface(NestingTestInterface.class)
                    .fromStore(getStore("nestedMissingValue"))
                    .done();
        } catch (ConfigurationException e) {
            assertThat(e.getMessage()).isEqualTo("No value provided for mandatory option nestedDate in nested");
            throw e;
        }
    }

    @Test
    public void testLists() throws Exception {
        ListTestInterface config =
                Configuration
                        .loadInterface(ListTestInterface.class)
                        .fromStore(getStore("lists"))
                        .done();
        assertThat(config.stringValues()).containsExactly(
                "First", "Second", "Third"
        );
        assertThat(config.intValues()).containsExactly(
                7, 5, 5, 7
        );
        assertThat(config.dates()).containsExactly(
                LocalDate.of(1988, 10, 21),
                LocalDate.of(2015, 10, 21)
        );
        assertThat(config.nested()).hasSize(2);
        NestedListTestInterface first = config.nested().get(0);
        assertThat(first.nestedInt()).isEqualTo(456);
        assertThat(first.nestedDate()).isEqualTo(LocalDate.of(2001, 12, 24));
        assertThat(first.nestedOptionalTruth()).isEqualTo(Optional.of(true));
        assertThat(first.stringValues()).containsExactly("001", "007");
        NestedListTestInterface second = config.nested().get(1);
        assertThat(second.nestedInt()).isEqualTo(123);
        assertThat(second.nestedDate()).isEqualTo(LocalDate.of(2001, 12, 31));
        assertThat(second.nestedOptionalTruth()).isEqualTo(Optional.empty());
        assertThat(second.stringValues()).isEmpty();
    }
}