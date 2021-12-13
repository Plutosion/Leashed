package plutosion.leashed.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobRenderer.class)
public abstract class MobRendererMixin<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
	public MobRendererMixin(EntityRendererProvider.Context context, M entityModelIn, float shadowSizeIn) {
		super(context, entityModelIn, shadowSizeIn);
	}

	@Inject(method = "renderLeash(Lnet/minecraft/world/entity/Mob;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private <E extends Entity> void leashedRenderLeash(T entityLivingIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, E leashHolder, CallbackInfo callback) {
		if(plutosion.leashed.util.LeadUtil.getUsedLeash(entityLivingIn) instanceof plutosion.leashed.item.CustomLeadItem) {
			plutosion.leashed.client.LeashRenderHelper.renderCustomLeash(entityLivingIn, partialTicks, matrixStackIn, bufferIn, leashHolder);
			callback.cancel();
		}
	}
}
