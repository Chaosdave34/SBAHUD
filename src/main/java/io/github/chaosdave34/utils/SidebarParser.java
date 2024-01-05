package io.github.chaosdave34.utils;

import com.google.common.collect.Sets;
import io.github.chaosdave34.SBHUD;
import io.github.chaosdave34.events.SkyblockJoinedEvent;
import io.github.chaosdave34.events.SkyblockLeftEvent;
import io.github.chaosdave34.listeners.PlayerListener;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.common.MinecraftForge;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SidebarParser {
    private final SBHUD main = SBHUD.INSTANCE;
    private static final Set<String> SKYBLOCK_IN_ALL_LANGUAGES = Sets.newHashSet("SKYBLOCK", "空岛生存", "空島生存");

    private static final Pattern KUUDRA_TOKENS_PATTERN = Pattern.compile("Tokens: (?<token>[0-9,.]+)");

    private static final Pattern DATE_AND_SERVER_ID_PATTERN = Pattern.compile("(?<month>[0-9]+)\\/(?<day>[0-9]+)\\/(?<year>[0-9]+) m(?<serverId>.+)");
    private static final Pattern INGAME_DATE_PATTERN = Pattern.compile("(Early|Late)? (Spring|Summer|Autumn|Winter) [0-9]+(st|nd|rd|th)");
    private static final Pattern INGAME_TIME_PATTERN = Pattern.compile("[0-9:]+(am|pm) .");
    private static final Pattern FLIGHT_DURATION_PATTERN = Pattern.compile("Flight Duration: (?<duration>[0-9:]+)");
    private static final Pattern PURSE_PATTERN = Pattern.compile("(Purse|Piggy): (?<purse>[0-9,.]+)( \\(\\+(?<changed>[0-9,.]+)(\\))?)?");
    private static final Pattern BITS_PATTERN = Pattern.compile("Bits: (?<bits>[0-9,.]+)");
    private static final Pattern ESSENCE_PATTERN = Pattern.compile("(Crimson )?Essence: (?<essence>[0-9.,]+)");
    private static final Pattern NORTH_STARS_PATTERN = Pattern.compile("North Stars: (?<stars>[0-9.,]+)");

    // Special Scenarios
    private boolean kuudra;
    private int kuudra_tokens;

    private boolean dungeons;

    private LocalDate date;
    private String serverId;
    private String ingameDateText;
    private String ingameTimeText;
    private String locationText;
    private Duration flightDuration;
    private String timeElapsedText;
    private String instanceShutdownInText;
    private long coins;
    private long coinsChanged;
    private boolean piggy;
    private int bits;
    private int essence;
    private int northStars;

    private String newYearEventText;

    private final ArrayList<String> missingLines = new ArrayList<>();

    public void parseSidebar() {
        boolean foundScoreboard = false;
        boolean foundSkyblockTitle = false;

        // TODO: This can be optimized more.
        if (main.getUtils().isOnHypixel() && ScoreboardManager.hasScoreboard()) {
            foundScoreboard = true;

            // Check title for skyblock
            String strippedScoreboardTitle = ScoreboardManager.getStrippedScoreboardTitle();
            for (String skyblock : SKYBLOCK_IN_ALL_LANGUAGES) {
                if (strippedScoreboardTitle.startsWith(skyblock)) {
                    foundSkyblockTitle = true;
                    break;
                }
            }

            if (foundSkyblockTitle) {
                if (!main.getUtils().isOnSkyblock()) {
                    MinecraftForge.EVENT_BUS.post(new SkyblockJoinedEvent());
                }
            }
        }
        // If it's not a Skyblock scoreboard, the player must have left Skyblock and
        // be in some other Hypixel lobby or game.
        if (!foundSkyblockTitle && main.getUtils().isOnSkyblock()) {
            // Check if we found a scoreboard in general. If not, its possible they are switching worlds.
            // If we don't find a scoreboard for 10s, then we know they actually left the server.
            if (foundScoreboard || System.currentTimeMillis() - ScoreboardManager.getLastFoundScoreboard() > 10000) {
                MinecraftForge.EVENT_BUS.post(new SkyblockLeftEvent());
            }
        }

        if (main.getUtils().isOnSkyblock() && ScoreboardManager.hasScoreboard()) {
            // Reset certain values
            missingLines.clear();

            kuudra = false;
            kuudra_tokens = -1;

            ingameTimeText = "";
            flightDuration = null;
            timeElapsedText = null;
            instanceShutdownInText = null;
            coins = -1;
            coinsChanged = 0;
            piggy = false;
            bits = -1;
            essence = -1;
            northStars = -1;

            newYearEventText = "";

            for (String line : ScoreboardManager.getScoreboardLines()) {
                boolean handled = parseLine(line);

                if (!handled) {
                    missingLines.add(line);
                }
            }
        }
    }

    private boolean parseLine(String line) {
        String strippedLine = TextUtils.stripColor(line);

        if (kuudra) {
            if (line.contains("Tokens")) {
                parseKuudraTokens(line);
            } else {
                return false;
            }
            return true;
        }

        if (strippedLine.matches(DATE_AND_SERVER_ID_PATTERN.pattern())) {
            parseDateAndServerId(line);
        } else if (strippedLine.matches(INGAME_DATE_PATTERN.pattern())) {
            parseIngameDate(line);
        } else if (strippedLine.matches(INGAME_TIME_PATTERN.pattern())) {
            parseIngameTime(line);
        } else if (line.contains("⏣")) {
            parseLocation(line);
        } else if (line.contains("Flight Duration")) {
            parseFlightDuration(line);
        } else if (line.contains("Time Elapsed")) {
            parseTimeElapsed(line);
        } else if (line.contains("Instance Shutdown In")) {
            parseInstanceShutdownIn(line);
        } else if (line.contains("Purse:")) {
            parsePurse(line, false);
        } else if (line.contains("Piggy:")) {
            parsePurse(line, true);
        } else if (line.contains("Bits")) {
            parseBits(line);
        } else if (line.contains("Essence")) {
            parseEssence(line);
        } else if (line.contains("North Stars")) {
            parseNorthStars(line);
        } else if (line.contains("New Year Event")) {
            handleNewYearEvent(line);
        } else if (strippedLine.equals("www.hypixel.net")) {
            return true; // ignored
        } else return line.trim().isEmpty();
        return true;
    }

    private void parseDateAndServerId(String dateAndServerIdLine) {
        String stripped = TextUtils.stripColor(dateAndServerIdLine);
        Matcher m = DATE_AND_SERVER_ID_PATTERN.matcher(stripped);
        if (m.matches()) {
            int month = TextUtils.parseInt(m.group("month"));
            int day = TextUtils.parseInt(m.group("day"));
            int year = TextUtils.parseInt(m.group("year"));

            date = LocalDate.of(year, month, day);
            serverId = m.group("serverId");
        }
    }

    // Kuudra
    private void parseKuudraTokens(String kuudraTokens) {
        String stripped = TextUtils.stripColor(kuudraTokens);
        Matcher m = KUUDRA_TOKENS_PATTERN.matcher(stripped);
        if (m.matches()) {
            kuudra_tokens = TextUtils.parseInt(m.group("token"));
        }
    }

    // Misc
    private void parseIngameDate(String ingameDateLine) {
        ingameDateText = ingameDateLine;
    }

    private void parseIngameTime(String ingameTimeLine) {
        ingameTimeText = ingameTimeLine;
    }

    private void parseLocation(String locationLine) {
        locationText = locationLine;

        String stripped = TextUtils.stripColor(locationLine);
        if (stripped.contains("Kuudra's Hollow")) {
            kuudra = true;
        }
    }

    private void parseFlightDuration(String flightDurationLine) {
        String stripped = TextUtils.stripColor(flightDurationLine);
        Matcher m = FLIGHT_DURATION_PATTERN.matcher(stripped);
        if (m.matches()) {
            flightDuration = Duration.ZERO;

            String flightDurationText = m.group("duration");
            String[] splits = flightDurationText.split(":");
            if (splits.length == 3) {
                flightDuration = flightDuration
                        .plusHours(TextUtils.parseLong(splits[0]))
                        .plusMinutes(TextUtils.parseLong(splits[1]))
                        .plusSeconds(TextUtils.parseLong(splits[2]));
            } else if (splits.length == 2) {
                flightDuration = flightDuration
                        .plusMinutes(TextUtils.parseLong(splits[0]))
                        .plusSeconds(TextUtils.parseLong(splits[1]));
            } else {
                flightDuration = flightDuration
                        .plusSeconds(TextUtils.parseLong(splits[0]));
            }
        }
    }

    private void parseTimeElapsed(String timeElapsedLine) {
        timeElapsedText = TextUtils.stripColor(timeElapsedLine).split(" ")[1];
    }

    private void parseInstanceShutdownIn(String instanceShutdownInLine) {
        instanceShutdownInText = TextUtils.stripColor(instanceShutdownInLine).split(" ")[3];
    }

    private void parsePurse(String coinsLine, boolean piggy) {
        String stripped = TextUtils.stripColor(coinsLine);
        Matcher m = PURSE_PATTERN.matcher(stripped);
        if (m.matches()) {
            coins = TextUtils.parseLong(m.group("purse"));

            if (m.group("changed") != null) {
                coinsChanged = TextUtils.parseLong(m.group("changed"));
            }

            this.piggy = piggy;
        }
    }

    private void parseBits(String bitsLine) {
        String stripped = TextUtils.stripColor(bitsLine);
        Matcher m = BITS_PATTERN.matcher(stripped);
        if (m.matches()) {
            bits = TextUtils.parseInt(m.group("bits"));
        }
    }

    private void parseEssence(String essenceLine) {
        String stripped = TextUtils.stripColor(essenceLine);
        Matcher m = ESSENCE_PATTERN.matcher(stripped);
        if (m.matches()) {
            essence = TextUtils.parseInt(m.group("essence"));
        }
    }

    private void parseNorthStars(String northStarsLine) {
        String stripped = TextUtils.stripColor(northStarsLine);
        Matcher m = NORTH_STARS_PATTERN.matcher(stripped);
        if (m.matches()) {
            northStars = TextUtils.parseInt(m.group("stars"));
        }
    }

    private void handleNewYearEvent(String newYearEventLine) {
        newYearEventText = newYearEventLine;
    }

    public Collection<Score> buildCustomSidebar(Scoreboard scoreboard, ScoreObjective objective) {
        ScoreObjective customObjective = new ScoreObjective(scoreboard, "CustomSidebar", objective.getCriteria());
        ArrayList<Score> customScores = new ArrayList<>();

        ArrayList<String> customLines = new ArrayList<>();

        PlayerListener playerListener = SBHUD.INSTANCE.getPlayerListener();

        // 1. Date
        String dateString = "N/A";
        if (date != null) {
            if (SBHUD.config.dateFormat == 1) {
                dateString = date.format(DateTimeFormatter.ofPattern("dd.MM.yy"));
            } else {
                dateString = date.format(DateTimeFormatter.ofPattern("MM/dd/yy"));
            }
        }
        customLines.add("§7" + dateString);

        // 2. Empty Line
        customLines.add(" ");

        // 3. Purse
        String purse = "";
        if (coins != -1) {
            purse = (piggy && !SBHUD.config.replacePiggyWithPurse) ? "Piggy: §6" : "Purse: §6";

            purse += TextUtils.abbreviate(coins);
            if (coinsChanged != 0) {
                purse += " (+" + TextUtils.abbreviate(coinsChanged) + ")";
            }
            customLines.add(purse);
        }

        // 4. Bank (hide if purse hidden)
        if (!playerListener.getTabListParser().getBankText().isEmpty() && SBHUD.config.showBank && coins != 0) {
            customLines.add("Bank: §6" + playerListener.getTabListParser().getBankText());
        }

        // 4. Bits
        if (bits != -1 && coins != 0) {
            customLines.add("Bits: §b" + TextUtils.NUMBER_FORMAT.format(bits));
        }

        // 5. Gems (hide if purse hidden)
        if (playerListener.getTabListParser().getGems() != -1 && SBHUD.config.showGems && coins != 0) {
            customLines.add("Gems: §a" + TextUtils.NUMBER_FORMAT.format(playerListener.getTabListParser().getGems()));
        }

        // 6. Essence
        if (essence != -1) {
            customLines.add("Essence: §d" + TextUtils.NUMBER_FORMAT.format(essence));
        }

        // 7. North Stars
        if (northStars != -1) {
            customLines.add("North Stars: §d" + TextUtils.NUMBER_FORMAT.format(northStars));
        }

        // 8. Flight Duration
        if (flightDuration != null) {
            String flightDurationText = "";
            if (flightDuration.toDays() >= 1) {
                flightDurationText = flightDuration.toDays() + "d";
            } else if (flightDuration.toHours() >= 1) {
                flightDurationText = flightDuration.toHours() + "h";
            } else {
                flightDurationText = flightDuration.toString();
            }
            customLines.add("Flight Duration: §a" + flightDurationText);
        }

        // 9. Empty Line
        customLines.add(" ");

        // 10. Location
        customLines.add(locationText);

        // 11. Ingame Date
        customLines.add(ingameDateText);

        // 12. Ingame time
        if (!ingameTimeText.isEmpty()) {
            customLines.add("§7" + ingameTimeText);
        }

        // 13. New Year Event
        if (!newYearEventText.isEmpty()) {
            customLines.add(" ");
            customLines.add(newYearEventText);
        }

        // 14. Time Elapsed
        if (timeElapsedText != null) {
            customLines.add(" ");
            customLines.add("Time Elapsed: §a" + timeElapsedText);
        }

        // 15. Instance Shutdown
        if (instanceShutdownInText != null) {
            customLines.add(" ");
            customLines.add("Instance Shutdown In: §a" + instanceShutdownInText);
        }

        // 16. Kuudra
        if (kuudra) {
            if (kuudra_tokens != -1) {
                customLines.add(" ");
                customLines.add("Tokens: §5" + TextUtils.NUMBER_FORMAT.format(kuudra_tokens));
            }
        }

        // 17. Empty Line
        customLines.add(" ");

        // 18. Server ID
        customLines.add("§8" + serverId);

        // 19. Missing Line
        if (!missingLines.isEmpty()) {
            customLines.add(" ");
            customLines.add("§4Missing lines:");
            customLines.addAll(missingLines);
        }

        int i = 0;
        for (String scoreString : customLines) {
            Score score = new Score(scoreboard, customObjective, scoreString);
            score.setScorePoints(customLines.size() - i);
            customScores.add(score);
            i++;
        }

        Collections.reverse(customScores);

        return customScores;
    }
}
