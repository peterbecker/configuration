package com.github.peterbecker.configuration.storage;

public class JsonStoreTest extends AbstractStoreTest {

    @Override
    protected String getExtension() {
        return "json";
    }

    @Override
    protected StoreFactory getStoreFactory() {
        return JsonStore::new;
    }
}