package xyz.fcidd.fzbbl.mixin;

import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.fcidd.fzbbl.FZBBL;
import xyz.fcidd.fzbbl.callback.PistonBreakBedrockCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlock.class)
public class PistonBreakBedrockMixin {

    @Redirect(require = 2, method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    public boolean onPistonContract(World world, BlockPos pos, boolean move) {
        //没人在破基岩的话HashMap是null
        if (FZBBL.pistonCaches != null && FZBBL.pistonCaches.get(world).containsKey(pos)) {
            if (world.removeBlock(pos, move)) {
                PistonBreakBedrockCallback.EVENT.invoker().interact(world, pos);
                return true;
            }
            return false;
        }
        return world.removeBlock(pos, move);
    }
}
