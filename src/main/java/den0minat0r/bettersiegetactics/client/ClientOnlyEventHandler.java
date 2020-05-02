package den0minat0r.bettersiegetactics.client;

import den0minat0r.bettersiegetactics.common.BlockRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ClientOnlyEventHandler {

	@SubscribeEvent
	public static void setRenderTypes(final FMLClientSetupEvent event) {
    	RenderTypeLookup.setRenderLayer(BlockRegistry.HEMP_CROP.get(), RenderType.func_228643_e_()); // func_228643_e_ -> field_228617_T_ := cutout
	}

}
