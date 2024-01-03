package io.github.chaosdave34.listeners;


import gg.essential.api.utils.GuiUtil;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.state.State;
import gg.essential.universal.UMatrixStack;
import io.github.chaosdave34.ComponentsGui;
import io.github.chaosdave34.Config;
import io.github.chaosdave34.MoveableUIText;
import io.github.chaosdave34.SBHUD;
import io.github.chaosdave34.core.ArmorAbilityStack;
import io.github.chaosdave34.core.Attribute;
import io.github.chaosdave34.utils.*;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.GuiNotification;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static io.github.chaosdave34.utils.TextUtils.NUMBER_FORMAT;

public class RenderListener {

    private static final ResourceLocation BARS = new ResourceLocation("skyblockaddons", "barsV2.png");

    private final SBHUD main = SBHUD.getInstance();

    @Getter
    @Setter
    private boolean predictHealth;
    @Getter
    @Setter
    private boolean predictMana;


    @SubscribeEvent()
    public void onRenderRegular(RenderGameOverlayEvent.Post e) {
        //TODO: very hacky way to accomplish update every frame. Fix in feature refactor?
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            EntityPlayerSP p = mc.thePlayer;
            if (p != null && main.getConfig().healthPrediction) { //Reverse calculate the player's health by using the player's vanilla hearts. Also calculate the health change for the gui item.
                float newHealth = getAttribute(Attribute.HEALTH) > getAttribute(Attribute.MAX_HEALTH) ?
                        getAttribute(Attribute.HEALTH) : Math.round(getAttribute(Attribute.MAX_HEALTH) * ((p.getHealth()) / p.getMaxHealth()));
                main.getUtils().getAttributes().get(Attribute.HEALTH).setValue(newHealth);
            }
        }

