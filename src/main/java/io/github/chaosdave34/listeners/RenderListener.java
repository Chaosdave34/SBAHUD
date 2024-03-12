package io.github.chaosdave34.listeners;


import gg.essential.api.utils.GuiUtil;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.effects.Effect;
import gg.essential.elementa.effects.OutlineEffect;
import gg.essential.elementa.state.State;
import gg.essential.universal.UMatrixStack;
import io.github.chaosdave34.gui.ComponentsGui;
import io.github.chaosdave34.Config;
import io.github.chaosdave34.gui.IMoveableUIContainer;
import io.github.chaosdave34.gui.MoveableUIBlock;
import io.github.chaosdave34.gui.MoveableUIText;
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

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static io.github.chaosdave34.utils.TextUtils.NUMBER_FORMAT;

public class RenderListener {
    private final SBHUD main = SBHUD.INSTANCE;

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
            if (p != null && SBHUD.config.healthPrediction) { //Reverse calculate the player's health by using the player's vanilla hearts. Also calculate the health change for the gui item.
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
        Config config = SBHUD.config;

        String healthText = getHealthText();
        healthText = TextUtils.fillFractionEqually(healthText);
        handleElement(componentsGui.healthText, componentsGui.healtTextState, healthText, config.healthText);

        String effectiveHealthText = NUMBER_FORMAT.format(Math.round(getAttribute(Attribute.HEALTH) * (1 + getAttribute(Attribute.DEFENCE) / 100F)));
        handleElement(componentsGui.effectiveHealthText, componentsGui.effectiveHealtTextState, effectiveHealthText, config.effectiveHealthText);

        String healingWandText = main.getPlayerListener().getHealingWandText();
        handleElement(componentsGui.healingWandTextText, componentsGui.healingWandTextState, healingWandText, config.healingWandText);

        String manaText = NUMBER_FORMAT.format(Math.round(getAttribute(Attribute.MANA))) + "/" + NUMBER_FORMAT.format(getAttribute(Attribute.MAX_MANA));
        manaText = TextUtils.fillFractionEqually(manaText);
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
                float scale = SBHUD.config.dummyScale;
                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1);
                drawBar(container, scale, mc);
                GlStateManager.popMatrix();
            }
        }

        for (UIComponent component : componentsGui.components) {
            if (component instanceof IMoveableUIContainer) {
                if (GuiUtil.getOpenedScreen() instanceof ComponentsGui)
                    ((IMoveableUIContainer) component).enableOutline();
                else
                    ((IMoveableUIContainer) component).disableOutline();
            }
        }
    }


    private void handleElement(UIComponent component, State<String> state, String value, boolean enabled) {
        if ((enabled && value != null) || GuiUtil.getOpenedScreen() instanceof ComponentsGui) {
            if (!main.getComponentsGui().getWindow().getChildren().contains(component)) {
                component.unhide(true);
            }
            if (component instanceof MoveableUIText)
                state.set(value == null ? ((MoveableUIText) component).defaultText : value);
            else
                state.set(value == null ? "100/100" : value);

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
            if (!SBHUD.config.manaBar) {
                return;
            }
        } else if (container == main.getComponentsGui().healthBar) {
            fill = getAttribute(Attribute.HEALTH) / getAttribute(Attribute.MAX_HEALTH);
            if (!SBHUD.config.healthBar) {
                return;
            }
        }
        if (fill > 1) fill = 1;

//        float scaleX = main.getConfigValues().getSizesX(feature);
//        float scaleY = main.getConfigValues().getSizesY(feature);
        float x = container.getLeft() + ((container.getRight() - container.getLeft()) / 2);
        float y = container.getBottom() + ((container.getTop() - container.getBottom()) / 2);
        float scaleX = SBHUD.config.dummyScaleX;
        float scaleY = SBHUD.config.dummyScaleY;

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

        if (container == main.getComponentsGui().healthBar && SBHUD.config.changeBarColorForPotions) {
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
        DrawUtils.drawMultiLayeredBar(mc, color, x, y, fill, hasAbsorption);
        main.getUtils().restoreGLOptions();
    }

    public static float transformXY(float xy, int widthHeight, float scale) {
        float minecraftScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        xy -= widthHeight / 2F * scale;
        xy = Math.round(xy * minecraftScale) / minecraftScale;
        return xy / scale;
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
                            if (SBHUD.config.hideAuroraArmorAbilityStack && abilityName.equals(ArmorAbilityStack.AURORA.getAbilityName())) {
                                return null;
                            }
                            String symbol = armorAbilityStack.getSymbol();
                            int stack = armorAbilityStack.getCurrentValue();
                            if (!SBHUD.config.shortArmorAbilityStack) {
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
                GuiIngameForge.renderFood = !SBHUD.config.hideFoodBar;
                GuiIngameForge.renderArmor = !SBHUD.config.hideArmorBar;
                GuiIngameForge.renderHealth = !SBHUD.config.hideHealthBar;
                GuiIngameForge.renderHealthMount = !SBHUD.config.hidePetHealthBar;
            }
        }
    }
}
