package io.github.chaosdave34.mixins;

import io.github.chaosdave34.SBHUD;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
@Mixin(NetHandlerPlayClient.class)
public class MixinScoreboard {
    @Redirect(method = "handleTeams", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;removeTeam(Lnet/minecraft/scoreboard/ScorePlayerTeam;)V"))
    public void fixNullPointerBecauseTeamIsNull(Scoreboard instance, ScorePlayerTeam scorePlayerTeam) {
        if (!(SBHUD.config.fixEmptyScorePlayerTeamsAndObjectives && scorePlayerTeam == null))
            instance.removeTeam(scorePlayerTeam);

    }

    @Redirect(method = "handleScoreboardObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;removeObjective(Lnet/minecraft/scoreboard/ScoreObjective;)V"))
    public void fixNullPointerBecauseObjectiveIsNull(Scoreboard instance, ScoreObjective scoreObjective) {
        if (!(SBHUD.config.fixEmptyScorePlayerTeamsAndObjectives && scoreObjective == null))
            instance.removeObjective(scoreObjective);
    }
}
