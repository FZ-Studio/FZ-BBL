package xyz.fcidd.bedrock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import xyz.fcidd.bedrock.callback.ScoreboardAddedCallback;

@Mixin(Scoreboard.class)
public class ScoreboardAddedMixin {
    @Inject(at = @At(value = "RETURN"), method = "addObjective")
    public void onScoreboardAdded(final String name, final ScoreboardCriterion criterion, final Text displayName,
            final ScoreboardCriterion.RenderType renderType, final CallbackInfoReturnable<ScoreboardObjective> cir) {
        ScoreboardAddedCallback.EVENT.invoker().interact(name, criterion, displayName, renderType);
    }
}