        if (e.type == RenderGameOverlayEvent.ElementType.EXPERIENCE || e.type == RenderGameOverlayEvent.ElementType.JUMPBAR) {
            if (main.getUtils().isOnSkyblock()) {
                renderOverlays();
            }
        }
    }

    /**
     * This renders all the gui elements (bars, icons, texts, skeleton bar, etc.).
     */
    private void renderOverlays() {
        ComponentsGui componentsGui = main.getComponentsGui();
        Config config = main.getConfig();

        String healthText = getHealthText();
        handleElement(componentsGui.healthText, componentsGui.healtTextState, healthText, config.healthText);

        String effectiveHealthText = NUMBER_FORMAT.format(Math.round(getAttribute(Attribute.HEALTH) * (1 + getAttribute(Attribute.DEFENCE) / 100F)));
        handleElement(componentsGui.effectiveHealthText, componentsGui.effectiveHealtTextState, effectiveHealthText, config.effectiveHealthText);

        String healingWandText = main.getPlayerListener().getHealingWandText();
        handleElement(componentsGui.healingWandTextText, componentsGui.healingWandTextState, healingWandText, config.healingWandText);

        String manaText = NUMBER_FORMAT.format(getAttribute(Attribute.MANA)) + "/" + NUMBER_FORMAT.format(getAttribute(Attribute.MAX_MANA));
        handleElement(componentsGui.manaText, componentsGui.manaTextState, manaText, config.manaText);

        String defenseText = NUMBER_FORMAT.format(getAttribute(Attribute.DEFENCE));
        handleElement(componentsGui.defenseText, componentsGui.defenseTextState, defenseText, config.defenseText);

        String defensePercentage = getDefensePercentage();
        handleElement(componentsGui.defensePercentage, componentsGui.defensePercentageState, defensePercentage, config.defensePercentage);

        String overflowManaText = getAttribute(Attribute.OVERFLOW_MANA) == 0 ? null : getAttribute(Attribute.OVERFLOW_MANA) + "ʬ";
        handleElement(componentsGui.overflowManaText, componentsGui.overflowManaTextState, overflowManaText, config.overflowManaText);

        String speedPercentage = getSpeedPercentage();
        handleElement(componentsGui.speedPercentage, componentsGui.speedPercentageState, speedPercentage, config.speedPercentage);

        String armorAbilityStack = getArmorAbilityStacks();
        handleElement(componentsGui.armorAbilityStack, componentsGui.armorAbilityStackState, armorAbilityStack, config.armorAbilityStack);

        String tickerText = main.getPlayerListener().getTickerText();
        handleElement(componentsGui.tickerText, componentsGui.tickerTextState, tickerText, config.tickerChargesText);

        if ((main.getPlayerListener().isAligned() && config.alignmentText) || GuiUtil.getOpenedScreen() instanceof ComponentsGui) {
            if (!main.getComponentsGui().getWindow().getChildren().contains(main.getComponentsGui().alignmentText)) {
                componentsGui.alignmentText.unhide(true);
            }
        } else {
            if (main.getComponentsGui().getWindow().getChildren().contains(main.getComponentsGui().alignmentText)) {
                componentsGui.alignmentText.hide();
            }
        }

        String salvationText = main.getPlayerListener().getSalvationText();
        handleElement(componentsGui.salvationText, componentsGui.salvationTextState, salvationText, config.salvationText);

        String trueDefenceText = main.getPlayerListener().getTrueDefence() == -1 ? null : NUMBER_FORMAT.format(main.getPlayerListener().getTrueDefence()) + "❂";
        handleElement(componentsGui.trueDefenceText, componentsGui.trueDefenceTextState, trueDefenceText, config.trueDefenceText);

        main.getComponentsGui().getWindow().draw(UMatrixStack.UNIT);

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiNotification)) {
            GlStateManager.disableBlend();

            for (UIContainer container : new UIContainer[]{(UIContainer) main.getComponentsGui().healthBar, (UIContainer) main.getComponentsGui().manaBar}) {
                //float scale = main.getConfigValues().getGuiScale(feature);
                float scale = main.getConfig().dummyScale;
                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1);
                drawBar(container, scale, mc);
                GlStateManager.popMatrix();
            }
        }
    }


    private void handleElement(UIComponent component, State<String> state, String value, boolean enabled) {
        if ((enabled && value != null) || GuiUtil.getOpenedScreen() instanceof ComponentsGui) {
            if (!main.getComponentsGui().getWindow().getChildren().contains(component)) {
                component.unhide(true);
            }
            state.set(value == null ? ((MoveableUIText) component).defaultText : value);
        } else {
            if (main.getComponentsGui().getWindow().getChildren().contains(component)) {
                component.hide();
            }
        }
    }

    /**
     * This draws all Skyblock Addons Bars, including the Health, Mana, Drill, and Skill XP bars
     *
     * @param container for which to render the bars
     * @param scale     the scale of the feature
     * @param mc        link to the minecraft session
     */
    public void drawBar(UIContainer container, float scale, Minecraft mc) {
        // The fill of the bar from 0 to 1
        float fill = 1;
        // Whether the player has absorption hearts
        boolean hasAbsorption = false;
        if (container == main.getComponentsGui().manaBar) {
            fill = getAttribute(Attribute.MANA) / getAttribute(Attribute.MAX_MANA);
            if (!main.getConfig().manaBar) {
                return;
            }
        } else if (container == main.getComponentsGui().healthBar) {
            fill = getAttribute(Attribute.HEALTH) / getAttribute(Attribute.MAX_HEALTH);
            if (!main.getConfig().healthBar) {
                return;
            }
        }
        if (fill > 1) fill = 1;

        // float scaleX = main.getConfigValues().getSizesX(feature);
        // float scaleY = main.getConfigValues().getSizesY(feature);
        float x = container.getLeft() + ((container.getRight() - container.getLeft()) / 2);
        float y = container.getBottom() + ((container.getTop() - container.getBottom()) / 2);
        float scaleX = main.getConfig().dummyScaleX;
        float scaleY = main.getConfig().dummyScaleY;

        GlStateManager.scale(scaleX, scaleY, 1);

        x = transformXY(x, 71, scale * scaleX);
        y = transformXY(y, 5, scale * scaleY);

        // Render the button resize box if necessary

        int color;
        if (container == main.getComponentsGui().healthBar) {
            color = ColorCode.RED.getColor();
        } else if (container == main.getComponentsGui().manaBar) {
            color = ColorCode.BLUE.getColor();
        } else {
            color = ColorCode.WHITE.getColor();
        }

        if (container == main.getComponentsGui().healthBar && main.getConfig().changeBarColorForPotions) {
            if (mc.thePlayer.isPotionActive(19/* Poison */)) {
                color = ColorCode.DARK_GREEN.getColor();
            } else if (mc.thePlayer.isPotionActive(20/* Wither */)) {
                color = ColorCode.DARK_GRAY.getColor();
            } else if (mc.thePlayer.isPotionActive(22) /* Absorption */) {
                if (getAttribute(Attribute.HEALTH) > getAttribute(Attribute.MAX_HEALTH)) {
                    fill = getAttribute(Attribute.MAX_HEALTH) / getAttribute(Attribute.HEALTH);
                    hasAbsorption = true;
                }
            }
        }

        main.getUtils().enableStandardGLOptions();
        // Draw the actual bar
        drawMultiLayeredBar(mc, color, x, y, fill, hasAbsorption);
        main.getUtils().restoreGLOptions();
    }

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
    private void drawMultiLayeredBar(Minecraft mc, int color, float x, float y, float fill, boolean hasAbsorption) {
        int barHeight = 5, barWidth = 71;
        float barFill = barWidth * fill;
        mc.getTextureManager().bindTexture(BARS);

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

    private String getHealthText() {
        String healthText;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(22/* Absorption */) && getAttribute(Attribute.HEALTH) > getAttribute(Attribute.MAX_HEALTH)) {
            healthText = "§6" + NUMBER_FORMAT.format(getAttribute(Attribute.HEALTH)) + "§c/" + NUMBER_FORMAT.format(getAttribute(Attribute.MAX_HEALTH));
        } else {
            healthText = "§c" + NUMBER_FORMAT.format(getAttribute(Attribute.HEALTH)) + "/" + NUMBER_FORMAT.format(getAttribute(Attribute.MAX_HEALTH));
        }
        return healthText;
    }

    private String getArmorAbilityStacks() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        ItemStack[] itemStacks = player.inventory.armorInventory;

        StringBuilder builder = new StringBuilder();
        out:
        for (ArmorAbilityStack armorAbilityStack : ArmorAbilityStack.values()) {
            for (ItemStack itemStack : itemStacks) {
                if (itemStack == null) continue;
                for (String line : ItemUtils.getItemLore(itemStack)) {
                    if (line.contains("§6Tiered Bonus: ")) {
                        String abilityName = armorAbilityStack.getAbilityName();
                        if (line.contains(abilityName)) {
                            String symbol = armorAbilityStack.getSymbol();
                            int stack = armorAbilityStack.getCurrentValue();
                            if (!main.getConfig().shortArmorAbilityStack) {
                                builder.append(abilityName).append(" ");
                            }
                            builder.append(stack).append(" ").append(symbol);
                            continue out;
                        }
                    }
                }
            }
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private String getDefensePercentage() {
        double doubleDefence = getAttribute(Attribute.DEFENCE);
        double doubleDefensePercentage = ((doubleDefence / 100) / ((doubleDefence / 100) + 1)) * 100; //Taken from https://hypixel.net/threads/how-armor-works-and-the-diminishing-return-of-higher-defence.2178928/
        BigDecimal bigDecimalDefensePercentage = new BigDecimal(doubleDefensePercentage).setScale(1, RoundingMode.HALF_UP);
        return bigDecimalDefensePercentage + "%";
    }

    private String getSpeedPercentage() {
        String speedPercentage = NUMBER_FORMAT.format(Minecraft.getMinecraft().thePlayer.capabilities.getWalkSpeed() * 1000);
        speedPercentage = speedPercentage.substring(0, Math.min(speedPercentage.length(), 3));
        if (speedPercentage.endsWith("."))
            speedPercentage = speedPercentage.substring(0, speedPercentage.indexOf('.')); //remove trailing periods
        speedPercentage = speedPercentage + "%";
        return speedPercentage;
    }

    /**
     * Easily grab an attribute from utils.
     */
    private float getAttribute(Attribute attribute) {
        return main.getUtils().getAttributes().get(attribute).getValue();
    }

    @SubscribeEvent()
    public void onRenderRemoveBars(RenderGameOverlayEvent.Pre e) {
        if (e.type == RenderGameOverlayEvent.ElementType.ALL) {
            if (main.getUtils().isOnSkyblock()) {
                GuiIngameForge.renderFood = !main.getConfig().hideFoodBar;
                GuiIngameForge.renderArmor = !main.getConfig().hideArmorBar;
                GuiIngameForge.renderHealth = !main.getConfig().hideHealthBar;
                GuiIngameForge.renderHealthMount = !main.getConfig().hidePetHealthBar;
            }
        }
    }


    public float transformXY(float xy, int widthHeight, float scale) {
        float minecraftScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        xy -= widthHeight / 2F * scale;
        xy = Math.round(xy * minecraftScale) / minecraftScale;
        return xy / scale;
    }

    /**
     * Binds a color given its rgb integer representation.
     */
    public static void bindColor(int color) {
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
