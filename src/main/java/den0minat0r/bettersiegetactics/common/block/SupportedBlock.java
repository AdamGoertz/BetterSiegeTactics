package den0minat0r.bettersiegetactics.common.block;


import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import den0minat0r.bettersiegetactics.common.util.SupportedBlockCache;

import org.apache.logging.log4j.LogManager;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.ItemStack;
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
    private static final Direction[] SUPPORT_SEARCH_ORDER = new Direction[] {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
    private static final Direction[] SUPPORT_UPDATE_ORDER = new Direction[] {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.UP};
	private static final SupportedBlockCache cache = new SupportedBlockCache(1024);
	private final int support_distance;

	public SupportedBlock(Properties properties, int support_distance) {
		super(properties);
		
		this.support_distance = support_distance;
	}
	
	@Override 
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (!isSupported(worldIn, pos)) {
			fall(worldIn, pos);
		}
	}

	@Override
	public void func_225534_a_(BlockState state, ServerWorld sWorld, BlockPos pos, Random rand) {
		// Do nothing. This functionality is handled elsewhere.
	}
	
	@Override 
	public void onStartFalling(FallingBlockEntity entity) {
		LOGGER.debug("Falling...");
		entity.setHurtEntities(true);
	}
	
	@Override 
	public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState) {
		LOGGER.debug("Landed.");
		cache.put(pos.down(), pos);
	}
	
	public static boolean isDirectlySupported(World worldIn, BlockPos pos) {
		return pos.getY() == 0 || !FallingBlock.canFallThrough(worldIn.getBlockState(pos.down()));
	}
	
	public int getSupportDistance() {
		return this.support_distance;
	}
	
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		LOGGER.debug("Destroyed by player.");
		updateAfterDestroy(worldIn, pos);
	}
	
	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		LOGGER.debug("Destroyed by explosion.");
		updateAfterDestroy(worldIn, pos);
	}
	

	private void updateAfterDestroy(IWorld worldIn, BlockPos pos) {
		cache.remove(pos);
		
		for (BlockPos checkPos : getAllBlocksInSupportRange(worldIn.getWorld(), pos, SupportedBlock.SUPPORT_UPDATE_ORDER)) {
			if (!isSupported(worldIn.getWorld(), checkPos)) {
				fall(worldIn, checkPos);
			}
		}
	}
	
	private void fall(IWorld worldIn, BlockPos pos) {
		FallingBlockEntity fallingblockentity = new FallingBlockEntity(worldIn.getWorld(), (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
        this.onStartFalling(fallingblockentity);
        worldIn.addEntity(fallingblockentity);
        cache.remove(pos);
	}
	
	/**
	 * Perform a breadth-first search for any block within Manhattan-distance {@value this.support_distance} that rests on top of a solid block.
	 * @param worldIn the {@link World}. 
	 * @param pos the block's {@link BlockPos}.
	 * @return {@code true} if the block is supported else {@code false}.
	 */
	public boolean isSupported(World worldIn, BlockPos pos) {
		if (cache.isSupported(pos)) {
			return true;
		}
		
		for (BlockPos checkPos : getAllBlocksInSupportRange(worldIn, pos, SupportedBlock.SUPPORT_SEARCH_ORDER)) {
			if (SupportedBlock.isDirectlySupported(worldIn, checkPos)) {
				cache.put(checkPos, pos);
				return true;
			}
		}
	
		return false;
	}
	
	private Set<BlockPos> getAllBlocksInSupportRange(World worldIn, BlockPos pos, Direction[] searchDirections) {
		ArrayDeque<BlockPos> search_queue = new ArrayDeque<>(4*this.support_distance);
		LinkedHashMap<BlockPos, Integer> discovered_blocks = new LinkedHashMap<>((2*this.support_distance*this.support_distance) + (2*this.support_distance) + 1);

		search_queue.addLast(pos);
		discovered_blocks.put(pos, 0);

		BlockPos current_pos;
		while ((current_pos = search_queue.pollFirst()) != null)
		{
			int current_distance = discovered_blocks.get(current_pos);
			
			for (Direction dir: searchDirections)
			{
				BlockPos next_pos = current_pos.offset(dir);

				if (!discovered_blocks.containsKey(next_pos) && worldIn.getBlockState(next_pos).isSolid() && current_distance < this.support_distance)
				{
					discovered_blocks.put(next_pos, current_distance + 1);
					search_queue.addLast(next_pos);
				}
			}
		}
		
		return discovered_blocks.keySet();
	}
	

	@Override
	@OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		// Do not spawn particles.
	}
	
}