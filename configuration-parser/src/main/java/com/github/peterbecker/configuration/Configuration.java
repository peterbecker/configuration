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
    public static <T> T fromPropertiesFile(Class<T> configurationInterface, Path propertiesFile) throws ConfigurationException {
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(propertiesFile));
        } catch (IOException e) {
            throw new ConfigurationException("Can not read properties file " + propertiesFile.toString(), e);
        }
        return fromProperties(configurationInterface, properties);
    }

    public static <T> T fromProperties(Class<T> configurationInterface, Properties properties) throws ConfigurationException {
        return fromStore(configurationInterface, new PropertiesStore(properties));
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromStore(Class<T> configurationInterface, Store store) throws ConfigurationException {
        return (T) Proxy.newProxyInstance(
                configurationInterface.getClassLoader(),
                new Class[]{configurationInterface},
                InterfaceParser.parse(configurationInterface, store)
        );
    }
}
