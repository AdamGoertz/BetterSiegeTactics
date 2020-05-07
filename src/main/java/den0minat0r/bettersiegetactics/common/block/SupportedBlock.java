package den0minat0r.bettersiegetactics.common.block;


import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;
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
	private static final Direction[] SUPPORT_SEARCH_ORDER = new Direction[] { Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH };
	private static final Direction[] SUPPORT_UPDATE_ORDER = new Direction[] { Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.UP };
	private final int support_distance;
	private ServerWorld server;

	public SupportedBlock(Properties properties, int support_distance) {
		super(properties);
		
		this.support_distance = support_distance;
	}
	
	@Override 
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		LOGGER.debug("Placed block "+pos.getX()+","+pos.getY()+","+pos.getZ());
		if (worldIn.isAreaLoaded(pos, 32) && !isSupported(worldIn, pos)) {
			fall(worldIn, pos);
		}
	}

	@Override
	public void func_225534_a_(BlockState state, ServerWorld sWorld, BlockPos pos, @Nullable Random rand) {
		LOGGER.debug("Tick Executed");
		this.server = sWorld;
		if (sWorld.isAreaLoaded(pos,  32) && !isSupported(sWorld, pos)) {
			fall(sWorld, pos);
		}
	}
	
	@Override 
	protected void onStartFalling(FallingBlockEntity entity) {
		LOGGER.debug("Falling...");
		entity.setHurtEntities(true);
	}
	
	
	public static boolean isDirectlySupported(World worldIn, BlockPos pos) {
		return pos.getY() == 0 || !FallingBlock.canFallThrough(worldIn.getBlockState(pos.down()));
	}
	
	public int getSupportDistance() {
		return this.support_distance;
	}
	
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		updateAfterDestroy(worldIn, pos);
	}
	
	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		updateAfterDestroy(worldIn, pos);
	}
	
	private void updateAfterDestroy(IWorld worldIn, BlockPos pos) {
		if (worldIn.isAreaLoaded(pos, 32)) {
			Set<BlockPos> blocks = getAllConnectedBlocksInRange(worldIn.getWorld(), pos, SUPPORT_UPDATE_ORDER, this.support_distance + 1);
			LOGGER.debug("Updating "+blocks.size()+" blocks.");
			for (BlockPos checkPos : blocks) {
				if (this.server != null) {
					this.server.getPendingBlockTicks().scheduleTick(checkPos, this, 0);
				}
			}
		}
	}

	private void fall(IWorld worldIn, BlockPos pos) {
		if (this.server != null)
		{
			FallingBlockEntity fallingblockentity = new FallingBlockEntity(this.server, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
			this.onStartFalling(fallingblockentity);
			this.server.addEntity(fallingblockentity);
		}
	}
	
	/**
	 * @param worldIn the {@link World}. 
	 * @param pos the block's {@link BlockPos}.
	 * @return {@code true} if the block is supported else {@code false}.
	 */
	public boolean isSupported(World worldIn, BlockPos pos) {
		if (worldIn.isAreaLoaded(pos, 32)) {
			LOGGER.debug("Checking for support at "+pos.getX()+","+pos.getY()+","+pos.getZ());
			Set<BlockPos> blocks = getAllConnectedBlocksInRange(worldIn, pos, SUPPORT_SEARCH_ORDER, this.support_distance);
			LOGGER.debug("Checking "+blocks.size()+" blocks.");
			for (BlockPos checkPos : blocks) {
				if (SupportedBlock.isDirectlySupported(worldIn, checkPos)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	private static Set<BlockPos> getAllConnectedBlocksInRange(World worldIn, BlockPos pos, Direction[] searchDirections, int range) {
		ArrayDeque<BlockPos> search_queue = new ArrayDeque<>(4*range);
		LinkedHashMap<BlockPos, Integer> discovered_blocks = new LinkedHashMap<>((2*range*range) + (2*range) + 1);

		search_queue.addLast(pos);
		discovered_blocks.put(pos, 0);

		BlockPos current_pos;
		while ((current_pos = search_queue.pollFirst()) != null)
		{
			int current_distance = discovered_blocks.get(current_pos);
			
			for (Direction dir: searchDirections)
			{
				BlockPos next_pos = current_pos.offset(dir);

				if (!discovered_blocks.containsKey(next_pos) && worldIn.getBlockState(next_pos).isSolid() && current_distance < range)
				{
					discovered_blocks.put(next_pos, current_distance + 1);
					search_queue.addLast(next_pos);
				}
			}
		}

		// Remove initial block if it is not solid... was needed previously to provide root of tree.
		if (!worldIn.getBlockState(pos).isSolid()) {
			discovered_blocks.remove(pos);
		}
		
		return discovered_blocks.keySet();	
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		// Do not spawn particles.
	}
	
}