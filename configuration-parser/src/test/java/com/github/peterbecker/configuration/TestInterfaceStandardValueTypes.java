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

    // TODO: other wrappers, date and time
    long requiredPrimitiveLong();
    short requiredPrimitiveShort();
    byte requiredPrimitiveByte();
    float requiredPrimitiveFloat();
    double requiredPrimitiveDouble();
    boolean requiredPrimitiveBoolean();
}
