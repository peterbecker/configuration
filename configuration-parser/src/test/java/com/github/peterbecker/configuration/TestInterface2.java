package com.github.peterbecker.configuration;

import com.github.peterbecker.configuration.v1.Configuration;

import java.util.Optional;

@Configuration
public interface TestInterface2 {
    String someValue();
    Optional<String> anotherValue();
}
