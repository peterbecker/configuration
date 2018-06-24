package com.github.peterbecker.configuration.parser;

import lombok.RequiredArgsConstructor;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
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
            return invokeDefaultMethod(proxy, method, args);
        }
        assert args == null;
        return data.get(method.getName());
    }

    // heavily inspired by https://stackoverflow.com/a/49532463/19820
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class);
        constructor.setAccessible(true);
        return constructor.newInstance(method.getDeclaringClass())
                .in(method.getDeclaringClass())
                .unreflectSpecial(method, method.getDeclaringClass())
                .bindTo(proxy)
                .invokeWithArguments(args);
    }
}
