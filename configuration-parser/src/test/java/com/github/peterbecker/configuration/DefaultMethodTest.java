package com.github.peterbecker.configuration;

import com.github.peterbecker.configuration.storage.PropertiesStore;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultMethodTest {
    @Test
    public void testDefaultMethod() throws ConfigurationException {
        Properties properties = new Properties();
        properties.put("someNumber", "5");
        properties.put("someThing", "frogs");
        TestInterfaceWithDefaultMethods config =
                Configuration.
                        loadInterface(TestInterfaceWithDefaultMethods.class).
                        fromStore(new PropertiesStore(properties)).
                        done();
        assertThat(config.someNumber()).isEqualTo(5);
        assertThat(config.someThing()).isEqualTo("frogs");
        assertThat(config.whatDoWeHave()).isEqualTo("5 frogs");
        assertThat(config.multiplify(3)).isEqualTo(15);
    }
}
