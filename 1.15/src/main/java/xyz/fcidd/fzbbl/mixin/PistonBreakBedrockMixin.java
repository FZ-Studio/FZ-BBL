package xyz.fcidd.fzbbl.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.fcidd.fzbbl.callback.PistonBreakBedrockCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlock.class)
public class PistonBreakBedrockMixin {
    @Redirect(method = "onBlockAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    public boolean onPistonContract(World world, BlockPos pos, boolean move) {
        try {
            if (world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK)) {
                if (world.removeBlock(pos, move) && !world.isClient) {
                    PistonBreakBedrockCallback.EVENT.invoker().interact(world, pos);
                    return true;
                }
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return world.removeBlock(pos, move);
    }
}
