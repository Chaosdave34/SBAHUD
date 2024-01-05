package io.github.chaosdave34.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;

public class DrawUtils {
    /**
     * Draws a multitextured bar:
     * Begins by coloring and rendering the empty bar.
     * Then, colors and renders the full bar up to the fraction {@param fill}.
     * Then, overlays the absorption portion of the bar in gold if the player has absorption hearts
     * Then, overlays (and does not color) an additional texture centered on the current progress of the bar.
     * Then, overlays (and does not color) a final style texture over the bar
     *
     * @param mc            link to the current minecraft session
     * @param color         the color with which to render the bar     * @param x             the x position of the bar
     * @param y             the y position of the bar
     * @param fill          the fraction (from 0 to 1) of the bar that's full
     * @param hasAbsorption {@code true} if the player has absorption hearts
     */
    public static void drawMultiLayeredBar(Minecraft mc, int color, float x, float y, float fill, boolean hasAbsorption) {
        int barHeight = 5, barWidth = 71;
        float barFill = barWidth * fill;
        mc.getTextureManager().bindTexture(new ResourceLocation("sbhud", "barsV2.png"));

        bindColor(color, 0.9F);


        // Empty bar first
        GuiIngameForge.drawModalRectWithCustomSizedTexture((int) x, (int) y, 1, 1, barWidth, barHeight, 80, 50);

        // Filled bar next
        if (fill != 0) {
            GuiIngameForge.drawModalRectWithCustomSizedTexture((int) x, (int) y, 1, 7, (int) barFill, barHeight, 80, 50);
        }

        // Overlay absorption health if needed
        if (hasAbsorption) {
            bindColor(ColorCode.GOLD.getColor());
            GuiIngameForge.drawModalRectWithCustomSizedTexture((int) (x + barFill), (int) y, barFill + 1, 7, (int) (barWidth - barFill), barHeight, 80, 50);
        }
        GlStateManager.color(1F, 1F, 1F, 1F);

        // Overlay uncolored progress indicator next (texture packs can use this to overlay their own static bar colors)
        if (fill > 0 && fill < 1) {
            // Make sure that the overlay doesn't go outside the bounds of the bar.
            // It's 4 pixels wide, so ensure we only render the texture between 0 <= x <= barWidth
            // Start rendering at x => 0 (for small fill values, also don't render before the bar starts)
            // Adding padding ensures that no green bar gets rendered from the texture...?
            float padding = .01F;
            float oneSide = 2 - padding;
            float startX = Math.max(0, barFill - oneSide);
            // Start texture at x >= 0 (for small fill values, also start the texture so indicator is always centered)
            float startTexX = Math.max(padding, oneSide - barFill);
            // End texture at x <= barWidth and 4 <= startTexX + endTexX (total width of overlay texture). Cut off for large fill values.
            float endTexX = Math.min(2 * oneSide - startTexX, barWidth - barFill + oneSide);
            GuiIngameForge.drawModalRectWithCustomSizedTexture((int) (x + startX), (int) y, 1 + startTexX, 24, (int) endTexX, barHeight, 80, 50);
        }
        // Overlay uncolored bar display next (texture packs can use this to overlay their own static bar colors)
        GuiIngameForge.drawModalRectWithCustomSizedTexture((int) x, (int) y, 1, 13, barWidth, barHeight, 80, 50);

    }



    /**
     * Binds a color given its rgb integer representation.
     */
    public  static void bindColor(int color) {
        bindColor(color, 1F);
    }

    /**
     * Binds a color, multiplying all color values by the specified
     * multiplier (for example to make the color darker).
     */
    public static void bindColor(int color, float colorMultiplier) {
        float red = (color >> 16 & 0xFF) / 255F * colorMultiplier;
        float green = (color >> 8 & 0xFF) / 255F * colorMultiplier;
        float blue = (color & 0xFF) / 255F * colorMultiplier;
        float alpha = (color >> 24 & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }
}
