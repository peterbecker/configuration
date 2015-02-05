package com.github.peterbecker.configuration.parser;

import com.github.peterbecker.configuration.ConfigurationException;
import com.github.peterbecker.configuration.storage.Key;
import com.github.peterbecker.configuration.storage.Store;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class InterfaceParser {
    /**
     * Map of classes to functions parsing corresponding objects from a string.
     *
     * In this map the two type wildcards are covariant, i.e. the return value of a function in the value position is
     * the class in the key position.
     */
    private static final Map<Class<?>, Function<String, ?>> VALUE_PARSERS = new HashMap<>();
    static {
        VALUE_PARSERS.put(String.class, Function.identity());
    }

    public static<T> ConfigurationInvocationHandler<T> parse(Class<T> configClass, Store store) throws ConfigurationException {
        Map<String, Object> data = new HashMap<>();
        for (Method method : configClass.getMethods()) {
            validateMethod(method);
            Optional<String> value = store.getValue(new Key(method.getName()));

            Class<?> returnType = method.getReturnType();
            if(returnType.equals(Optional.class)) {
                Class<?> actualType = returnType.getTypeParameters()[0].getGenericDeclaration();
                Function<String, ?> valueParser = getValueParser(method, actualType);
                data.put(method.getName(), value.map(valueParser));
            } else {
                if(!value.isPresent()) {
                    throw new ConfigurationException("No value provided for mandatory option " + method.getName());
                }
                Function<String, ?> valueParser = getValueParser(method, returnType);
                data.put(method.getName(), valueParser.apply(value.get()));
            }
        }
        return new ConfigurationInvocationHandler<>(configClass, data);
    }

    @SuppressWarnings("unchecked")
    private static <T> Function<String, T> getValueParser(Method method, Class<T> actualType) throws ConfigurationException {
        Function<String, T> valueParser = (Function<String, T>) VALUE_PARSERS.get(actualType);
        if(valueParser == null) {
            throw new ConfigurationException(
                    String.format(
                            "Can not parse type %s used in return value of %s#%s()",
                            actualType.getName(),
                            method.getClass().getName(),
                            method.getName()
                    )
            );
        }
        return valueParser;
    }

    private static void validateMethod(Method method) throws ConfigurationException {
        if(method.getParameterCount() != 0) {
            throw new ConfigurationException(
                    String.format(
                            "Method %s#%s has parameters, configuration interfaces should not have any",
                            method.getClass().getName(),
                            method.getName()
                    )
            );
        }
    }
}
