package xyz.fcidd.bedrock.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PistonBreakBedrockCallback {
    Event<PistonBreakBedrockCallback> EVENT = EventFactory.createArrayBacked(PistonBreakBedrockCallback.class,
            (listeners) -> (world, pos) -> {
                for (PistonBreakBedrockCallback listener : listeners) {
                    ActionResult result = listener.interact(world, pos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(World world, BlockPos pos);
}
