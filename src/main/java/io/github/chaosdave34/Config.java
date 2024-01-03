package io.github.chaosdave34;

import gg.essential.api.utils.GuiUtil;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {

    @Property(
            type = PropertyType.BUTTON,
            name = "Edit HUD Location",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public void editHudLocation() {
        GuiUtil.open(SBHUD.getInstance().getComponentsGui());
    }

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Health Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean healthText = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Health Prediction",
            description = "Reverse calculate the player's health by using the player's vanilla hearts.",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean healthPrediction = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Effective Health Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean effectiveHealthText = true;

    @Property(
            type=PropertyType.CHECKBOX,
            name = "Healing Wang Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean healingWandText= true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Mana Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean manaText = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Overflow Mana Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean overflowManaText = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Defense Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean defenseText = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Defense Percentage",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean defensePercentage = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Speed Percentage",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean speedPercentage = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Armor Ability Stack",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean armorAbilityStack = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Short Armor Ability Stack",
            description = "Only render armor ability stack with symbol.",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean shortArmorAbilityStack = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Ticker Charge",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean tickerChargesText = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Cells Alignment",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean alignmentText = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Salvation",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean salvationText = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "True Defence",
            description = "Only renders if true defence is shown in actionbar.",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean trueDefenceText = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Health Bar",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean healthBar = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Mana Bar",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean manaBar = true;

    public boolean changeBarColorForPotions = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Food Bar",
            description = "Prevents the game from rendering the food bar.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideFoodBar = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Armor Bar",
            description = "Prevents the game from rendering the armor bar.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideArmorBar = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Health Bar",
            description = "Prevents the game from rendering the health bar.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideHealthBar = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Absorptions Hearts",
            description = "Prevents the game from rendering absorption hearts.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideAbsorptionHearts = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Pet Health Bar",
            description = "Prevents the game from rendering the pet health bar.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hidePetHealthBar = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide True Defense",
            description = "Prevents the game from rendering the true defense number.",
            category = "Skyblock HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideTrueDefense = true;

    @Property(
            type = PropertyType.NUMBER,
            name = "Move Y-Position of Tool Highlight",
            description = "Adjust the y-position at which the display name of an item is displayed.",
            category = "HUD",
            subcategory = "Modify HUD Elements",
            min = -100,
            max = +100
    )
    public int moveYPositionOfToolHighlight = 0;

    @Property(
            type = PropertyType.NUMBER,
            name = "Move Y-Position of Actionbar",
            description = "Adjust the y-position at which the actionbar is displayed.",
            category = "HUD",
            subcategory = "Modify HUD Elements",
            min = -100,
            max = +100
    )
    public int moveYPositionOfActionbar = 0;

    @Property(
            type = PropertyType.NUMBER,
            name = "Move Y-Position of Chat",
            description = "Adjust the y-position at which the chat is displayed.",
            category = "HUD",
            subcategory = "Modify HUD Elements",
            min = -100,
            max = +100
    )
    public int moveYPositionOfChat = 0;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Disable Jumping Hearts",
            description = "Disables moving hearts when player has regeneration potion active.",
            category = "HUD",
            subcategory = "Modify HUD Elements"
    )
    public boolean disableJumpingHearts = true;

    @Property(
            type = PropertyType.NUMBER,
            name = "Maximum Number of Hearts",
            description = "The maximum number of hearts that should be displayed.",
            category = "HUD",
            subcategory = "Modify HUD Elements",
            min = 1,
            max = 20
    )
    public int maxNumberOfHearts = 20;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Potion Effects",
            description = "Prevents the game from rendering potion effects in inventories.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hidePotionEffects = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Scoreboard Numbers",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideScoreboardNumbers = true;

    public float dummyScaleX = 1;

    public float dummyScaleY = 1;

    public float dummyScale = 1;

    public static Config INSTANCE = new Config();

    public Config() {
        super(new File("./config/" + SBHUD.MODID + ".toml"), SBHUD.MOD_NAME + " " + SBHUD.VERSION);

        initialize();
    }
}
