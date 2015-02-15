package com.github.peterbecker.configuration;

import java.time.LocalDate;
import java.util.Optional;

public interface NestedTestInterface {
    int nestedInt();
    LocalDate nestedDate();
    Optional<Boolean> nestedOptionalTruth();
}
