package com.github.peterbecker.configuration.v1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marker for an interface method that is used as configuration option.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Option {
    /**
     * Null value for string options since annotations cannot use nulls.
     *
     * Note that we can't use empty stings either since that can be a valid configuration value. The value below is
     * intended to be something that no one wants to use.
     */
    static final String NOT_SET = "_______________NOT_______SET______________";

    /**
     * A default value that is used if the option is not set in the input.
     */
    String defaultValue() default NOT_SET;

    /**
     * A human-readable description of what the option does.
     *
     * Note that this is assumed to be single language (usually English). At this time localization is not supported,
     * if it will it will most likely use resource bundles based on the option names as keys.
     */
    String description() default NOT_SET;
}
