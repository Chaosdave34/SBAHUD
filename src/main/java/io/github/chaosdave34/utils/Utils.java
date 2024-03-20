package io.github.chaosdave34.utils;

import com.google.common.collect.Sets;
import io.github.chaosdave34.SBHUD;
import io.github.chaosdave34.core.Attribute;
import io.github.chaosdave34.events.SkyblockJoinedEvent;
import io.github.chaosdave34.events.SkyblockLeftEvent;
import io.github.chaosdave34.features.ScoreboardManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Getter
@Setter
public class Utils {

    private static final SBHUD main = SBHUD.INSTANCE;
    private static final Logger logger = SBHUD.logger;

    private static final Set<String> SKYBLOCK_IN_ALL_LANGUAGES = Sets.newHashSet("SKYBLOCK", "\u7A7A\u5C9B\u751F\u5B58", "\u7A7A\u5CF6\u751F\u5B58");

    /**
     * Get a player's attributes. This includes health, mana, and defence.
     */
    private Map<Attribute, MutableFloat> attributes = new EnumMap<>(Attribute.class);


    /**
     * Whether the player is on skyblock.
     */
    private boolean onSkyblock;

    public Utils() {
        addDefaultStats();
    }

    private void addDefaultStats() {
        for (Attribute attribute : Attribute.values()) {
            attributes.put(attribute, new MutableFloat(attribute.getDefaultValue()));
        }
    }

    /**
     * Checks if the player is on the Hypixel Network
     *
     * @return {@code true} if the player is on Hypixel, {@code false} otherwise
     */
    public boolean isOnHypixel() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            return false;
        }
        String brand = player.getClientBrand();
        if (brand != null) {
            return Pattern.compile(".*Hypixel BungeeCord.*").matcher(brand).matches();
        }
        return false;
    }

    public void parseSidebar() {
        boolean foundScoreboard = false;
        boolean foundSkyblockTitle = false;

        // TODO: This can be optimized more.
        if (isOnHypixel() && ScoreboardManager.hasScoreboard()) {
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
                if (!this.isOnSkyblock()) {
                    MinecraftForge.EVENT_BUS.post(new SkyblockJoinedEvent());
                }
            }

        }
        // If it's not a Skyblock scoreboard, the player must have left Skyblock and
        // be in some other Hypixel lobby or game.
        if (!foundSkyblockTitle && this.isOnSkyblock()) {


            // Check if we found a scoreboard in general. If not, its possible they are switching worlds.
            // If we don't find a scoreboard for 10s, then we know they actually left the server.
            if (foundScoreboard || System.currentTimeMillis() - ScoreboardManager.getLastFoundScoreboard() > 10000) {
                MinecraftForge.EVENT_BUS.post(new SkyblockLeftEvent());
            }
        }
    }

    private boolean depthEnabled;
    private boolean blendEnabled;
    private boolean alphaEnabled;
    private int blendFunctionSrcFactor;
    private int blendFunctionDstFactor;

    public void enableStandardGLOptions() {
        depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        alphaEnabled = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        blendFunctionSrcFactor = GL11.glGetInteger(GL11.GL_BLEND_SRC);
        blendFunctionDstFactor = GL11.glGetInteger(GL11.GL_BLEND_DST);

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1, 1);
    }

    public void restoreGLOptions() {
        if (depthEnabled) {
            GlStateManager.enableDepth();
        }
        if (!alphaEnabled) {
            GlStateManager.disableAlpha();
        }
        if (!blendEnabled) {
            GlStateManager.disableBlend();
        }
        GlStateManager.blendFunc(blendFunctionSrcFactor, blendFunctionDstFactor);
    }

}
