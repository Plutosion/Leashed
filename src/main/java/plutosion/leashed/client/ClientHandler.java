package plutosion.leashed.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import plutosion.leashed.client.renderer.CustomLeashKnotRenderer;
import plutosion.leashed.init.ModRegistry;

public class ClientHandler {
	public static void onClientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ModRegistry.LEASH_KNOT.get(), CustomLeashKnotRenderer::new);
	}
}
