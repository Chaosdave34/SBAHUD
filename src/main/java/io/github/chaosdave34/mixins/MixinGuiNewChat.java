package io.github.chaosdave34.mixins;

import io.github.chaosdave34.SBHUD;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {
    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    private void translate(CallbackInfo ci) {
        float y = SBHUD.config.moveYPositionOfChat;
        GlStateManager.translate(0, y, 0);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "getChatComponent", at = @At(value = "STORE", ordinal = 0), ordinal = 4)
    private int modifyY(int original) {
        SBHUD.logger.info(original);
        return original + SBHUD.config.moveYPositionOfChat;
    }
}
