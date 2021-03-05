package plutosion.leashed.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobRenderer.class)
public abstract class MobRendererMixin<T extends MobEntity, M extends EntityModel<T>> extends LivingRenderer<T, M> {
	public MobRendererMixin(EntityRendererManager renderManagerIn, M entityModelIn, float shadowSizeIn) {
		super(renderManagerIn, entityModelIn, shadowSizeIn);
	}

	@Inject(method = "renderLeash(Lnet/minecraft/entity/MobEntity;FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private <E extends Entity> void renderTheLeash(T entityLivingIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, E leashHolder, CallbackInfo callback) {
		if(plutosion.leashed.util.LeadUtil.getUsedLeash(entityLivingIn) instanceof plutosion.leashed.item.CustomLeadItem) {
			plutosion.leashed.client.LeashRenderHelper.renderCustomLeash(entityLivingIn, partialTicks, matrixStackIn, bufferIn, leashHolder);
			callback.cancel();
		}
	}
}
