package com.github.peterbecker.configuration.storage;

import lombok.NonNull;

import java.util.Optional;

/**
 * A place where configuration data is stored.
 */
public interface Store {
    Optional<String> getValue(@NonNull Key key); // TODO: decided if we switch to JSR-305
}
