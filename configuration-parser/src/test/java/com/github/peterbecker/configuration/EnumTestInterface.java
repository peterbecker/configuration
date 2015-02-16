package com.github.peterbecker.configuration;

import java.util.Optional;

public interface EnumTestInterface {
    TestEnum1 enum1();
    TestEnum2 enum2();
    TestEnum2 enum2b();
    Optional<TestEnum1> optionalPresentEnum();
    Optional<TestEnum1> optionalAbsentEnum();
}
