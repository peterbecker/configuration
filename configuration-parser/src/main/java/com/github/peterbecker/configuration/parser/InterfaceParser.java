package com.github.peterbecker.configuration.parser;

import com.github.peterbecker.configuration.ConfigurationException;
import com.github.peterbecker.configuration.storage.Key;
import com.github.peterbecker.configuration.storage.Store;
import com.github.peterbecker.configuration.v1.Option;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.function.Function;

public class InterfaceParser {
    /**
     * Map of classes to functions parsing corresponding objects from a string.
     * <p/>
     * In this map the two type wildcards are covariant, i.e. the return value of a function in the value position is
     * the class in the key position.
     */
    private static final Map<Class<?>, Function<String, ?>> DEFAULT_VALUE_PARSERS = new HashMap<>();

    static {
        DEFAULT_VALUE_PARSERS.put(String.class, Function.identity());
        DEFAULT_VALUE_PARSERS.put(Integer.class, Integer::valueOf);
        DEFAULT_VALUE_PARSERS.put(Integer.TYPE, Integer::parseInt);
        DEFAULT_VALUE_PARSERS.put(Long.class, Long::valueOf);
        DEFAULT_VALUE_PARSERS.put(Long.TYPE, Long::parseLong);
        DEFAULT_VALUE_PARSERS.put(Short.class, Short::valueOf);
        DEFAULT_VALUE_PARSERS.put(Short.TYPE, Short::parseShort);
        DEFAULT_VALUE_PARSERS.put(Byte.class, Byte::valueOf);
        DEFAULT_VALUE_PARSERS.put(Byte.TYPE, Byte::parseByte);
        DEFAULT_VALUE_PARSERS.put(Float.class, Float::valueOf);
        DEFAULT_VALUE_PARSERS.put(Float.TYPE, Float::parseFloat);
        DEFAULT_VALUE_PARSERS.put(Double.class, Double::valueOf);
        DEFAULT_VALUE_PARSERS.put(Double.TYPE, Double::parseDouble);
        DEFAULT_VALUE_PARSERS.put(Boolean.class, Boolean::valueOf);
        DEFAULT_VALUE_PARSERS.put(Boolean.TYPE, Boolean::parseBoolean);
        DEFAULT_VALUE_PARSERS.put(Character.class, InterfaceParser::getSoleCharacter);
        DEFAULT_VALUE_PARSERS.put(Character.TYPE, InterfaceParser::getSoleCharacter);

        DEFAULT_VALUE_PARSERS.put(BigInteger.class, BigInteger::new);
        DEFAULT_VALUE_PARSERS.put(BigDecimal.class, BigDecimal::new);

        DEFAULT_VALUE_PARSERS.put(Duration.class, Duration::parse);
        DEFAULT_VALUE_PARSERS.put(Instant.class, Instant::parse);
        DEFAULT_VALUE_PARSERS.put(LocalDate.class, LocalDate::parse);
        DEFAULT_VALUE_PARSERS.put(LocalDateTime.class, LocalDateTime::parse);
        DEFAULT_VALUE_PARSERS.put(LocalTime.class, LocalTime::parse);
        DEFAULT_VALUE_PARSERS.put(MonthDay.class, MonthDay::parse);
        DEFAULT_VALUE_PARSERS.put(OffsetDateTime.class, OffsetDateTime::parse);
        DEFAULT_VALUE_PARSERS.put(OffsetTime.class, OffsetTime::parse);
        DEFAULT_VALUE_PARSERS.put(Period.class, Period::parse);
        DEFAULT_VALUE_PARSERS.put(Year.class, Year::parse);
        DEFAULT_VALUE_PARSERS.put(YearMonth.class, YearMonth::parse);
        DEFAULT_VALUE_PARSERS.put(ZonedDateTime.class, ZonedDateTime::parse);
        DEFAULT_VALUE_PARSERS.put(ZoneId.class, ZoneId::of);
        DEFAULT_VALUE_PARSERS.put(ZoneOffset.class, ZoneOffset::of);
    }

