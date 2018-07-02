package com.github.peterbecker.configuration;

import java.time.LocalDate;
import java.util.List;

public interface ListTestInterface {
    List<String> stringValues();
    List<Integer> intValues();
    List<LocalDate> dates();
    List<NestedListTestInterface> nested();
}
