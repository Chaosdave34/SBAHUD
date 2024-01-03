package io.github.chaosdave34;

import com.google.gson.Gson;
import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.state.BasicState;
import gg.essential.elementa.state.State;
import io.github.chaosdave34.utils.ColorCode;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ComponentsGui extends WindowScreen {
    private final Gson gson = new Gson();
    private String configPath = SBHUD.getConfigDir() + "/" + SBHUD.MODID + "_HUD.json";

    public State<String> healtTextState = new BasicState<>("0");

    public UIComponent healthText = new MoveableUIText("HEALTH_TEXT", "100/100")
            .bindText(healtTextState)
            .setColor(ColorCode.RED.getColorObject())
            .setX(new CenterConstraint())
            .setY(new CenterConstraint())
            .setChildOf(getWindow());

    public State<String> effectiveHealtTextState = new BasicState<>("0");

    public UIComponent effectiveHealthText = new MoveableUIText("EFFECTIVE_HEALTH_TEXT", "100")
            .bindText(effectiveHealtTextState)
            .setColor(ColorCode.RED.getColorObject())
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(120))
            .setChildOf(getWindow());

    public State<String> healingWandTextState = new BasicState<>("0");

    public UIComponent healingWandTextText = new MoveableUIText("HEALING_WAND_TEXT", "+100▆")
            .bindText(healingWandTextState)
            .setColor(ColorCode.RED.getColorObject())
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(120))
            .setChildOf(getWindow());

    public State<String> manaTextState = new BasicState<>("0");

    public UIComponent manaText = new MoveableUIText("MANA_TEXT", "100/100")
            .bindText(manaTextState)
            .setColor(ColorCode.BLUE.getColorObject())
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(20))
            .setChildOf(getWindow());

    public State<String> overflowManaTextState = new BasicState<>("0");

    public UIComponent overflowManaText = new MoveableUIText("OVERFLOW_MANA_TEXT", "100ʬ")
            .bindText(overflowManaTextState)
            .setColor(ColorCode.DARK_AQUA.getColorObject())
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(40))
            .setChildOf(getWindow());

    public State<String> defenseTextState = new BasicState<>("0");

    public UIComponent defenseText = new MoveableUIText("DEFENSE_TEXT", "100")
            .bindText(defenseTextState)
            .setColor(ColorCode.GREEN.getColorObject())
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(60))
            .setChildOf(getWindow());

    public State<String> defensePercentageState = new BasicState<>("0");

    public UIComponent defensePercentage = new MoveableUIText("DEFENSE_PERCENTAGE", "100%")
            .bindText(defensePercentageState)
            .setColor(ColorCode.GREEN.getColorObject())
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(80))
            .setChildOf(getWindow());

    public State<String> speedPercentageState = new BasicState<>("0%");

    public UIComponent speedPercentage = new MoveableUIText("SPEED_PERCENTAGE", "100%")
            .bindText(speedPercentageState)
            .setColor(Color.WHITE)
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(100))
            .setChildOf(getWindow());

    public State<String> armorAbilityStackState = new BasicState<>("0%");

    public UIComponent armorAbilityStack = new MoveableUIText("ARMOR_ABILITY_STACK", "10ᝐ Dominus")
            .bindText(armorAbilityStackState)
            .setColor(ColorCode.GOLD.getColorObject())
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(140))
            .setChildOf(getWindow());

    public State<String> tickerTextState = new BasicState<>("");

    public UIComponent tickerText = new MoveableUIText("TICKER_TEXT", "§a§lⓄⓄⓄⓄⓄ")
            .bindText(tickerTextState)
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(160))
            .setChildOf(getWindow());

    public UIComponent alignmentText = new MoveableUIText("ALIGNMENT_TEXT", "|||")
            .setText("|||")
            .setColor(ColorCode.GREEN.getColorObject())
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(200))
            .setChildOf(getWindow());

    public State<String> salvationTextState = new BasicState<>("");

    public UIComponent salvationText = new MoveableUIText("SALVATION_TEXT", "§a§lT3!")
            .bindText(salvationTextState)
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(160))
            .setChildOf(getWindow());

    public State<String> trueDefenceTextState = new BasicState<>("");

    public UIComponent trueDefenceText = new MoveableUIText("TRUE_DEFENCE_TEXT", "100❂")
            .bindText(trueDefenceTextState)
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(220))
            .setChildOf(getWindow());

    public UIComponent healthBar = new MoveableUIContainer("HEALTH_BAR")
            .setX(new PixelConstraint(100))
            .setY(new CenterConstraint())
            .setHeight(new PixelConstraint(5))
            .setWidth(new PixelConstraint(71))
            .setChildOf(getWindow());

    public UIComponent manaBar = new MoveableUIContainer("MANA_BAR")
            .setX(new PixelConstraint(200))
            .setY(new CenterConstraint())
            .setHeight(new PixelConstraint(5))
            .setWidth(new PixelConstraint(71))
            .setChildOf(getWindow());


    UIComponent[] components = new UIComponent[]{
            healthText,
            effectiveHealthText,
            manaText,
            overflowManaText,
            defenseText,
            defensePercentage,
            speedPercentage,
            armorAbilityStack,
            tickerText,
            alignmentText,
            salvationText,
            trueDefenceText,
            healthBar,
            manaBar
    };

    public ComponentsGui() {
        super(ElementaVersion.V5);
        loadLocations();
    }


    private void loadLocations() {
        try {
            HashMap<String, ArrayList<Double>> locations = gson.fromJson(new FileReader(configPath), HashMap.class);
            if (locations != null) {
                for (UIComponent component : components) {
                    float x = locations.get(((IMoveableUIContainer) component).getName()).get(0).floatValue();
                    float y = locations.get(((IMoveableUIContainer) component).getName()).get(1).floatValue();
                    component.setX(new PixelConstraint(x));
                    component.setY(new PixelConstraint(y));
                }
            }
        } catch (FileNotFoundException e) {
            SBHUD.logger.info("HUD config file does not exist. Creating now...");
            saveLocations();
        }
    }

    private void saveLocations() {
        HashMap<String, float[]> locations = new HashMap<>();

        for (UIComponent component : components) {
            locations.put(((IMoveableUIContainer) component).getName(), new float[]{component.getLeft(), component.getTop()});
        }

        try {
            FileWriter fileWriter = new FileWriter(configPath);
            gson.toJson(locations, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            SBHUD.logger.error("Could not write to HUD config file.");
        }
    }

    @Override
    public void onScreenClose() {
        super.onScreenClose();
        saveLocations();
    }
}
