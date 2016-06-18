package com.github.peterbecker.configuration.storage;

public class YamlStoreTest extends AbstractStoreTest {

    @Override
    protected String getExtension() {
        return "yaml";
    }

    @Override
    protected StoreFactory getStoreFactory() {
        return YamlStore::new;
    }
}