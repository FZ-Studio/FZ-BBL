package xyz.fcidd.fzbbl.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.ActionResult;

public interface ScoreboardRemovedCallback {
    Event<ScoreboardRemovedCallback> EVENT = EventFactory.createArrayBacked(ScoreboardRemovedCallback.class,
            (listeners) -> (objective) -> {
                for (ScoreboardRemovedCallback listener : listeners) {
                    ActionResult result = listener.interact(objective);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(ScoreboardObjective objective);
}
