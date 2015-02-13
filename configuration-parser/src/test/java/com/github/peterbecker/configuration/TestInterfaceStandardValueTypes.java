package com.github.peterbecker.configuration;

import java.util.Optional;

/**
 * An interface covering all the standard value types supported.
 *
 * Each has three variants: required, optional but present, optional and absent.
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

    // TODO: date and time
}
