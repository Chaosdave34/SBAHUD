package io.github.chaosdave34.sbhud.core;

import lombok.Getter;

@Getter
public enum Attribute {

    DEFENCE(0),
    HEALTH(100),
    MAX_HEALTH(100),
    MANA(100),
    MAX_MANA(100),
    OVERFLOW_MANA(20);

    private final float defaultValue;

    Attribute(float defaultValue) {
        this.defaultValue = defaultValue;
    }
}
