package io.github.chaosdave34.sbhud.mixins.skytils;

import gg.essential.universal.UResolution;
import io.github.chaosdave34.sbhud.SBHUD;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "gg.skytils.skytilsmod.features.impl.handlers.ChatTabs", remap = false)
public class MixinSkytilsChatTabs {
    @Dynamic
    @Redirect(method = "preDrawScreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getY()I"))
    private int modifyChatYPosition(GuiScreenEvent.DrawScreenEvent.Pre instance) {
        double scale = UResolution.getScaleFactor();
        return (int) (Mouse.getY() + (scale * SBHUD.config.moveYPositionOfChat));
    }
}
