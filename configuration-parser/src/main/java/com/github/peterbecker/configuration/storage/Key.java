package com.github.peterbecker.configuration.storage;

import lombok.Data;
import lombok.NonNull;

/**
 * A reference into a configuration store.
 */
@Data
public class Key {
    /**
     * The name of the option in the configuration interface.
     */
    private final @NonNull String optionName;
}
