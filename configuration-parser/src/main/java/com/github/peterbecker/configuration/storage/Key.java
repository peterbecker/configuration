package com.github.peterbecker.configuration.storage;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * A reference into a configuration store.
 */
@Data
public class Key {
    /**
     * The sequence of nestings that contextualizes the option.
     */
    private final List<String> context;

    /**
     * The name of the option in the configuration interface.
     */
    private final @NonNull String optionName;
}
