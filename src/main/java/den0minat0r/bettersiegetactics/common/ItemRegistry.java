package den0minat0r.bettersiegetactics.common;

import den0minat0r.bettersiegetactics.BetterSiegeTactics;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry 
{
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, BetterSiegeTactics.modId);

	public static final RegistryObject<Item> HEMP = ITEMS.register("hemp", () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
	
	public static final RegistryObject<Item> HEMP_SEEDS = ITEMS.register("hemp_seeds", () -> new BlockNamedItem(BlockRegistry.HEMP_CROP.get(), new Item.Properties().group(ItemGroup.MISC)));
	
	public static final RegistryObject<Item> ROPE = ITEMS.register("rope",  () -> new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
	
}
