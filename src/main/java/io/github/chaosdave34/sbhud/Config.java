package io.github.chaosdave34.sbhud;

import gg.essential.api.utils.GuiUtil;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {
    // Skyblock HUD
    @SuppressWarnings("unused")
    @Property(
            type = PropertyType.BUTTON,
            name = "Edit HUD Location",
            category = "Skyblock HUD"
    )
    public void editHudLocation() {
        GuiUtil.open(SBHUD.INSTANCE.getComponentsGui());
    }

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Health Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean healthText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Health Prediction",
            description = "Reverse calculate the player's health by using the player's vanilla hearts.",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean healthPrediction = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Effective Health Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean effectiveHealthText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Healing Wand Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean healingWandText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Mana Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean manaText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Overflow Mana Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean overflowManaText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Defense Text",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean defenseText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Defense Percentage",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean defensePercentage = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Speed Percentage",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean speedPercentage = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Armor Ability Stack",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean armorAbilityStack = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Short Armor Ability Stack",
            description = "Only render armor ability stack with symbol.",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean shortArmorAbilityStack = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Aurora Armor Ability Stack",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean hideAuroraArmorAbilityStack = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Ticker Charge",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean tickerChargesText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Cells Alignment",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean alignmentText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Salvation",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean salvationText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "True Defence",
            description = "Only renders if true defence is shown in actionbar.",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean trueDefenceText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Magic Find",
            description = "Only renders if magic find is shown in tablist.",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean magicFindText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Ferocity",
            description = "Only renders if ferocity is shown in tablist.",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean ferocityText = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Health Bar",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean healthBar = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Mana Bar",
            category = "Skyblock HUD",
            subcategory = "HUD Elements"
    )
    public boolean manaBar = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Change Bar Color for Potions",
            category = "Skyblock HUD",
            subcategory = "Bars"
    )
    public boolean changeBarColorForPotions = false;

    @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "X Scale",
            category = "Skyblock HUD",
            subcategory = "Bars",
            minF = 0.5f,
            maxF = 2f,
            decimalPlaces = 2
    )
    public float barScaleX = 1;

    @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Y Scale",
            category = "Skyblock HUD",
            subcategory = "Bars",
            minF = 0.5f,
            maxF = 2f,
            decimalPlaces = 2
    )
    public float barScaleY = 1;

    public float dummyBarScale = 1;

    // Hud
    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Food Bar",
            description = "Prevents the game from rendering the food bar.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideFoodBar = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Armor Bar",
            description = "Prevents the game from rendering the armor bar.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideArmorBar = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Health Bar",
            description = "Prevents the game from rendering the health bar.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideHealthBar = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Absorptions Hearts",
            description = "Prevents the game from rendering absorption hearts.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideAbsorptionHearts = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Pet Health Bar",
            description = "Prevents the game from rendering the pet health bar.",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hidePetHealthBar = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide True Defense",
            description = "Prevents the game from rendering the true defense number.",
            category = "Skyblock HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideTrueDefense = false;

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
    public boolean disableJumpingHearts = false;

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
    public boolean hidePotionEffects = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Hide Scoreboard Numbers",
            category = "HUD",
            subcategory = "Hide HUD Elements"
    )
    public boolean hideScoreboardNumbers = false;

    // Misc
    @Property(
            type = PropertyType.CHECKBOX,
            name = "Custom App Icon",
            description = "Requires a restart.",
            category = "Miscellaneous"
    )
    public boolean enableCustomAppIcon = false;

    // Item Tooltips
    @Property(
            type = PropertyType.CHECKBOX,
            name = "Show Item ID",
            category = "Item Tooltips"
    )
    public boolean showItemID = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Show Item UUID",
            category = "Item Tooltips"
    )
    public boolean showItemUUID = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Show Item Timestamp",
            category = "Item Tooltips",
            subcategory = "Timestamp"
    )
    public boolean showItemTimestamp = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Nicely format Timestamp",
            category = "Item Tooltips",
            subcategory = "Timestamp"
    )
    public boolean nicelyFormatTimestamp = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Show Origin Tag",
            description = "kinda useless",
            category = "Item Tooltips"
    )
    public boolean showOriginTag = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Show Necromancer Souls",
            category = "Item Tooltips"
    )
    public boolean showNecromancerSouls = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Show Item Tier",
            description = "Shows the dungeon floor this item was obtained.",
            category = "Item Tooltips",
            subcategory = "Dungeon Items"
    )
    public boolean showItemTier = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Show Base Stat Boost Percentage",
            category = "Item Tooltips",
            subcategory = "Dungeon Items"
    )
    public boolean showBaseStatBoostPercentage = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Show Type",
            category = "Item Tooltips",
            subcategory = "Pets"
    )
    public boolean showPetType = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Show Candys Used",
            category = "Item Tooltips",
            subcategory = "Pets"
    )
    public boolean showCandyUsed = false;

    // bug fixes
    @Property(
            type = PropertyType.CHECKBOX,
            name = "Fix Empty Sounds",
            description = "Has no impact on gameplay,",
            category = "Bug Fixes"
    )
    public boolean fixEmptySounds = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Fix Empty Score Player Teams and Objectives",
            description = "Has no impact on gameplay,",
            category = "Bug Fixes"
    )
    public boolean fixEmptyScorePlayerTeamsAndObjectives = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Fix Adding existing Scoreboard Teams",
            description = "Has no impact on gameplay,",
            category = "Bug Fixes"
    )
    public boolean fixAddingExistingScoreboardTeams = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Fix Opening Links on Linux",
            category = "Bug Fixes"
    )
    public boolean fixOpeningLinksOnLinux = false;

    // Spam hider
    @Property(
            type = PropertyType.CHECKBOX,
            name = "Dungeon Buff",
            description = "DUNGEON BUFF! {}; Granted you {}; Also granted you {}",
            category = "Spam Hider",
            subcategory = "Dungeons"
    )
    public boolean hideDungeonBuffMessages = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Dungeon Blessings",
            description = "A Blessing {} was picked up!; {} has obtained Blessing {}",
            category = "Spam Hider",
            subcategory = "Dungeons"
    )
    public boolean hideDungeonBlessingsMessages = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Essence",
            description = "ESSENCE! {}; {} found a {} Essence! Everyone gains an extra essence!",
            category = "Spam Hider",
            subcategory = "Dungeons"
    )
    public boolean hideDungeonEssenceMessages = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Boss Dialogue",
            description = "[Boss] {}",
            category = "Spam Hider",
            subcategory = "Dungeons"
    )
    public boolean hideDungeonBossDialogueMessages = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Milestones",
            description = "{} Milestone {}",
            category = "Spam Hider",
            subcategory = "Dungeons"
    )
    public boolean hideDungeonMilestoneMessages = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Mort Dialogue",
            description = "[Npc] Mort: {}",
            category = "Spam Hider",
            subcategory = "Dungeons"
    )
    public boolean hideDungeonMortDialogueMessages = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Orb Pickup",
            description = "◕ {} picked up your {} Orb!; ◕ You picked up a {} Orb from {}.",
            category = "Spam Hider",
            subcategory = "Dungeons"
    )
    public boolean hideDungeonOrbPickupMessages = false;

    // Dungeons
    @Property(
            type=PropertyType.CHECKBOX,
            name = "Fire Freeze Staff Timer",
            description = "For F3 and M3 boss phase.",
            category = "Dungeons"
    )
    public boolean fireFreezeTimer = false;


    public static Config INSTANCE = new Config();

    public Config() {
        super(new File("./config/" + SBHUD.MODID + ".toml"), SBHUD.MOD_NAME + " " + SBHUD.VERSION);

        initialize();
    }
}
