package xyz.fcidd.bedrock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import xyz.fcidd.bedrock.callback.PlayerPlacedCallback;

@Mixin(ServerPlayerInteractionManager.class)
public class PlayerPlacedMixin {
    @Inject(at = @At(value = "RETURN", ordinal = 4), method = "interactBlock")
    private void onPlayerUsed(final ServerPlayerEntity player, final World world, final ItemStack stack,
            final Hand hand, final BlockHitResult hitResult, final CallbackInfoReturnable<ActionResult> cir) {
        //放出来了返回CONSUME，没放出来返回FAIL
        if (cir.getReturnValue().equals(ActionResult.CONSUME)){
            PlayerPlacedCallback.EVENT.invoker().interact(player, world, stack, hand, hitResult);
        }
    }
}
