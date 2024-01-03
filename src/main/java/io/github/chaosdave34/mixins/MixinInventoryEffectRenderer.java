package io.github.chaosdave34.mixins;

import io.github.chaosdave34.SBHUD;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer {
    @ModifyVariable(method = "updateActivePotionEffects", at = @At(value = "STORE"))
    public boolean hasVisibleEffect_updateActivePotionEffects(boolean hasVisibleEffect) {
        return (!SBHUD.INSTANCE.getUtils().isOnSkyblock() || !SBHUD.INSTANCE.config.hidePotionEffects) && hasVisibleEffect;
    }
}
