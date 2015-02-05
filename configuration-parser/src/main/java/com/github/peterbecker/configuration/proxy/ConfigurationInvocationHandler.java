package com.github.peterbecker.configuration.proxy;

import com.github.peterbecker.configuration.storage.Key;
import com.github.peterbecker.configuration.storage.Store;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Binds calls to the configuration interface to the Store used.
 */
public class ConfigurationInvocationHandler<T> implements InvocationHandler {
    private final Class<T> configurationInterface;
    private final Store store;

    public ConfigurationInvocationHandler(Class<T> configurationInterface, Store store) {
        this.store = store;
        this.configurationInterface = configurationInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        assert configurationInterface.isAssignableFrom(proxy.getClass());
        assert args == null;

        return store.getValue(new Key(method.getName())).get();
    }
}
