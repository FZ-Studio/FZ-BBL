package xyz.fcidd.fzbbl.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface PlayerPlacedCallback {
    Event<PlayerPlacedCallback> EVENT = EventFactory.createArrayBacked(PlayerPlacedCallback.class,
            (listeners) -> (player, world, stack, hand, hitResult) -> {
                for (PlayerPlacedCallback listener : listeners) {
                    ActionResult result = listener.interact(player, world, stack, hand, hitResult);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });
    ActionResult interact(PlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult);
}
