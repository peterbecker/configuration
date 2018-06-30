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
     * The sequence of nestings that contextualizes the option. Null for root values.
     */
    private final Key context;

    /**
     * The name of the option in the configuration interface.
     */
    private final @NonNull String optionName;

    /**
     * The position within a list. Set to -1 for single values.
     */
    private final int position;

    /**
     * More readable version of null for use as {@linkplain #context}.
     */
    public static final Key ROOT = null;
}
