package io.github.chaosdave34.mixins;

import com.google.common.collect.Iterables;
import io.github.chaosdave34.SBHUD;
import io.github.chaosdave34.utils.ScoreboardManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", ordinal = 1))
    private int hideScoreboardNumbers(FontRenderer instance, String text, int x, int y, int color) {
        if (SBHUD.INSTANCE.getUtils().isOnSkyblock() && SBHUD.config.hideScoreboardNumbers) {
            return -1;
        }

        return instance.drawString(text, x, y, color);
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getSortedScores(Lnet/minecraft/scoreboard/ScoreObjective;)Ljava/util/Collection;"))
    private Collection<Score> onRenderScoreboard(Scoreboard instance, ScoreObjective objective) {
        if (SBHUD.INSTANCE.getUtils().isOnSkyblock() && ScoreboardManager.hasScoreboard() && SBHUD.config.enableCustomSidebar) {
            return SBHUD.INSTANCE.getPlayerListener().getScoreboardParser().buildCustomSidebar(instance, objective);
        }
        return instance.getSortedScores(objective);
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Iterables;skip(Ljava/lang/Iterable;I)Ljava/lang/Iterable;"))
    private <T> Iterable<T> modifyCollection(Iterable<T> list, int iterable) {
        if (SBHUD.INSTANCE.getUtils().isOnSkyblock() && SBHUD.config.enableCustomSidebar) {
            return list;
        }
        return Iterables.skip(list, iterable);
    }
}
