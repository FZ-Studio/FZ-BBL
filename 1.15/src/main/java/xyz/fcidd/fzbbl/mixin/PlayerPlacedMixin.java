package xyz.fcidd.fzbbl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import xyz.fcidd.fzbbl.callback.PlayerPlacedCallback;

@Mixin(ServerPlayerInteractionManager.class)
public class PlayerPlacedMixin {
    @Inject(method = "interactBlock", at = @At(value = "RETURN", ordinal = 3))
    private void onPlayerUsed(PlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        // 放出来了返回SUCCESS，没放出来返回FAIL
        if (cir.getReturnValue().equals(ActionResult.SUCCESS)) {
            PlayerPlacedCallback.EVENT.invoker().interact(player, world, stack, hand, hitResult);
        }
    }

    @Inject(method = "interactBlock", at = @At(value = "RETURN", ordinal = 4))
    private void onPlayerUsed2(PlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        // 放出来了返回SUCCESS，没放出来返回FAIL
        if (cir.getReturnValue().equals(ActionResult.SUCCESS)) {
            PlayerPlacedCallback.EVENT.invoker().interact(player, world, stack, hand, hitResult);
        }
    }
}
