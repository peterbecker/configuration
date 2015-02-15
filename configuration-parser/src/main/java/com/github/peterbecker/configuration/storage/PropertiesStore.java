package com.github.peterbecker.configuration.storage;

import lombok.Data;
import lombok.NonNull;

import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * A Store implemented through a Properties object.
 */
@Data
public class PropertiesStore implements Store {
    private final
    @NonNull
    Properties properties;

    @Override
    public Optional<String> getValue(Key key) {
        String propKey;
        if (key.getContext().isEmpty()) {
            propKey = key.getOptionName();
        } else {
            propKey = key.getContext().stream().collect(Collectors.joining(".")) +
                    "." +
                    key.getOptionName();
        }
        return Optional.ofNullable(properties.getProperty(propKey));
    }
}
