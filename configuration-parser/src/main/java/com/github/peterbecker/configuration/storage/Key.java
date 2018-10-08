package com.github.peterbecker.configuration.storage;

import lombok.Data;
import lombok.NonNull;

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
    private final @NonNull
    String optionName;

    /**
     * The position within a list. Set to -1 for single values.
     */
    private final int index;

    public boolean isIndexed() {
        return index >= 0;
    }

    /**
     * More readable version of null for use as {@linkplain #context}.
     */
    public static final Key ROOT = null;

    public boolean isTopLevel() {
        return context == Key.ROOT;
    }

    /**
     * Renders the key in Object Graph Navigation Language.
     */
    public String toOgnl() {
        if (isTopLevel()) {
            return localOgnl();
        } else {
            return context.toOgnl() + "." + localOgnl();
        }
    }

    private String localOgnl() {
        if (isIndexed()) {
            return optionName + "[" + index + "]";
        } else {
            return optionName;
        }
    }
}
