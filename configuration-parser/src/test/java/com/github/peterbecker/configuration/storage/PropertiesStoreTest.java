package com.github.peterbecker.configuration.storage;

public class PropertiesStoreTest extends AbstractStoreTest {
    @Override
    protected String getExtension() {
        return "properties";
    }

    @Override
    protected StoreFactory getStoreFactory() {
        return PropertiesStore::new;
    }
}
