package com.github.peterbecker.configuration.storage;

import java.io.IOException;
import java.nio.file.Path;

public interface StoreFactory {
    Store getStore(Path resource) throws IOException;
}
