package com.github.peterbecker.configuration.storage;

import lombok.Data;
import lombok.NonNull;

import java.util.Optional;
import java.util.Properties;

/**
 * A Store implemented through a Properties object.
 */
@Data
public class PropertiesStore implements Store {
    private final @NonNull Properties properties;

    @Override
    public Optional<String> getValue(Key key) {
        return Optional.ofNullable(properties.getProperty(key.getOptionName()));
    }
}
