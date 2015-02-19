package com.github.peterbecker.configuration;

import com.github.peterbecker.configuration.v1.*;

import java.time.LocalDate;

@com.github.peterbecker.configuration.v1.Configuration
public interface TestInterfaceWithDefaults {
    @Option(
            defaultValue = "text"
    )
    String defaultedString();

    @Option(
            defaultValue = "text"
    )
    String setString();

    @Option(
            defaultValue = "7"
    )
    int defaultedInt();

    @Option(
            defaultValue = "7"
    )
    int setInt();

    @Option(
            defaultValue = "2015-02-19"
    )
    LocalDate defaultedDate();

    @Option(
            defaultValue = "2015-02-19"
    )
    LocalDate setDate();

    @Option(
            defaultValue = "1"
    )
    SpecialValueType defaultedSpecialValue();

    @Option(
            defaultValue = "1"
    )
    SpecialValueType setSpecialValue();
}
