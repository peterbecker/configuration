package com.github.peterbecker.configuration;

public interface TestInterfaceWithDefaultMethods {
    int someNumber();
    String someThing();

    default String whatDoWeHave() {
        return someNumber() + " " + someThing();
    }

    default int multiplify(int other) {
        return someNumber() * other;
    }
}
