package io.github.chaosdave34.sbhud.features;

import io.github.chaosdave34.sbhud.Config;
import io.github.chaosdave34.sbhud.SBHUD;
import io.github.chaosdave34.sbhud.utils.TextUtils;

import java.util.regex.Pattern;

public class SpamHider {
    private final static Pattern DUNGEON_BUFF_PATTERN_1 = Pattern.compile("DUNGEON BUFF!(.+)?");
    private final static Pattern DUNGEON_BUFF_PATTERN_2 = Pattern.compile("Granted you(.+)?");
    private final static Pattern DUNGEON_BUFF_PATTERN_3 = Pattern.compile("Also granted you(.+)?");

    private final static Pattern DUNGEON_BLESSING_PATTERN_1 = Pattern.compile("(.+)?has obtained Blessing(.+)?");
    private final static Pattern DUNGEON_BLESSING_PATTERN_2 = Pattern.compile("A Blessing(.+)?was picked up!");

    private final static Pattern DUNGEON_ESSENCE_PATTERN_1 = Pattern.compile("ESSENCE!(.+)?");
    private final static Pattern DUNGEON_ESSENCE_PATTERN_2 = Pattern.compile("(.+)?found a(.+)?Essence! Everyone gains an extra essence!");

    private final static Pattern DUNGEON_BOSS_DIALOGUE_PATTERN = Pattern.compile("\\[BOSS](.+)?");

    private final static Pattern DUNGEON_MILESTONE_PATTERN = Pattern.compile("(.+)?Milestone(.+)?");

    private final static Pattern DUNGEON_MORT_DIALOGUE_PATTERN = Pattern.compile("\\[NPC] Mort:(.+)?");

    private final static Pattern DUNGEON_ORB_PICKUP_1 = Pattern.compile("◕(.+)?picked up your(.+)?Orb!");
    private final static Pattern DUNGEON_ORB_PICKUP_2 = Pattern.compile("◕ You picked up a(.+)?Orb from(.+)?\\.");

    private static final Config config = SBHUD.config;

    public boolean filter(String message) {
        message = TextUtils.trimWhitespaceAndResets(message);

        if (config.hideDungeonBuffMessages && (message.matches(DUNGEON_BUFF_PATTERN_1.pattern()) || message.matches(DUNGEON_BUFF_PATTERN_2.pattern()) || message.matches(DUNGEON_BUFF_PATTERN_3.pattern()))) {
            return true;
        } else if (config.hideDungeonBlessingsMessages && (message.matches(DUNGEON_BLESSING_PATTERN_1.pattern()) || message.matches(DUNGEON_BLESSING_PATTERN_2.pattern()))) {
            return true;
        } else if (config.hideDungeonEssenceMessages && (message.matches(DUNGEON_ESSENCE_PATTERN_1.pattern()) || message.matches(DUNGEON_ESSENCE_PATTERN_2.pattern()))) {
            return true;
        } else if (config.hideDungeonBossDialogueMessages && message.matches(DUNGEON_BOSS_DIALOGUE_PATTERN.pattern())) {
            return true;
        } else if (config.hideDungeonMilestoneMessages && message.matches(DUNGEON_MILESTONE_PATTERN.pattern())) {
            return true;
        } else if (config.hideDungeonMortDialogueMessages && message.matches(DUNGEON_MORT_DIALOGUE_PATTERN.pattern())) {
            return true;
        } else if (config.hideDungeonOrbPickupMessages && (message.matches(DUNGEON_ORB_PICKUP_1.pattern()) || message.matches(DUNGEON_ORB_PICKUP_2.pattern()))) {
            return true;
        }
        return false;
    }
}
