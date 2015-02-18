package com.github.peterbecker.configuration;

public enum SpecialValueType {
    ONE, TWO, THREE, MANY, NOT_SO_MANY;

    public static SpecialValueType fromInt(int x) {
        if(x < 1) {
            return NOT_SO_MANY;
        }
        switch (x) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            default:
                return MANY;
        }
    }
}
