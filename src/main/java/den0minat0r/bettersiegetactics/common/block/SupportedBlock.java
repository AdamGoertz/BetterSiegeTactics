package den0minat0r.bettersiegetactics.common.block;

import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.HashSet;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SupportedBlock extends Block {
	
	private static final Logger LOGGER = LogManager.getLogger(); 
    private static final Direction[] SUPPORT_SEARCH_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
    private static final int MAX_SEARCH_DISTANCE = 32;
	private final int support_distance;

	public SupportedBlock(Properties properties, int support_distance) {
		super(properties);
		
		this.support_distance = support_distance;
		
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (!isSupported(worldIn, pos)) {
			fall(state, worldIn, pos);
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState fromState = worldIn.getBlockState(fromPos);
		if (!fromState.isSolid()) // TODO: What if the neighbor is changed to a block with lower support_distance? 
		{
			if (!this.isSupported(worldIn, pos)) 
			{
				fall(state, worldIn, pos);
			}
		}
	}
	
	public static boolean isDirectlySupported(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isSolid();
	}
	
	public int getSupportDistance() {
		return this.support_distance;
	}
	
	public boolean isSupported(World worldIn, BlockPos pos) {
		ArrayDeque<BlockPos> search_queue = new ArrayDeque<>(4*this.support_distance);
		HashSet<BlockPos> discovered_blocks = new HashSet<>(4*this.support_distance);
		search_queue.addLast(pos);
		return isSupportedHelper(worldIn, 0, search_queue, discovered_blocks);
	}
	
	private static boolean isSupportedHelper(World worldIn, int distance, ArrayDeque<BlockPos> search_queue, HashSet<BlockPos> discovered_blocks) {
		BlockPos current_pos = search_queue.pollFirst();

		if (current_pos == null)
		{
			return false; 
		}

		discovered_blocks.add(current_pos);

		if (isDirectlySupported(worldIn, current_pos))
		{
			return true;
		}
		else
		{
			Block current_block = worldIn.getBlockState(current_pos).getBlock();
			
			if (current_block instanceof SupportedBlock && distance >= ((SupportedBlock)current_block).getSupportDistance())
			{
				return false;
			}
			else if (distance >= SupportedBlock.MAX_SEARCH_DISTANCE)
			{
				return false;
			}
			
			for (Direction dir: SupportedBlock.SUPPORT_SEARCH_ORDER)
			{
				BlockPos next_pos = current_pos.offset(dir);
				if (!discovered_blocks.contains(next_pos) && worldIn.getBlockState(next_pos).isSolid())
				{
					search_queue.addLast(next_pos);
				}
			}
			
			return isSupportedHelper(worldIn, distance + 1, search_queue, discovered_blocks);
		}
	}
	
	/**
	 *  Find the {@link BlockPos} of the air {@link Block} directly above the next solid {@link Block} directly below the given {@link BlockPos}.
	 *  
	 * @param worldIn the world.
	 * @param pos the position of the source {@link Block}.
	 * @return the {@link BlockPos} of the lowest air {@link Block} above a solid block .
	 */
	public BlockPos getPosAboveNearestSolidBlock(World worldIn, BlockPos pos) {
		while (!worldIn.getBlockState(pos.down()).isSolid()) {
			pos = pos.down();
		}
		return pos;
	}
	
	public void fall(BlockState state, World worldIn, BlockPos pos) {
		BlockPos fall_pos = getPosAboveNearestSolidBlock(worldIn, pos);
		BlockState ground_state = worldIn.getBlockState(fall_pos);
		Block.replaceBlock(ground_state, state, worldIn, fall_pos, 0);
		worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
	}
	
}