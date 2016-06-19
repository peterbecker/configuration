package com.github.peterbecker.configuration.storage;

public class XmlStoreTest extends AbstractStoreTest {
    @Override
    protected String getExtension() {
        return "xml";
    }

    @Override
    protected StoreFactory getStoreFactory() {
        return XmlStore::new;
    }
}
