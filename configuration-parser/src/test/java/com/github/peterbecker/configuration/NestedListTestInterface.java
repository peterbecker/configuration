package com.github.peterbecker.configuration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NestedListTestInterface {
    int nestedInt();
    LocalDate nestedDate();
    Optional<Boolean> nestedOptionalTruth();
    List<String> stringValues();
}
