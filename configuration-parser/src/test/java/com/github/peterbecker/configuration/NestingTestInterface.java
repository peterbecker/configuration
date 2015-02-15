package com.github.peterbecker.configuration;

import java.time.LocalDate;

public interface NestingTestInterface {
    int toplevelInt();
    LocalDate toplevelDate();
    NestedTestInterface nested();
}