    private static char getSoleCharacter(String s) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Missing value");
        }
        if (s.length() > 1) {
            throw new IllegalArgumentException("More than one character");
        }
        return s.charAt(0);
    }

    public static <T> ConfigurationInvocationHandler<T> parse(
            Class<T> configClass, Store store, Map<Class<?>, Function<String, ?>> additionalValueParsers) throws ConfigurationException {
        Map<Class<?>, Function<String, ?>> valueParsers = new HashMap<>();
        valueParsers.putAll(DEFAULT_VALUE_PARSERS);
        valueParsers.putAll(additionalValueParsers);
        return parse(configClass, store, valueParsers, Key.ROOT);
    }

    private static <T> ConfigurationInvocationHandler<T> parse(Class<T> configClass, Store store,
                                                               Map<Class<?>, Function<String, ?>> valueParsers, Key context) throws ConfigurationException {
        Map<String, Object> data = new HashMap<>();
        for (Method method : configClass.getMethods()) {
            if (method.isDefault()) {
                continue;
            }
            validateMethod(method);
            Class<?> returnType = method.getReturnType();
            if (returnType.equals(List.class)) {
                Class<?> actualType = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                List<Object> result = new ArrayList<>();
                int i = 0;
                Optional<String> value;
                do {
                    if (actualType.isInterface()) {
                        Key newContext = new Key(context, method.getName(), i);
                        try {
                            result.add(Proxy.newProxyInstance(
                                    actualType.getClassLoader(),
                                    new Class[]{actualType},
                                    parse(actualType, store, valueParsers, newContext)));
                            i++;
                        } catch (ConfigurationException e) { // inelegant, but we are not too worried about performance here
                            i = -1;
                        }
                    } else {
                        value = store.getValue(new Key(context, method.getName(), i));
                        if (value.isPresent()) {
                            result.add(getValue(value, store, valueParsers, context, method, actualType));
                            i++;
                        } else {
                            i = -1;
                        }
                    }
                } while (i >= 0);
                data.put(method.getName(), result);
            } else {
                Optional<String> value = store.getValue(new Key(context, method.getName(), -1));
                data.put(method.getName(), getValue(value, store, valueParsers, context, method, returnType));
            }
        }
        return new ConfigurationInvocationHandler<>(configClass, data);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static Object getValue(Optional<String> value, Store store, Map<Class<?>, Function<String, ?>> valueParsers, Key context, Method method, Class<?> returnType) throws ConfigurationException {
        Object valueToStore;
        if (returnType.isInterface()) {
            Key newContext = new Key(context, method.getName(), -1);
            valueToStore = Proxy.newProxyInstance(
                    returnType.getClassLoader(),
                    new Class[]{returnType},
                    parse(returnType, store, valueParsers, newContext)
            );
        } else if (returnType.isEnum() && !valueParsers.containsKey(returnType)) {
            if (!value.isPresent()) {
                throw new ConfigurationException("No value provided for mandatory option " + method.getName());
            }
            try {
                Method valueOf = returnType.getMethod("valueOf", String.class);
                valueToStore = valueOf.invoke(null, value.get());
            } catch (NoSuchMethodException e) {
                // this should never happen, but we re-throw to be sure we know if it does
                throw new ConfigurationException("Internal error, can not find valueOf method on enum", e);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new ConfigurationException("Can not find value " + value.get() + " for enum " + returnType.getCanonicalName());
            }
        } else if (returnType.equals(Optional.class)) {
            Class<?> actualType = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            if (actualType.isEnum() && !valueParsers.containsKey(actualType)) {
                try {
                    Method valueOf = actualType.getMethod("valueOf", String.class);
                    valueToStore = value.isPresent() ? Optional.of(valueOf.invoke(null, value.get())) : Optional.empty();
                } catch (NoSuchMethodException e) {
                    // this should never happen, but we re-throw to be sure we know if it does
                    throw new ConfigurationException("Internal error, can not find valueOf method on enum", e);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new ConfigurationException("Can not find value " + value.get() + " for enum " + returnType.getCanonicalName());
                }
            } else {
                Function<String, ?> valueParser = getValueParser(method, actualType, valueParsers);
                valueToStore = value.map(valueParser);
            }
        } else {
            String realValue;
            if (value.isPresent()) {
                realValue = value.get();
            } else {
                Option optionAnnotation = method.getAnnotation(Option.class);
                if (optionAnnotation != null && !optionAnnotation.defaultValue().equals(Option.NOT_SET)) {
                    realValue = optionAnnotation.defaultValue();
                } else {
                    throw new ConfigurationException("No value provided for mandatory option " + method.getName());
                }
            }
            Function<String, ?> valueParser = getValueParser(method, returnType, valueParsers);
            valueToStore = valueParser.apply(realValue);
        }
        return valueToStore;
    }

    @SuppressWarnings("unchecked")
    private static <T> Function<String, T> getValueParser(Method method, Class<T> actualType,
                                                          Map<Class<?>, Function<String, ?>> valueParsers) throws ConfigurationException {
        Function<String, T> valueParser = (Function<String, T>) valueParsers.get(actualType);
        if (valueParser == null) {
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
        if (method.getParameterCount() != 0) {
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
