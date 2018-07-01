package com.github.peterbecker.configuration.storage;

import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * A Store implemented through a Properties object.
 */
public class PropertiesStore implements Store {
    private final
    @NonNull
    Properties properties;

    public PropertiesStore(Path resource) throws IOException {
        this.properties = new Properties();
        properties.load(Files.newInputStream(resource));
    }

    public PropertiesStore(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Optional<String> getValue(Key key) {
        return Optional.ofNullable(properties.getProperty(getPropertyKey(key)));
    }

    private String getPropertyKey(Key key) {
        String propKey;
        if (key.isTopLevel()) {
            propKey = key.getOptionName();
        } else {
            propKey = getPropertyKey(key.getContext()) + "." + key.getOptionName();
        }
        if(key.isIndexed()) {
            propKey += "." + key.getIndex();
        }
        return propKey;
    }
}
