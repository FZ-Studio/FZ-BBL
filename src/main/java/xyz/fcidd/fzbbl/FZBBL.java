package xyz.fcidd.fzbbl;

import java.util.ArrayList;
import java.util.HashMap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.fcidd.fzbbl.callback.PistonBreakBedrockCallback;
import xyz.fcidd.fzbbl.callback.PlayerPlacedCallback;
import xyz.fcidd.fzbbl.callback.ScoreboardAddedCallback;
import xyz.fcidd.fzbbl.callback.ScoreboardRemovedCallback;

public class FZBBL implements ModInitializer {
	//活塞缓存
	public static HashMap<World, HashMap<BlockPos, PlayerEntity>> pistonCaches;
	//上面的值
	private static HashMap<BlockPos, PlayerEntity> pistonPosCaches;
	//破基岩记分板
	private static ArrayList<ScoreboardObjective> scoreboardBBL = new ArrayList<>();

	@Override
	public void onInitialize() {
		ScoreboardAddedCallback.EVENT.register((name, criterion, displayName, renderType, objective) -> {
			try {
				//判断计分板名字后四位是不是“.bbl”，是的话把它加到列表里
				//在服务器初始化时会为每个记分板调用这个方法(Scoreboard.addObjective())，因此不用单独在初始化完成后获取记分板
				if (name.substring(name.length() - 4).equals(".bbl") && criterion.equals(ScoreboardCriterion.DUMMY)) {
					System.out.println("AddedBedrockBreakedList: " + name);
					scoreboardBBL.add(objective);
				}
			} catch (StringIndexOutOfBoundsException e) {

			}
			return ActionResult.PASS;
		});
		ScoreboardRemovedCallback.EVENT.register((objective) -> {
			String name = objective.getName();
			try {
				if (name.substring(name.length() - 4).equals(".bbl") && objective.getCriterion().equals(ScoreboardCriterion.DUMMY)) {
					//判断计分板名字后四位是不是“.bbl”，是的话把它从列表里删除
					System.out.println("RemovedBedrockBreakedList: " + name);
					scoreboardBBL.remove(objective);
				}
			} catch (StringIndexOutOfBoundsException e) {

			}
			return ActionResult.PASS;
		});
		PlayerPlacedCallback.EVENT.register((player, world, stack, hand, hitResult) -> {
			//判断玩家方的是不是活塞
			//stack是调用该方法(ServerPlayerInteractionManager.interactBlock())时生效的物品
			if (stack.getItem().equals(Items.PISTON)) {
				BlockPos pistonPos = hitResult.getBlockPos().offset(hitResult.getSide());
				try {
					BlockPos bedrockPos = pistonPos.offset(world.getBlockState(pistonPos).get(FacingBlock.FACING));
					if (world.getBlockState(bedrockPos).getBlock().equals(Blocks.BEDROCK)) {
						//初始化活塞缓存
						newPistonCaches();
						newPistonPosCaches();
						pistonPosCaches.put(bedrockPos, player);
						pistonCaches.put(world, pistonPosCaches);
						System.out.println("PlacedPistonPointToBedrockAt: " + pistonPos.toString());
					}
				} catch (IllegalArgumentException e) {

				}
			}
			return ActionResult.SUCCESS;
		});
		PistonBreakBedrockCallback.EVENT.register((world, pos) -> {
			String player = pistonCaches.get(world).get(pos).getEntityName();
			//遍历破基岩记分板列表
			scoreboardBBL.forEach(objective -> {
				try {
					//给记分板加分
					world.getScoreboard().getPlayerScore(player, objective).incrementScore();
				} catch (NullPointerException e) {

				}
			});
			System.out.println("BreakBedrockSucceeded: " + player);
			return ActionResult.PASS;
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			//在每tick末尾活塞缓存非null时赋值null
			if (pistonCaches != null) {
				nullPistonCaches();
			}
		});
	}

	private static void nullPistonCaches() {
		//在不需要它的时候赋值为null，方便其他方法中判断它是否等于null以提高效率
		pistonCaches = null;
	}

	private static void newPistonCaches() {
		//初始化活塞缓存
		pistonCaches = new HashMap<>();
	}

	private static void newPistonPosCaches() {
		//初始化活塞缓存中的值
		pistonPosCaches = new HashMap<>();
	}
}