package io.github.chaosdave34.features;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import io.github.chaosdave34.SBHUD;
import io.github.chaosdave34.core.ParsedTabColumn;
import io.github.chaosdave34.core.ParsedTabSection;
import io.github.chaosdave34.utils.TextUtils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.WorldSettings;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class TabListParser {
    private static final Pattern MAGIC_FIND_PATTERN = Pattern.compile("Magic Find: ✯(?<magic>[0-9,.]+)");
    private static final Pattern FEROCITY_PATTERN = Pattern.compile("Ferocity: ⫽(?<magic>[0-9,.]+)");

    private static final SBHUD main = SBHUD.INSTANCE;
    private static final Logger logger = SBHUD.logger;

    private int magicFind;
    private int ferocity;

    public void parse() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.thePlayer.sendQueue == null || !main.getUtils().isOnSkyblock()) {
            return;
        }

        // reset values
        magicFind = -1;
        ferocity = -1;

        NetHandlerPlayClient netHandler = mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> tabListLines = Ordering.from(new PlayerComparator()).sortedCopy(netHandler.getPlayerInfoMap());

        List<ParsedTabColumn> columns = parseColumns(tabListLines);

        parseSections(columns);
    }

    public void parseSections(List<ParsedTabColumn> columns) {
        for (ParsedTabColumn column : columns) {
            ParsedTabSection currentSection = null;
            for (String line : column.getLines()) {

                // Empty lines reset the current section
                if (TextUtils.trimWhitespaceAndResets(line).isEmpty()) {
                    currentSection = null;
                    continue;
                }
                String stripped = TextUtils.stripColor(line).trim();

                if (stripped.matches(MAGIC_FIND_PATTERN.pattern())) {
                    parseMagicFind(stripped);
                } else if (stripped.matches(FEROCITY_PATTERN.pattern())) {
                    parseFerocity(stripped);
                }

                if (currentSection == null) {
                    column.addSection(currentSection = new ParsedTabSection(column));
                }

                currentSection.addLine(line);
            }
        }
    }

    private void parseMagicFind(String line) {
        Matcher m = MAGIC_FIND_PATTERN.matcher(line);
        if (m.matches()) {
            magicFind = TextUtils.parseInt(m.group("magic"));
        }
    }

    private void parseFerocity(String line) {
        Matcher m = FEROCITY_PATTERN.matcher(line);
        if (m.matches()) {
            ferocity = TextUtils.parseInt(m.group("magic"));
        }
    }

    static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        @Override
        public int compare(NetworkPlayerInfo networkPlayerInfo, NetworkPlayerInfo networkPlayerInfo2) {
            ScorePlayerTeam scorePlayerTeam = networkPlayerInfo.getPlayerTeam();
            ScorePlayerTeam scorePlayerTeam2 = networkPlayerInfo2.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(networkPlayerInfo.getGameType() != WorldSettings.GameType.SPECTATOR, networkPlayerInfo2.getGameType() != WorldSettings.GameType.SPECTATOR).compare(scorePlayerTeam != null ? scorePlayerTeam.getRegisteredName() : "", scorePlayerTeam2 != null ? scorePlayerTeam2.getRegisteredName() : "").compare(networkPlayerInfo.getGameProfile().getName(), networkPlayerInfo2.getGameProfile().getName()).result();
        }

//        public /* synthetic */ int compare(Object object, Object object2) {
//            return this.compare((NetworkPlayerInfo)object, (NetworkPlayerInfo)object2);
//        }
    }

    private static List<ParsedTabColumn> parseColumns(List<NetworkPlayerInfo> fullList) {
        GuiPlayerTabOverlay tabList = Minecraft.getMinecraft().ingameGUI.getTabList();

        List<ParsedTabColumn> columns = new LinkedList<>();
        for (int entry = 0; entry < fullList.size(); entry += 20) {
            String title = TextUtils.trimWhitespaceAndResets(tabList.getPlayerName(fullList.get(entry)));
            ParsedTabColumn column = getColumnFromName(columns, title);
            if (column == null) {
                column = new ParsedTabColumn(title);
                columns.add(column);
            }

            for (int columnEntry = entry + 1; columnEntry < fullList.size() && columnEntry < entry + 20; columnEntry++) {
                column.addLine(tabList.getPlayerName(fullList.get(columnEntry)));
            }
        }

        return columns;
    }

    public static ParsedTabColumn getColumnFromName(List<ParsedTabColumn> columns, String name) {
        for (ParsedTabColumn parsedTabColumn : columns) {
            if (name.equals(parsedTabColumn.getTitle())) {
                return parsedTabColumn;
            }
        }

        return null;
    }
}
