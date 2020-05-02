package den0minat0r.bettersiegetactics.common.block;

import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class RotatingCauldronBlock implements IFluidHandler {

	public RotatingCauldronBlock() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTankCapacity(int tank) {
		return 1000;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return stack.isFluidEqual(new FluidStack(Fluids.WATER, 0));
	}

	public boolean isFluidValid(int tank, ItemStack stack) {
		return stack.isItemEqual(new ItemStack(Items.SAND, 0));
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int fill(ItemStack resource, FluidAction action) {
		// Convert Item to appropriate fluid type.
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		// TODO Auto-generated method stub
		return null;
	}

}
