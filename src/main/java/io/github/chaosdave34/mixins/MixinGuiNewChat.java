package io.github.chaosdave34.mixins;

import io.github.chaosdave34.SBHUD;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {
    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    public int redirectDrawStringWithShadow(FontRenderer instance, String text, float x, float y, int color) {
        if (SBHUD.INSTANCE.getUtils().isOnSkyblock()) {
            y += SBHUD.INSTANCE.config.moveYPositionOfChat;
        }
        return instance.drawStringWithShadow(text, x, y, color);
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    public void redirectDrawRect(int left, int top, int right, int bottom, int color) {
        if (SBHUD.INSTANCE.getUtils().isOnSkyblock()) {
            top += SBHUD.INSTANCE.config.moveYPositionOfChat;
            bottom += SBHUD.INSTANCE.config.moveYPositionOfChat;
        }
        GuiNewChat.drawRect(left, top, right, bottom, color);
    }

    @ModifyVariable(method = "getChatComponent", at = @At(value = "STORE"), ordinal = 4)
    private int modifyY(int original) {
        return SBHUD.INSTANCE.getUtils().isOnSkyblock() ? original + SBHUD.INSTANCE.config.moveYPositionOfChat: original;
    }
}
