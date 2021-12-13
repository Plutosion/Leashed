package plutosion.leashed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import plutosion.leashed.Leashed;
import plutosion.leashed.client.ClientHandler;
import plutosion.leashed.entity.CustomLeashKnotEntity;

@OnlyIn(Dist.CLIENT)
public class CustomLeashKnotRenderer extends EntityRenderer<CustomLeashKnotEntity> {
	private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation(Leashed.MOD_ID, "textures/entity/diamond_lead_knot.png");
	private final LeashKnotModel<CustomLeashKnotEntity> model;

	public CustomLeashKnotRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new LeashKnotModel<>(context.bakeLayer(ClientHandler.LEASHED_KNOT));
	}

	public void render(CustomLeashKnotEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		poseStack.pushPose();
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		this.model.setupAnim(entityIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		VertexConsumer vertexConsumer = bufferIn.getBuffer(this.model.renderType(LEASH_KNOT_TEXTURES));
		this.model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public ResourceLocation getTextureLocation(CustomLeashKnotEntity entity) {
		return LEASH_KNOT_TEXTURES;
	}
}
