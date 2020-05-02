package den0minat0r.bettersiegetactics.common;


import den0minat0r.bettersiegetactics.BetterSiegeTactics;
import den0minat0r.bettersiegetactics.common.block.HempCrop;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry 
{
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, BetterSiegeTactics.modId);

	public static final RegistryObject<Block> HEMP_CROP = BLOCKS.register("hemp", () -> new HempCrop());
	
}
