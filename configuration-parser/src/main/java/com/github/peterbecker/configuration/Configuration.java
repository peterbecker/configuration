package com.github.peterbecker.configuration;

import com.github.peterbecker.configuration.parser.InterfaceParser;
import com.github.peterbecker.configuration.storage.PropertiesStore;
import com.github.peterbecker.configuration.storage.Store;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * The main entry point for parsing configuration files.
 */
public class Configuration {
    public static <T> StoreCollector<T> loadInterface(Class<T> configurationInterface) {
        return new StoreCollector<>(configurationInterface);
    }

    public static class StoreCollector<T> {
        private final Class<T> configurationInterface;

        public StoreCollector(Class<T> configurationInterface) {
            this.configurationInterface = configurationInterface;
        }

        public ConfigurationBuilder<T> fromPropertiesFile(Path propertiesFile) throws ConfigurationException {
            Properties properties = new Properties();
            try {
                properties.load(Files.newInputStream(propertiesFile));
            } catch (IOException e) {
                throw new ConfigurationException("Can not read properties file " + propertiesFile.toString(), e);
            }
            return fromProperties(properties);
        }

        public ConfigurationBuilder<T> fromProperties(Properties properties) throws ConfigurationException {
            return fromStore(new PropertiesStore(properties));
        }

        public ConfigurationBuilder<T> fromStore(Store store) {
            return new ConfigurationBuilder<>(configurationInterface, store);
        }
    }

    public static class ConfigurationBuilder<T> {
        private final Class<T> configurationInterface;
        private final Store store;

        private ConfigurationBuilder(Class<T> configurationInterface, Store store) {
            this.configurationInterface = configurationInterface;
            this.store = store;
        }

        @SuppressWarnings("unchecked")
        public T done() throws ConfigurationException {
            return (T) Proxy.newProxyInstance(
                    configurationInterface.getClassLoader(),
                    new Class[]{configurationInterface},
                    InterfaceParser.parse(configurationInterface, store)
            );
        }
    }
}
