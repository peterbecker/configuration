package com.github.peterbecker.configuration.parser;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Binds calls to the configuration interface to the data extracted.
 */
@RequiredArgsConstructor
public class ConfigurationInvocationHandler<T> implements InvocationHandler {
    private final Class<T> configurationInterface;
    private final Map<String, Object> data;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        assert configurationInterface.isAssignableFrom(proxy.getClass());
        if(method.isDefault()) {
            return InvocationHandler.invokeDefault(proxy, method, args);
        }
        assert args == null;
        return data.get(method.getName());
    }
}
