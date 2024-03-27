package io.github.chaosdave34.sbhud.mixins;

import io.github.chaosdave34.sbhud.SBHUD;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {
    @ModifyVariable(method = "renderHealth", at = @At(value = "STORE"), ordinal = 1, remap = false)
    private float removeAbsorption(float absorption) {
        return (SBHUD.config.hideAbsorptionHearts && SBHUD.INSTANCE.getUtils().isOnSkyblock()) ? 0f : absorption;
    }

    @ModifyVariable(method = "renderToolHightlight", at = @At(value = "STORE"), ordinal = 1, remap = false)
    private int modifyY(int y) {
        return y + (SBHUD.INSTANCE.getUtils().isOnSkyblock() ? SBHUD.config.moveYPositionOfToolHighlight : 0);
    }

    @Redirect(method = "renderRecordOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    private int redirectDrawString(FontRenderer instance, String text, int x, int y, int color) {
        if (SBHUD.INSTANCE.getUtils().isOnSkyblock()) {
            y += SBHUD.config.moveYPositionOfActionbar;
        }
        return instance.drawString(text, x, y, color);
    }

    @Redirect(method = "renderHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isPotionActive(Lnet/minecraft/potion/Potion;)Z"))
    private boolean redirectPotionRegeneration(EntityPlayer instance, Potion potion) {
        if (potion == Potion.regeneration && SBHUD.config.disableJumpingHearts && SBHUD.INSTANCE.getUtils().isOnSkyblock()) {
            return false;
        } else return instance.isPotionActive(potion);
    }

    @Redirect(method = "renderHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;getAttributeValue()D"))
    private double redirectGetAttributeValue(IAttributeInstance instance) {
        return SBHUD.INSTANCE.getUtils().isOnSkyblock() ? SBHUD.config.maxNumberOfHearts * 2 : instance.getAttributeValue();
    }

    @Redirect(method = "renderHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;getHealth()F"))
    private float redirectGetHealth(EntityPlayer instance) {
        double maxHealth = instance.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).getAttributeValue();

        if (SBHUD.INSTANCE.getUtils().isOnSkyblock()) {
            return (float) (instance.getHealth() * (SBHUD.config.maxNumberOfHearts * 2 / maxHealth));
        }
        return instance.getHealth();
    }
}
