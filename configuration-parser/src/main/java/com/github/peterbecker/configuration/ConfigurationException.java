package com.github.peterbecker.configuration;

import com.github.peterbecker.configuration.storage.Key;

/**
 * An exception that was triggered during configuration processing.
 */
public class ConfigurationException extends Exception {
    private final Key context;

    public ConfigurationException(String message, Key context) {
        super(message);
        this.context = context;
    }

    public ConfigurationException(String message, Throwable cause, Key context) {
        super(message, cause);
        this.context = context;
    }

    @Override
    public String getMessage() {
        if(context == null) {
            return super.getMessage();
        } else {
            return super.getMessage() + " in " + context.toOgnl();
        }
    }
}
