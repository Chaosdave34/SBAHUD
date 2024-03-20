package io.github.chaosdave34.features;

import io.github.chaosdave34.SBHUD;
import io.github.chaosdave34.core.ArmorAbilityStack;
import io.github.chaosdave34.core.Attribute;
import io.github.chaosdave34.utils.TextUtils;
import io.github.chaosdave34.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to parse action bar messages and get stats and other info out of them.
 * Parses things like health, defense, mana, skill xp, item ability tickers and
 * if they are displayed else where by SBA, removes them from the action bar.
 * <p>
 * Action bars can take many shapes, but they're always divided into sections separated by 3 or more spaces
 * (usually 5, zombie tickers by 4, race timer by 12, trials of fire by 3).
 * Here are some examples:
 * <p>
 * Normal:                     §c1390/1390❤     §a720§a❈ Defense     §b183/171✎ Mana§r
 * Normal with Skill XP:       §c1390/1390❤     §3+10.9 Combat (313,937.1/600,000)     §b183/171✎ Mana§r
 * Zombie Sword:               §c1390/1390❤     §a725§a❈ Defense     §b175/233✎ Mana    §a§lⓩⓩⓩⓩ§2§l§r
 * Zombie Sword with Skill XP: §c1390/1390❤     §3+10.9 Combat (313,948/600,000)     §b187/233✎ Mana    §a§lⓩⓩⓩⓩ§2§l§r
 * Normal with Wand:           §c1390/1390❤+§c30▅     §a724§a❈ Defense     §b97/171✎ Mana§r
 * Normal with Absorption:     §61181/1161❤     §a593§a❈ Defense     §b550/550✎ Mana§r
 * Normal with Absorp + Wand:  §61181/1161❤+§c20▆     §a593§a❈ Defense     §b501/550✎ Mana§r
 * End Race:                   §d§lTHE END RACE §e00:52.370            §b147/147✎ Mana§r
 * Woods Race:                 §A§LWOODS RACING §e00:31.520            §b147/147✎ Mana§r
 * Trials of Fire:             §c1078/1078❤   §610 DPS   §c1 second     §b421/421✎ Mana§r
 * Soulflow:                   §b421/421✎ §3100ʬ
 * Tethered + Alignment:      §a1039§a❈ Defense§a |||§a§l  T3!
 * Five stages of healing wand:     §62151/1851❤+§c120▆
 * §62151/1851❤+§c120▅
 * §62151/1851❤+§c120▄
 * §62151/1851❤+§c120▃
 * §62151/1851❤+§c120▂
 * §62151/1851❤+§c120▁
 * <p>
 * To add something new to parse, add an else-if case in {@link #parseActionBar(String)} to call a method that
 * parses information from that section.
 */

@Getter
public class ActionBarParser {
    private static final Pattern MANA_PATTERN = Pattern.compile("(?<num>[0-9,.]+)/(?<den>[0-9,.]+)✎(| Mana| (?<overflow>-?[0-9,.]+)ʬ)");
    private static final Pattern DEFENSE_PATTERN = Pattern.compile("(?<defense>[0-9,.]+)❈ Defense");
    private static final Pattern TRUE_DEFENSE_PATTERN = Pattern.compile("(?<trueDefense>[0-9,.]+)❂ True Defense");
    private static final Pattern HEALTH_PATTERN = Pattern.compile("(?<health>[0-9,.]+)/(?<maxHealth>[0-9,.]+)❤(?<wand>\\+(?<wandHeal>[0-9,.]+)[▆▅▄▃▂▁])?");
    private static final Pattern SALVATION_PATTERN = Pattern.compile("(§6|§a§l) {2}(T[1-3]+!?)");

    private static final SBHUD main = SBHUD.INSTANCE;
    private static final Logger logger = SBHUD.logger;

    private String tickerText;
    private boolean isAligned;
    private String salvationText;
    private float trueDefence;
    private String healingWandText;

    @Setter
    private float lastSecondHealth = -1;
    @Setter
    private Float healthUpdate;
    @Setter
    private long lastHealthUpdate;

    private boolean healthLock;

    private final LinkedList<String> stringsToRemove = new LinkedList<>();

    /**
     * Parses the stats out of an action bar message and returns a new action bar message without the parsed stats
     * to display instead.
     * Looks for Health, Defense, Mana, Skill XP and parses and uses the stats accordingly.
     * Only removes the stats from the new action bar when their separate display features are enabled.
     *
     * @param actionBar Formatted action bar message
     */
    public void parseActionBar(String actionBar) {
        // First split the action bar into sections
        String[] splitMessage = actionBar.split(" {3,}");
        // This list holds the text of unused sections that aren't displayed anywhere else in SBA,
        // so they can keep being displayed in the action bar
        stringsToRemove.clear();

        // health and mana section methods determine if prediction can be disabled, so enable both at first
        main.getRenderListener().setPredictMana(true);
        main.getRenderListener().setPredictHealth(true);
        tickerText = null;
        trueDefence = -1;
        healingWandText = null;

        // If the action bar is displaying player stats and the defense section is absent, the player's defense is zero.
        if (actionBar.contains("❤") && !actionBar.contains("❈") && splitMessage.length == 2) {
            setAttribute(Attribute.DEFENCE, 0);
        }

        ArrayList<String> newSplitMessages = new ArrayList<>();

        List<String> messages = Arrays.asList(splitMessage);
        for (String msg : messages) {
            if (msg.contains("❈") || msg.contains("❂")) {
                salvationText = null;
                isAligned = false;
                messages = messages.subList(1, messages.size());

                List<String> splits = Arrays.asList(msg.split("Defense"));
                String defense = splits.get(0) + "Defense";
                newSplitMessages.add(defense);

                msg = String.join("", splits.subList(1, splits.size()));

                if (msg.contains("|||")) {
                    splits = Arrays.asList(msg.split("\\|\\|\\|"));

                    newSplitMessages.add(splits.get(0) + "|||");

                    if (splits.size() > 1) {
                        newSplitMessages.add(splits.get(1));
                    }
                } else {
                    newSplitMessages.add(msg);
                }

            } else {
                newSplitMessages.add(msg);
            }
        }

        splitMessage = newSplitMessages.toArray(new String[0]);

        for (String section : splitMessage) {
            try {
                if (parseSection(section) == null) {
                    // Remove via callback
                    stringsToRemove.add(section);
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Parses a single section of the action bar.
     *
     * @param section Section to parse
     * @return Text to keep displaying or null
     */
    private String parseSection(String section) {
        String stripColoring = TextUtils.stripColor(section);
        String convertMag;

        try {
            convertMag = TextUtils.convertMagnitudes(stripColoring);

            // Format for overflow mana is a bit different. Splitstats must parse out overflow first before getting numbers
            if (section.contains("ʬ")) {
                convertMag = convertMag.split(" ")[0];
            }
            String numbersOnly = TextUtils.getNumbersOnly(convertMag).trim(); // keeps numbers and slashes
            String[] splitStats = numbersOnly.split("/");

            if (section.contains("❤")) {
                // cutting the crimson stack information out
                section = parseArmorAbilityStack(section);

                // Fixing health when glare damage (from magma boss in crimson isle) is displayed.
                // Glare damage stays in the action bar normally
                if (section.endsWith("ಠ")) {
                    if (section.contains("Glare Damage")) {
                        section = section.split(Pattern.quote("§6 "))[0];
                    }
                }

                // ❤ indicates a health section
                return parseHealth(section);
            } else if (section.contains("❈")) {
                // ❈ indicates a defense section
                return parseDefense(section);
            } else if (section.endsWith("§f❂ True Defense")) {
                return parseTrueDefence(section);
            } else if (section.contains("✎")) {
                return parseMana(section);
            } else if (section.contains("Ⓞ") || section.contains("ⓩ")) {
                return parseTickers(section);
            } else if (section.matches(SALVATION_PATTERN.pattern())) {
                return parseSalvation(section);
            } else if (section.contains("|||")) {
                return parseAligned(section);
            }
        } catch (ParseException e) {
            logger.error("The section \"" + section + "\" will be skipped due to an error during number parsing.");
            logger.error("Failed to parse number at offset " + e.getErrorOffset() + " in string \"" + e.getMessage() + "\".", e);
            return section;
        }

        return section;
    }

    private String parseTrueDefence(String trueDefenceSection) {
        String stripped = TextUtils.stripColor(trueDefenceSection);
        Matcher m = TRUE_DEFENSE_PATTERN.matcher(stripped);
        if (m.matches()) {
            trueDefence = TextUtils.parseFloat(m.group("trueDefense"));
            if (SBHUD.config.trueDefenceText || SBHUD.config.hideTrueDefense) {
                return null;
            }
        }
        return trueDefenceSection;
    }

    private String parseArmorAbilityStack(String section) {
        for (ArmorAbilityStack armorAbilityStack : ArmorAbilityStack.values()) {
            armorAbilityStack.setCurrentValue(0);
        }

        int runs = 0;
        out:
        while (section.contains("  ")) {
            runs++;
            if (runs == 5) break;

            if (section.endsWith("§r")) {
                section = section.substring(0, section.length() - 2);
            }

            for (ArmorAbilityStack amorAbilityStack : ArmorAbilityStack.values()) {
                String stackSymbol = amorAbilityStack.getSymbol();

                if (section.endsWith(stackSymbol)) {

                    String[] split = section.split("§6");
                    String stack = split[split.length - 1];
                    String remove = "§6" + stack;
                    if (stack.contains("§l")) {
                        stack = stack.substring(2);
                        if (SBHUD.config.armorAbilityStack) {
                            String realRemove = remove + "§r";
                            stringsToRemove.add(realRemove);
                        }
                    } else {
                        if (SBHUD.config.armorAbilityStack) stringsToRemove.add(remove);
                    }
                    stack = stack.substring(0, stack.length() - 1);

                    section = section.substring(0, section.length() - remove.length());
                    section = section.trim();
                    amorAbilityStack.setCurrentValue(Integer.parseInt(stack));
                    continue out;
                }
            }
        }

        return section;
    }

    /**
     * Parses the health section and sets the read values as attributes in {@link Utils}.
     * Returns the healing indicator if a healing Wand is active.
     *
     * @param healthSection Health section of the action bar
     * @return null or Wand healing indicator or {@code healthSection} if neither health bar nor health text are enabled
     */
    private String parseHealth(String healthSection) {
        // Normal:      §c1390/1390❤
        // With Wand:   §c1390/1390❤+§c30▅
        final boolean separateDisplay = SBHUD.config.healthBar || SBHUD.config.healthText;
        String returnString = healthSection;
        float newHealth;
        float maxHealth;
        String stripped = TextUtils.stripColor(healthSection);
        Matcher m = HEALTH_PATTERN.matcher(stripped);
        if (separateDisplay && m.matches()) {
            newHealth = TextUtils.parseFloat(m.group("health"));
            maxHealth = TextUtils.parseFloat(m.group("maxHealth"));
            if (m.group("wand") != null) {
                // Jank way of doing this for now
                if (SBHUD.config.healingWandText) {
                    returnString = "";// "§c"+ m.group("wand");
                    healingWandText = m.group("wand");
                    stringsToRemove.add(healthSection);
                } else {
                    returnString = "";// "§c"+ m.group("wand");
                    stringsToRemove.add(stripped.substring(0, m.start("wand")));
                }
            } else {
                stringsToRemove.add(healthSection);
                returnString = "";
            }
            healthLock = false;
            boolean postSetLock = main.getUtils().getAttributes().get(Attribute.MAX_HEALTH).getValue() != maxHealth ||
                    (Math.abs(main.getUtils().getAttributes().get(Attribute.HEALTH).getValue() - newHealth) / maxHealth) > .05;
            setAttribute(Attribute.HEALTH, newHealth);
            setAttribute(Attribute.MAX_HEALTH, maxHealth);
            healthLock = postSetLock;
        }
        return returnString;
    }

    /**
     * Parses the mana section and sets the read values as attributes in {@link Utils}.
     *
     * @param manaSection Mana section of the action bar
     * @return null or {@code manaSection} if neither mana bar nor mana text are enabled
     */
    private String parseMana(String manaSection) {
        // 183/171✎ Mana
        // 421/421✎ 10ʬ
        // 421/421✎ -10ʬ
        Matcher m = MANA_PATTERN.matcher(TextUtils.stripColor(manaSection).trim());
        if (m.matches()) {
            setAttribute(Attribute.MANA, TextUtils.parseFloat(m.group("num")));
            setAttribute(Attribute.MAX_MANA, TextUtils.parseFloat(m.group("den")));
            float overflowMana = 0;
            if (m.group("overflow") != null) {
                overflowMana = TextUtils.parseFloat(m.group("overflow"));
            }
            setAttribute(Attribute.OVERFLOW_MANA, overflowMana);
            main.getRenderListener().setPredictMana(false);
            if (SBHUD.config.manaBar || SBHUD.config.manaText) {
                return null;
            }
        }
        return manaSection;
    }

    /**
     * Parses the defense section and sets the read values as attributes in {@link Utils}.
     *
     * @param defenseSection Defense section of the action bar
     * @return null or {@code defenseSection} if neither defense text nor defense percentage are enabled
     */
    private String parseDefense(String defenseSection) {
        // §a720§a❈ Defense
        // Tethered T1 (Dungeon Healer)--means tethered to 1 person I think: §a1024§a? Defense§6  T1
        // Tethered T3! (Dungeon Healer)--not sure why exclamation mark: §a1039§a? Defense§a§l  T3!
        // Tethered T3! (Dungeon Healer) + Aligned ||| (Gyrokinetic Wand): §a1039§a? Defense§a |||§a§l  T3!
        String stripped = TextUtils.stripColor(defenseSection);
        Matcher m = DEFENSE_PATTERN.matcher(stripped);
        if (m.matches()) {
            float defense = TextUtils.parseFloat(m.group("defense"));
            setAttribute(Attribute.DEFENCE, defense);
            if (SBHUD.config.defenseText || SBHUD.config.defensePercentage) {
                return null;
            }
        }
        return defenseSection;
    }

    /**
     * Parses the ticker section and updates {@link #tickerText} accordingly.
     *
     * @param tickerSection Ticker section of the action bar
     * @return null or {@code tickerSection} if the ticker display is disabled
     */
    private String parseTickers(String tickerSection) {
        // Zombie with full charges: §a§lⓩⓩⓩⓩ§2§l§r
        // Zombie with one used charges: §a§lⓩⓩⓩ§2§lⓄ§r
        // Scorpion tickers: §e§lⓄⓄⓄⓄ§7§l§r
        // Ornate: §e§lⓩⓩⓩ§6§lⓄⓄ§r

        // Zombie uses ⓩ with color code a for usable charges, Ⓞ with color code 2 for unusable
        // Scorpion uses Ⓞ with color code e for usable tickers, Ⓞ with color code 7 for unusable
        // Ornate uses ⓩ with color code e for usable charges, Ⓞ with color code 6 for unusable
        tickerText = tickerSection;
        if (SBHUD.config.tickerChargesText) {
            return null;
        } else {
            return tickerSection;
        }
    }

    private String parseSalvation(String salvationSection) {
        salvationText = salvationSection.trim();
        if (SBHUD.config.salvationText) {
            return null;
        } else {
            return salvationSection;
        }
    }

    private String parseAligned(String alignedSection) {
        isAligned = true;
        if (SBHUD.config.alignmentText) {
            return null;
        } else {
            return alignedSection;
        }
    }

    /**
     * Sets an attribute in {@link Utils}
     * Ignores health if it's locked
     *
     * @param attribute Attribute
     * @param value     Attribute value
     */
    private void setAttribute(Attribute attribute, float value) {
        if (attribute == Attribute.HEALTH && healthLock) return;
        main.getUtils().getAttributes().get(attribute).setValue(value);
    }
}
