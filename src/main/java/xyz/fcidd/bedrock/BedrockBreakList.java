package xyz.fcidd.bedrock;

import java.util.HashMap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.fcidd.bedrock.callback.PistonBreakBedrockCallback;
import xyz.fcidd.bedrock.callback.PlayerPlacedCallback;

public class BedrockBreakList implements ModInitializer {
	public static HashMap<World, HashMap<BlockPos, PlayerEntity>> pistonCaches;
	public static HashMap<BlockPos, PlayerEntity> pistonPosCaches;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {

		});
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.getBlockState(hitResult.getBlockPos()).getBlock().equals(Blocks.LEVER)) {
				System.out.println(1);
			}
			return ActionResult.PASS;
		});
		PlayerPlacedCallback.EVENT.register((player, world, stack, hand, hitResult) -> {
			if (stack.getItem().equals(Items.PISTON)) {
				BlockPos pistonPos = hitResult.getBlockPos().offset(hitResult.getSide());
				try {
					BlockPos bedrockPos = pistonPos.offset(world.getBlockState(pistonPos).get(FacingBlock.FACING));
					if (world.getBlockState(bedrockPos).getBlock().equals(Blocks.BEDROCK)) {
						pistonCaches = new HashMap<>();
						pistonPosCaches = new HashMap<>();
						pistonPosCaches.put(bedrockPos, player);
						pistonCaches.put(world, pistonPosCaches);
						System.out.println("PlacedPistonInFrontOfBedrock");
					}
				} catch (IllegalArgumentException e) {
					return ActionResult.PASS;
				}
			}
			return ActionResult.PASS;
		});
		PistonBreakBedrockCallback.EVENT.register((world, pos) -> {
			// PlayerEntity player = pistonCaches.get(world).get(pos);

			System.out.println("BreakBedrockSucceeded");
			return ActionResult.PASS;
		});
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (pistonCaches != null) {
				nullPistonCaches();
			}
		});
	}

	public static void nullPistonCaches() {
		pistonCaches = null;
	}
}