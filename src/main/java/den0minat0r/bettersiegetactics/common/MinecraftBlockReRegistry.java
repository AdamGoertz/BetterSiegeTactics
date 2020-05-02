package den0minat0r.bettersiegetactics.common;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import den0minat0r.bettersiegetactics.common.block.SupportedBlock;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MinecraftBlockReRegistry {

	private static final Logger LOGGER = LogManager.getLogger(); 
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, "minecraft");
	
	public static HashMap<String, RegistryObject<Block>> BLOCK_REGISTRY_OBJECTS = new HashMap<>();
	
	static {
		String[] overridden_items = { "cobblestone" };
		for (String item : overridden_items) 
		{
			LOGGER.info("Overriding registry entry for minecraft:" + item);
			Block.Properties props = Block.Properties.from(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft", item)));
			BLOCK_REGISTRY_OBJECTS.put(item, BLOCKS.register(item, () -> new SupportedBlock(props, 3)));
		}
	}
}