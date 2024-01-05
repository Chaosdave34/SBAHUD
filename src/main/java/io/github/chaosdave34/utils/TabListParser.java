package io.github.chaosdave34.utils;

import io.github.chaosdave34.SBHUD;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class TabListParser {
    private static final Pattern BITS_PATTERN = Pattern.compile(" Gems: (?<gems>[0-9,.]+)");

    private int gems;
    private String bankText;

    public void parseTabList() {
        gems = -1;
        bankText = "";

        for (NetworkPlayerInfo player : Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap()) {
            if (player != null) {

                String line;
                if (player.getDisplayName() != null) {
                    line = player.getDisplayName().getFormattedText();
                } else {
                    line = ScorePlayerTeam.formatPlayerName(player.getPlayerTeam(), player.getGameProfile().getName());
                }

                parseLine(line);
            }
        }
    }

    private void parseLine(String line) {
        if (line.contains("Gems")) {
            parseGems(line);
        } else if (line.contains("Bank")) {
            parseBank(line);
        }
    }

    private void parseGems(String gemsLine) {
        String stripped = TextUtils.stripColor(gemsLine);
        Matcher m = BITS_PATTERN.matcher(stripped);
        if (m.matches()) {
            gems = TextUtils.parseInt(m.group("gems"));
        }
    }

    private void parseBank(String bankLine) {
        bankText = bankLine.split(" ")[2];
    }
}
