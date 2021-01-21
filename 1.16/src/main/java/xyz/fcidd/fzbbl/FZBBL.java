package xyz.fcidd.fzbbl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.fcidd.fzbbl.callback.PistonBreakBedrockCallback;
import xyz.fcidd.fzbbl.callback.PlayerPlacedCallback;
import xyz.fcidd.fzbbl.callback.ScoreboardAddedCallback;
import xyz.fcidd.fzbbl.callback.ScoreboardRemovedCallback;

public class FZBBL implements ModInitializer {
	// 活塞缓存
	public static HashMap<World, HashMap<BlockPos, PlayerEntity>> pistonCaches = null;
	public static HashMap<PlayerEntity, BlockPos> playerPlacedCaches = null;
	// 破基岩记分板
	private static ArrayList<ScoreboardObjective> scoreboardBBL = new ArrayList<>();
	private static ArrayList<ScoreboardObjective> scoreboardMBB = new ArrayList<>();
	private static ArrayList<ScoreboardObjective> scoreboardLBL = new ArrayList<>();

	@Override
	public void onInitialize() {
		ScoreboardAddedCallback.EVENT.register((name, criterion, displayName, renderType, objective) -> {
			try {
				String sufix = name.substring(name.length() - 4);
				// 判断计分板名字后四位是不是“.bbl”或“.mbb”，是的话把它加到列表里
				// 在服务器初始化时会为每个记分板调用这个方法(Scoreboard.addObjective())，因此不用单独在初始化完成后获取记分板
				if (sufix.equals(".bbl") && criterion.equals(ScoreboardCriterion.DUMMY)) {
					System.out.println("AddedBedrockBreakedList: " + name);
					scoreboardBBL.add(objective);

				} else if (sufix.equals(".mbb") && criterion.equals(ScoreboardCriterion.DUMMY)) {
					System.out.println("AddedMachineBedrockBreaked: " + name);
					scoreboardMBB.add(objective);
				} else if (sufix.equals(".lbl") && criterion.equals(ScoreboardCriterion.DUMMY)) {
					System.out.println("AddedLooserBreakedList: " + name);
					scoreboardLBL.add(objective);
				}
			} catch (StringIndexOutOfBoundsException e) {

			}
			return ActionResult.PASS;
		});
		ScoreboardRemovedCallback.EVENT.register(objective -> {
			String name = objective.getName();
			try {
				String sufix = name.substring(name.length() - 4);
				// 判断计分板名字后四位是不是“.bbl”或“.mbb”，是的话把它从列表里删除
				if (sufix.equals(".bbl") && objective.getCriterion().equals(ScoreboardCriterion.DUMMY)) {
					System.out.println("RemovedBedrockBreakedList: " + name);
					scoreboardBBL.remove(objective);
				} else if (sufix.equals(".mbb") && objective.getCriterion().equals(ScoreboardCriterion.DUMMY)) {
					System.out.println("RemovedMachineBedrockBreaked: " + name);
					scoreboardMBB.remove(objective);
				} else if (sufix.equals(".lbl") && objective.getCriterion().equals(ScoreboardCriterion.DUMMY)) {
					System.out.println("RemovedLooserBreakedList: " + name);
					scoreboardLBL.remove(objective);
				}
			} catch (StringIndexOutOfBoundsException e) {

			}
			return ActionResult.PASS;
		});
		PlayerPlacedCallback.EVENT.register((player, world, stack, hand, hitResult) -> {
			// 判断玩家方的是不是活塞
			// stack是调用该方法(ServerPlayerInteractionManager.interactBlock())时生效的物品
			if (stack.getItem().equals(Items.PISTON)) {
				BlockPos pistonPos;
				BlockState pistonState;
				BlockPos bedrockPos;
				try {
					pistonPos = hitResult.getBlockPos().offset(hitResult.getSide());
					pistonState = world.getBlockState(pistonPos);
					bedrockPos = pistonPos.offset(pistonState.get(FacingBlock.FACING));
					if (world.getBlockState(bedrockPos).getBlock().equals(Blocks.BEDROCK)) {
						// 活塞缓存
						putPistonCaches(world, bedrockPos, player);
						System.out.println("PlacedPistonPointToBedrockAt: " + pistonPos.toString());
					}
				} catch (IllegalArgumentException e) {
					pistonPos = hitResult.getBlockPos();
					pistonState = world.getBlockState(pistonPos);
					bedrockPos = pistonPos.offset(pistonState.get(FacingBlock.FACING));
					if (world.getBlockState(bedrockPos).getBlock().equals(Blocks.BEDROCK)) {
						// 活塞缓存
						putPistonCaches(world, bedrockPos, player);
						System.out.println("PlacedPistonPointToBedrockAt: " + pistonPos.toString());
					}
				}
			}
			return ActionResult.SUCCESS;
		});
		PistonBreakBedrockCallback.EVENT.register((world, pos) -> {
			// 没人在破基岩的话pistonCaches是null
			if (FZBBL.pistonCaches != null && FZBBL.pistonCaches.containsKey(world)
					&& FZBBL.pistonCaches.get(world).containsKey(pos)) {
				String player = pistonCaches.get(world).get(pos).getEntityName();
				// 遍历玩家破基岩记分板列表
				scoreboardBBL.forEach(objective ->
				// 给记分板加分
				world.getScoreboard().getPlayerScore(player, objective).incrementScore());
				System.out.println("BreakBedrockSucceeded: " + player);
			} else {
				// 遍历机器破基岩记分板列表
				String worldID = world.getRegistryKey().getValue().getPath();
				scoreboardMBB.forEach(objective -> {
					// 给记分板加分，加分的玩家id等于基岩所在世界($overworld、$the_end、$the_nether)
					world.getScoreboard().getPlayerScore("$" + worldID, objective).incrementScore();
					world.getScoreboard().getPlayerScore("$total", objective).incrementScore();
				});
				System.out.println("BreakBedrockSucceeded: $" + worldID);
			}
			// 遍历条件宽松的玩家破基岩记分板列表
			PlayerEntity playerEntity = world.getClosestPlayer((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D,
					(double) pos.getY() + 0.5D, 100.0D, false);
			if (playerEntity != null) {
				String player = playerEntity.getEntityName();
				scoreboardLBL.forEach(objective ->
				// 给最近的玩家加分
				world.getScoreboard().getPlayerScore(player, objective).incrementScore());
				System.out.println("LooselyBreakBedrock: " + player);
			}
			return ActionResult.PASS;
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			// 在每tick末尾活塞缓存非null时赋值null
			if (pistonCaches != null) {
				nullPistonCaches();
			}
		});
	}

	private static void nullPistonCaches() {
		// 在不需要它的时候赋值为null，方便其他方法中判断它是否等于null以提高效率
		pistonCaches = null;
	}

	private static HashMap<World, HashMap<BlockPos, PlayerEntity>> putPistonCaches(World world, BlockPos pos,
			PlayerEntity player) {
		// 初始化活塞缓存中的值
		if (pistonCaches == null) {
			pistonCaches = new HashMap<>();
		}
		if (pistonCaches.containsKey(world)) {
			pistonCaches.get(world).put(pos, player);
		} else {
			HashMap<BlockPos, PlayerEntity> pistonPosCaches = new HashMap<>();
			pistonPosCaches.put(pos, player);
			pistonCaches.put(world, pistonPosCaches);
		}
		return pistonCaches;
	}
}