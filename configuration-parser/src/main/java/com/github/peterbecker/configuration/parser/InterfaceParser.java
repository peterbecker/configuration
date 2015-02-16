package com.github.peterbecker.configuration.parser;

import com.github.peterbecker.configuration.ConfigurationException;
import com.github.peterbecker.configuration.storage.Key;
import com.github.peterbecker.configuration.storage.Store;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
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
    private static final Map<Class<?>, Function<String, ?>> VALUE_PARSERS = new HashMap<>();

    static {
        VALUE_PARSERS.put(String.class, Function.identity());
        VALUE_PARSERS.put(Integer.class, Integer::valueOf);
        VALUE_PARSERS.put(Integer.TYPE, Integer::parseInt);
        VALUE_PARSERS.put(Long.class, Long::valueOf);
        VALUE_PARSERS.put(Long.TYPE, Long::parseLong);
        VALUE_PARSERS.put(Short.class, Short::valueOf);
        VALUE_PARSERS.put(Short.TYPE, Short::parseShort);
        VALUE_PARSERS.put(Byte.class, Byte::valueOf);
        VALUE_PARSERS.put(Byte.TYPE, Byte::parseByte);
        VALUE_PARSERS.put(Float.class, Float::valueOf);
        VALUE_PARSERS.put(Float.TYPE, Float::parseFloat);
        VALUE_PARSERS.put(Double.class, Double::valueOf);
        VALUE_PARSERS.put(Double.TYPE, Double::parseDouble);
        VALUE_PARSERS.put(Boolean.class, Boolean::valueOf);
        VALUE_PARSERS.put(Boolean.TYPE, Boolean::parseBoolean);
        VALUE_PARSERS.put(Character.class, InterfaceParser::getSoleCharacter);
        VALUE_PARSERS.put(Character.TYPE, InterfaceParser::getSoleCharacter);

        VALUE_PARSERS.put(Color.class, InterfaceParser::decodeAwtColor);
        VALUE_PARSERS.put(javafx.scene.paint.Color.class, javafx.scene.paint.Color::valueOf);

        VALUE_PARSERS.put(Duration.class, Duration::parse);
        VALUE_PARSERS.put(Instant.class, Instant::parse);
        VALUE_PARSERS.put(LocalDate.class, LocalDate::parse);
        VALUE_PARSERS.put(LocalDateTime.class, LocalDateTime::parse);
        VALUE_PARSERS.put(LocalTime.class, LocalTime::parse);
        VALUE_PARSERS.put(MonthDay.class, MonthDay::parse);
        VALUE_PARSERS.put(OffsetDateTime.class, OffsetDateTime::parse);
        VALUE_PARSERS.put(OffsetTime.class, OffsetTime::parse);
        VALUE_PARSERS.put(Period.class, Period::parse);
        VALUE_PARSERS.put(Year.class, Year::parse);
        VALUE_PARSERS.put(YearMonth.class, YearMonth::parse);
        VALUE_PARSERS.put(ZonedDateTime.class, ZonedDateTime::parse);
        VALUE_PARSERS.put(ZoneId.class, ZoneId::of);
        VALUE_PARSERS.put(ZoneOffset.class, ZoneOffset::of);
    }

    /**
     * Method to decode an AWT color encoded the JavaFX/web way.
     * <p/>
     * AWT has Color::decode, but that needs a full three byte integer value. JavaFX allows all CSS variants, including
     * names.
     */
    private static Color decodeAwtColor(String s) {
        javafx.scene.paint.Color color = javafx.scene.paint.Color.valueOf(s);
        return new Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity());
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

    public static <T> ConfigurationInvocationHandler<T> parse(Class<T> configClass, Store store) throws ConfigurationException {
        return parse(configClass, store, Collections.emptyList());
    }

    private static <T> ConfigurationInvocationHandler<T> parse(Class<T> configClass, Store store, List<String> context) throws ConfigurationException {
        Map<String, Object> data = new HashMap<>();
        for (Method method : configClass.getMethods()) {
            validateMethod(method);
            Optional<String> value = store.getValue(new Key(context, method.getName()));

            Class<?> returnType = method.getReturnType();
            if (returnType.isInterface()) {
                List<String> newContext = new ArrayList<>(context);
                newContext.add(method.getName());
                data.put(method.getName(),
                        Proxy.newProxyInstance(
                                returnType.getClassLoader(),
                                new Class[]{returnType},
                                parse(returnType, store, newContext)
                        ));
            } else if (returnType.isEnum()) {
                if (!value.isPresent()) {
                    throw new ConfigurationException("No value provided for mandatory option " + method.getName());
                }
                try {
                    Method valueOf = returnType.getMethod("valueOf", String.class);
                    data.put(method.getName(), valueOf.invoke(null, value.get()));
                } catch (NoSuchMethodException e) {
                    // this should never happen, but we re-throw to be sure we know if it does
                    throw new ConfigurationException("Internal error, can not find valueOf method on enum", e);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new ConfigurationException("Can not find value " + value.get() + " for enum " + returnType.getCanonicalName());
                }
            } else if (returnType.equals(Optional.class)) {
                Class<?> actualType = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                if (actualType.isEnum()) {
                    try {
                        Method valueOf = actualType.getMethod("valueOf", String.class);
                        data.put(
                                method.getName(),
                                value.isPresent() ? Optional.of(valueOf.invoke(null, value.get())) : Optional.empty()
                        );
                    } catch (NoSuchMethodException e) {
                        // this should never happen, but we re-throw to be sure we know if it does
                        throw new ConfigurationException("Internal error, can not find valueOf method on enum", e);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new ConfigurationException("Can not find value " + value.get() + " for enum " + returnType.getCanonicalName());
                    }
                } else {
                    Function<String, ?> valueParser = getValueParser(method, actualType);
                    data.put(method.getName(), value.map(valueParser));
                }
            } else {
                if (!value.isPresent()) {
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
