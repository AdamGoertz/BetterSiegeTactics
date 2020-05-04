package den0minat0r.bettersiegetactics.common.block;


import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Spliterator;

import org.apache.logging.log4j.Logger;

import den0minat0r.bettersiegetactics.common.util.SupportedBlockCache;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SupportedBlock extends FallingBlock {
	
	private static final Logger LOGGER = LogManager.getLogger();
    private static final Direction[] SUPPORT_SEARCH_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
	private static final SupportedBlockCache cache = new SupportedBlockCache(1024);
	private final int support_distance;

	public SupportedBlock(Properties properties, int support_distance) {
		super(properties);
		
		this.support_distance = support_distance;
		
	}
	
	@Override
	public void func_225534_a_(BlockState state, ServerWorld sWorld, BlockPos pos, Random rand) {
		if (!isSupported(sWorld.getWorld(), pos)) 
		{
			cache.remove(pos);
			super.func_225534_a_(state, sWorld, pos, rand);
		}
	}
	
	@Override 
	public void onStartFalling(FallingBlockEntity entity) {
		entity.setHurtEntities(true);
	}
	
	public static boolean isDirectlySupported(World worldIn, BlockPos pos) {
		return pos.getY() == 0 || !FallingBlock.canFallThrough(worldIn.getBlockState(pos.down()));
	}
	
	public int getSupportDistance() {
		return this.support_distance;
	}
	
	/**
	 * Perform a depth-first search for any blocks within Manhattan-distance {@value this.support_distance} that are no longer supported.
	 */
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		updateAfterDestroy(worldIn, pos);
	}
	
	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		updateAfterDestroy(worldIn, pos);
	}
	
	public void updateAfterDestroy(IWorld worldIn, BlockPos pos) {
//		LOGGER.debug("Supported Block Broken");
		cache.remove(pos);
		
		BlockPos supportBase = pos.up();
		if (FallingBlock.canFallThrough(worldIn.getBlockState(supportBase))) {
			// The block is not supporting anything, so return early.
			return;
		}
		
//		Spliterator<BlockPos> checked_positions = breadthFirstSearch(worldIn.getWorld(), pos);
//		
//		checked_positions.forEachRemaining((BlockPos check_pos) -> {
//			if (!isSupported(worldIn.getWorld(), check_pos)) {
//				// TODO: Do stuff
//			}
//		});
//		
	}

	/**
	 * Perform a breadth-first search for any block within Manhattan-distance {@value this.support_distance} that rests on top of a solid block.
	 * @param worldIn the {@link World}. 
	 * @param pos the block's {@link BlockPos}.
	 * @return {@code true} if the block is supported else {@code false}.
	 */
	public boolean isSupported(World worldIn, BlockPos pos) {
		if (cache.isCachedSupported(pos)) {
			return true;
		}
		
		Pair<Double, Double> hitRate = cache.getHitRate();
		LOGGER.debug("Hit Rate:"+hitRate.getLeft()+","+hitRate.getRight());

		ArrayDeque<BlockPos> search_queue = new ArrayDeque<>(4*this.support_distance);
		HashMap<BlockPos, Integer> discovered_blocks = new HashMap<>(4*this.support_distance);

		search_queue.addLast(pos);
		discovered_blocks.put(pos, 0);

		BlockPos current_pos;
		while ((current_pos = search_queue.pollFirst()) != null)
		{
			int current_distance = discovered_blocks.get(current_pos);

			if (isDirectlySupported(worldIn, current_pos))
			{
				cache.put(current_pos, pos);
				return true;
			}
			
			for (Direction dir: SupportedBlock.SUPPORT_SEARCH_ORDER)
			{
				BlockPos next_pos = current_pos.offset(dir);
				if (!discovered_blocks.containsKey(next_pos) && worldIn.getBlockState(next_pos).isSolid() && current_distance < this.support_distance)
				{
					discovered_blocks.put(next_pos, current_distance + 1);
					search_queue.addLast(next_pos);
				}
			}
		}

		return false;
	}
	
	private Spliterator<BlockPos> breadthFirstSearch(World worldIn, BlockPos pos) {
		ArrayDeque<BlockPos> search_queue = new ArrayDeque<>(4*this.support_distance);
		LinkedHashMap<BlockPos, Integer> discovered_blocks = new LinkedHashMap<>((2*this.support_distance*this.support_distance) + (2*this.support_distance) + 1);

		search_queue.addLast(pos);
		discovered_blocks.put(pos, 0);

		BlockPos current_pos;
		while ((current_pos = search_queue.pollFirst()) != null)
		{
			int current_distance = discovered_blocks.get(current_pos);
			
			for (Direction dir: SupportedBlock.SUPPORT_SEARCH_ORDER)
			{
				BlockPos next_pos = current_pos.offset(dir);
				if (!discovered_blocks.containsKey(next_pos) && worldIn.getBlockState(next_pos).isSolid() && current_distance < this.support_distance)
				{
					discovered_blocks.put(next_pos, current_distance + 1);
					search_queue.addLast(next_pos);
				}
			}
		}

		return discovered_blocks.keySet().spliterator(); 
	}
	

	@Override
	@OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		// Do not spawn particles.
	}
	
}