package com.github.peterbecker.configuration.storage;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class KeyTest {

    @Test
    public void toOgnl() {
        assertThat(new Key(null, "option", -1).toOgnl()).isEqualTo("option");
        assertThat(new Key(null, "option", 3).toOgnl()).isEqualTo("option[3]");
        Key level1 = new Key(null, "top", -1);
        assertThat(new Key(level1, "option", -1).toOgnl()).isEqualTo("top.option");
        assertThat(new Key(level1, "option", 6).toOgnl()).isEqualTo("top.option[6]");
        Key level2 = new Key(level1, "second", 2);
        Key level3 = new Key(level2, "third", 5);
        assertThat(new Key(level3, "opt", -1).toOgnl()).isEqualTo("top.second[2].third[5].opt");
    }
}