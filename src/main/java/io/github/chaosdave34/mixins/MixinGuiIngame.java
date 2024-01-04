package io.github.chaosdave34.mixins;

import io.github.chaosdave34.SBHUD;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Redirect(method = "renderScoreboard", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", ordinal = 1))
    public int hideScoreboardNumbers(FontRenderer instance, String text, int x, int y, int color){
        if (SBHUD.INSTANCE.getUtils().isOnSkyblock() && SBHUD.config.hideScoreboardNumbers) {
            return -1;
        }

        return instance.drawString(text, x, y, color);
    }
}
