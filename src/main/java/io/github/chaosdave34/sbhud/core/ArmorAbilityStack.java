package io.github.chaosdave34.sbhud.core;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ArmorAbilityStack {

    CRIMSON("Crimson", "Dominus", "ᝐ"),
    TERROR("Terror", "Hydra Strike", "⁑"),
    AURORA("Aurora", "Arcane Vision", "Ѫ"),
    FERVOR("Fervor", "Fervor", "҉"),
    HOLLOW("Hollow", "Spirit", "⚶");

    private final String armorName;
    private final String abilityName;
    private final String symbol;

    @SuppressWarnings("NonFinalFieldInEnum") //lombok plugin moment
    @Setter
    private int currentValue = 0;

    ArmorAbilityStack(String armorName, String abilityName, String symbol) {
        this.armorName = armorName;
        this.abilityName = abilityName;
        this.symbol = symbol;
    }

}
