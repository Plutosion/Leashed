package plutosion.leashed.client;

import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import plutosion.leashed.Leashed;
import plutosion.leashed.client.renderer.CustomLeashKnotRenderer;
import plutosion.leashed.init.ModRegistry;

public class ClientHandler {
	public static final ModelLayerLocation LEASHED_KNOT = new ModelLayerLocation(new ResourceLocation(Leashed.MOD_ID, "main"), "leashed_knot");

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModRegistry.LEASH_KNOT.get(), CustomLeashKnotRenderer::new);
	}

	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ClientHandler.LEASHED_KNOT, LeashKnotModel::createBodyLayer);
	}
}
