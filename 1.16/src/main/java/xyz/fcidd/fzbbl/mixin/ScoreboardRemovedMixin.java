package xyz.fcidd.fzbbl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import xyz.fcidd.fzbbl.callback.ScoreboardRemovedCallback;

@Mixin(Scoreboard.class)
public class ScoreboardRemovedMixin {
    @Inject(at = @At(value = "HEAD"), method = "removeObjective")
    public void onScoreboardRemoved(final ScoreboardObjective objective, CallbackInfo cir) {
        ScoreboardRemovedCallback.EVENT.invoker().interact(objective);
    }
}
