package den0minat0r.bettersiegetactics.common.block;


import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SupportedBlock extends FallingBlock {
	
    private static final Direction[] SUPPORT_SEARCH_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
	private final int support_distance;

	public SupportedBlock(Properties properties, int support_distance) {
		super(properties);
		
		this.support_distance = support_distance;
		
	}
	
	@Override
	public void func_225534_a_(BlockState state, ServerWorld sWorld, BlockPos pos, Random rand) {
		if (!isSupported(sWorld.getWorld(), pos)) 
		{
			super.func_225534_a_(state, sWorld, pos, rand);
		}
	}
	
	public static boolean isDirectlySupported(World worldIn, BlockPos pos) {
		return pos.getY() == 0 || !FallingBlock.canFallThrough(worldIn.getBlockState(pos.down()));
	}
	
	public int getSupportDistance() {
		return this.support_distance;
	}
	
	public boolean isSupported(World worldIn, BlockPos pos) {
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
	
	/**
	 *  Find the {@link BlockPos} of the air {@link Block} directly above the next solid {@link Block} directly below the given {@link BlockPos}.
	 *  
	 * @param worldIn the world.
	 * @param pos the position of the source {@link Block}.
	 * @return the {@link BlockPos} of the lowest air {@link Block} above a solid block .
	 */
	public static BlockPos getPosAboveNearestSolidBlock(World worldIn, BlockPos pos) {
		while (pos.getY() >= 0 && FallingBlock.canFallThrough(worldIn.getBlockState(pos.down()))) {
			pos = pos.down();
		}
		return pos;
	}
	

	@Override
	@OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		// Do not spawn particles.
	}
	
}