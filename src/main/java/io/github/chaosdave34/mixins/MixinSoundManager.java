package io.github.chaosdave34.mixins;

import io.github.chaosdave34.SBHUD;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class MixinSoundManager {
    @Inject(method = "playSound", at = @At(value = "HEAD"), cancellable = true)
    public void fixEmptySoundError(ISound p_sound, CallbackInfo ci) {
        if (SBHUD.config.fixEmptySounds && p_sound.getSoundLocation().getResourcePath().isEmpty()) ci.cancel();
    }
}
