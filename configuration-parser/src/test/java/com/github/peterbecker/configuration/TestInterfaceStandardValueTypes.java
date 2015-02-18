package com.github.peterbecker.configuration;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

/**
 * An interface covering all the standard value types supported.
 *
 * Most have three or four variants: required, optional but present, optional and absent; plus maybe a primitive version
 * (which is always required).
 *
 * Date and time are in a separate test interface {@linkplain com.github.peterbecker.configuration.TestInterfaceDateAndTime}.
 */
public interface TestInterfaceStandardValueTypes {
    String requiredString();
    Optional<String> presentOptionalString();
    Optional<String> absentOptionalString();

    Integer requiredInteger();
    Optional<Integer> presentOptionalInteger();
    Optional<Integer> absentOptionalInteger();
    int requiredPrimitiveInt();

    Long requiredLong();
    Optional<Long> presentOptionalLong();
    Optional<Long> absentOptionalLong();
    long requiredPrimitiveLong();

    Short requiredShort();
    Optional<Short> presentOptionalShort();
    Optional<Short> absentOptionalShort();
    short requiredPrimitiveShort();

    Byte requiredByte();
    Optional<Byte> presentOptionalByte();
    Optional<Byte> absentOptionalByte();
    byte requiredPrimitiveByte();

    Float requiredFloat();
    Optional<Float> presentOptionalFloat();
    Optional<Float> absentOptionalFloat();
    float requiredPrimitiveFloat();

    Double requiredDouble();
    Optional<Double> presentOptionalDouble();
    Optional<Double> absentOptionalDouble();
    double requiredPrimitiveDouble();

    Boolean requiredBoolean();
    Optional<Boolean> presentOptionalBoolean();
    Optional<Boolean> absentOptionalBoolean();
    boolean requiredPrimitiveBoolean();

    Character requiredCharacter();
    Optional<Character> presentOptionalCharacter();
    Optional<Character> absentOptionalCharacter();
    char requiredPrimitiveChar();

    Color requiredAWTColor();
    Optional<Color> presentOptionalAWTColor();
    Optional<Color> absentOptionalAWTColor();

    javafx.scene.paint.Color requiredJavaFXColor();
    Optional<javafx.scene.paint.Color> presentOptionalJavaFXColor();
    Optional<javafx.scene.paint.Color> absentOptionalJavaFXColor();

    BigInteger requiredBigInteger();
    Optional<BigInteger> presentOptionalBigInteger();
    Optional<BigInteger> absentOptionalBigInteger();

    BigDecimal requiredBigDecimal();
    Optional<BigDecimal> presentOptionalBigDecimal();
    Optional<BigDecimal> absentOptionalBigDecimal();
}
