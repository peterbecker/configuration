package com.github.peterbecker.configuration.parser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Binds calls to the configuration interface to the data extracted.
 */
public class ConfigurationInvocationHandler<T> implements InvocationHandler {
    private final Class<T> configurationInterface;
    private final Map<String, Object> data;

    public ConfigurationInvocationHandler(Class<T> configurationInterface, Map<String, Object> data) {
        this.configurationInterface = configurationInterface;
        this.data = data;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        assert configurationInterface.isAssignableFrom(proxy.getClass());
        assert args == null;

        return data.get(method.getName());
    }
}
