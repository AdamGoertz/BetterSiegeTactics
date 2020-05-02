package den0minat0r.bettersiegetactics.common.block;

import den0minat0r.bettersiegetactics.common.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class HempCrop extends CropsBlock {

	public HempCrop() {
		super(Block.Properties.create(Material.PLANTS).tickRandomly().hardnessAndResistance(0.0F).doesNotBlockMovement().sound(SoundType.CROP));
	}
	
	@Override
	protected IItemProvider getSeedsItem() {
		return ItemRegistry.HEMP_SEEDS.get();
	}
	
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(getSeedsItem());
	}

}
