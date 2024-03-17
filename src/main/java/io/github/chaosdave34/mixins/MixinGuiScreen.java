package io.github.chaosdave34.mixins;

import io.github.chaosdave34.SBHUD;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {

    /**
     * @author Chaosdave34
     * @reason fix not working on linux
     */
    @Inject(method = "openWebLink", at = @At(value = "HEAD"), cancellable = true)
    private void openWebLink(URI url, CallbackInfo ci) {
        if (SBHUD.config.fixOpeningLinksOnLinux) {
            ci.cancel();

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) { // Windows
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(url);
                } catch (IOException e) {
                    SBHUD.logger.error("Couldn't open link", e);
                }

            } else {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url);
                } catch (IOException e) {
                    SBHUD.logger.error("Couldn't open link", e);
                }
            }
        }
    }
}
