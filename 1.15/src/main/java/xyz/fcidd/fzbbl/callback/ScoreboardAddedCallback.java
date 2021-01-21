package xyz.fcidd.fzbbl.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public interface ScoreboardAddedCallback {
    Event<ScoreboardAddedCallback> EVENT = EventFactory.createArrayBacked(ScoreboardAddedCallback.class,
            (listeners) -> (name, criterion, displayName, renderType, objective) -> {
                for (ScoreboardAddedCallback listener : listeners) {
                    ActionResult result = listener.interact(name, criterion, displayName, renderType, objective);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(String name, ScoreboardCriterion criterion, Text displayName,
            ScoreboardCriterion.RenderType renderType, ScoreboardObjective objective);
}
