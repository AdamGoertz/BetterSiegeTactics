package den0minat0r.bettersiegetactics;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import den0minat0r.bettersiegetactics.common.BlockRegistry;
import den0minat0r.bettersiegetactics.common.ItemRegistry;
import den0minat0r.bettersiegetactics.common.MinecraftBlockReRegistry;

@Mod(BetterSiegeTactics.modId)
public class BetterSiegeTactics
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String modId = "bettersiegetactics";

    public BetterSiegeTactics() {
    	
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        BlockRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());        
        ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());        
        MinecraftBlockReRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event) {
    	LOGGER.info("Setting things up...");
    }

}
