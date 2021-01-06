package xyz.fcidd.bedrock.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public interface ScoreboardRemovedCallback {
    Event<ScoreboardRemovedCallback> EVENT = EventFactory.createArrayBacked(ScoreboardRemovedCallback.class,
            (listeners) -> (name, criterion, displayName, renderType) -> {
                for (ScoreboardRemovedCallback listener : listeners) {
                    ActionResult result = listener.interact(name, criterion, displayName, renderType);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(String name, ScoreboardCriterion criterion, Text displayName,
            ScoreboardCriterion.RenderType renderType);
}
