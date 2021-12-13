package plutosion.leashed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LightLayer;

public class LeashRenderHelper {
	public static void renderCustomLeash(Mob mob, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity leashHolder) {
		poseStack.pushPose();
		Vec3 vec3 = leashHolder.getRopeHoldPosition(partialTicks);
		double d0 = (double)(Mth.lerp(partialTicks, mob.yBodyRot, mob.yBodyRotO) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
		Vec3 vec31 = mob.getLeashOffset();
		double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
		double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
		double d3 = Mth.lerp(partialTicks, mob.xo, mob.getX()) + d1;
		double d4 = Mth.lerp(partialTicks, mob.yo, mob.getY()) + vec31.y;
		double d5 = Mth.lerp(partialTicks, mob.zo, mob.getZ()) + d2;
		poseStack.translate(d1, vec31.y, d2);
		float f = (float)(vec3.x - d3);
		float f1 = (float)(vec3.y - d4);
		float f2 = (float)(vec3.z - d5);
		float f3 = 0.025F;
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
		Matrix4f pose = poseStack.last().pose();
		float f4 = Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
		float f5 = f2 * f4;
		float f6 = f * f4;
		BlockPos mobEyePosition = new BlockPos(mob.getEyePosition(partialTicks));
		BlockPos holderEyePos = new BlockPos(leashHolder.getEyePosition(partialTicks));
		int mobLightLevel = getBlockLightLevel(mob, mobEyePosition);
		int holderLightLevel = getBlockLightLevel(leashHolder, holderEyePos);
		int mobBrightness = mob.level.getBrightness(LightLayer.SKY, mobEyePosition);
		int holderBrightness = mob.level.getBrightness(LightLayer.SKY, holderEyePos);

		for(int i1 = 0; i1 <= 24; ++i1) {
			addVertexPair(vertexConsumer, pose, f, f1, f2, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0.025F, f5, f6, i1, false);
		}

		for(int j1 = 24; j1 >= 0; --j1) {
			addVertexPair(vertexConsumer, pose, f, f1, f2, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0.0F, f5, f6, j1, true);
		}

		poseStack.popPose();
	}

	protected static int getBlockLightLevel(Entity mob, BlockPos pos) {
		return mob.isOnFire() ? 15 : mob.level.getBrightness(LightLayer.BLOCK, pos);
	}

	private static void addVertexPair(VertexConsumer vertexConsumer, Matrix4f pose, float p_174310_, float p_174311_, float p_174312_, int mobLightLevel, int holderLightLevel, int mobBrightness, int holderBrightness, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
		float f = (float)p_174321_ / 24.0F;
		int i = (int)Mth.lerp(f, (float)mobLightLevel, (float)holderLightLevel);
		int j = (int)Mth.lerp(f, (float)mobBrightness, (float)holderBrightness);
		int k = LightTexture.pack(i, j);
		float f1 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
		float f2 = 0.29F * f1;
		float f3 = 0.929F * f1;
		float f4 = 0.851F * f1;
//		Original colors:
//		float f2 = 0.5F * f1;
//		float f3 = 0.4F * f1;
//		float f4 = 0.3F * f1;
		float f5 = p_174310_ * f;
		float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
		float f7 = p_174312_ * f;
		vertexConsumer.vertex(pose, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
		vertexConsumer.vertex(pose, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
	}
}
