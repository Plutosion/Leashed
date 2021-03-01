package plutosion.leashed.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeadItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(MobEntity.class)
public class MobEntityMixin {
	@Inject(at = @At("HEAD"), method = "func_233661_c_", cancellable = true)
	public void func_233661_c_(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
		MobEntity entity = (MobEntity) (Object) this;
		ItemStack itemstack = player.getHeldItem(hand);
		if(itemstack.getItem() instanceof LeadItem && plutosion.leashed.util.LeadUtil.canBeCustomleashed(entity, player, itemstack)) {
			CompoundNBT persistentNBT = entity.getPersistentData();
			persistentNBT.putString("LeadItem", itemstack.getItem().getRegistryName().toString());
			entity.setLeashHolder(player, true);
			plutosion.leashed.events.LeashHooks.onMobLeashed(entity, player);
			itemstack.shrink(1);
			cir.setReturnValue(ActionResultType.func_233537_a_(entity.world.isRemote));
		}
	}

	@Overwrite
	public void clearLeashed(boolean sendPacket, boolean dropLead) {
		MobEntity entity = (MobEntity) (Object) this;
		if (entity.leashHolder != null) {
			plutosion.leashed.events.LeashHooks.onMobUnleashed(entity, entity.leashHolder);
			entity.forceSpawn = false;
			if (!(entity.leashHolder instanceof PlayerEntity)) {
				entity.leashHolder.forceSpawn = false;
			}

			entity.leashHolder = null;
			entity.leashNBTTag = null;
			if (!entity.world.isRemote && dropLead) {
				entity.entityDropItem(plutosion.leashed.util.LeadUtil.getUsedLeash(entity));
			}

			if (!entity.world.isRemote && sendPacket && entity.world instanceof ServerWorld) {
				((ServerWorld)entity.world).getChunkProvider().sendToAllTracking(entity, new SMountEntityPacket(entity, (Entity)null));
			}
		}
	}

	@Overwrite
	private void recreateLeash() {
		MobEntity mobEntity = (MobEntity) (Object) this;
		if (mobEntity.leashNBTTag != null && mobEntity.world instanceof ServerWorld) {
			if (mobEntity.leashNBTTag.hasUniqueId("UUID")) {
				UUID uuid = mobEntity.leashNBTTag.getUniqueId("UUID");
				Entity entity = ((ServerWorld)mobEntity.world).getEntityByUuid(uuid);
				if (entity != null) {
					mobEntity.setLeashHolder(entity, true);
					return;
				}
			} else if (mobEntity.leashNBTTag.contains("X", 99) && mobEntity.leashNBTTag.contains("Y", 99) && mobEntity.leashNBTTag.contains("Z", 99)) {
				BlockPos blockpos = new BlockPos(mobEntity.leashNBTTag.getInt("X"), mobEntity.leashNBTTag.getInt("Y"), mobEntity.leashNBTTag.getInt("Z"));
				mobEntity.setLeashHolder(LeashKnotEntity.create(mobEntity.world, blockpos), true);
				return;
			}

			if (mobEntity.ticksExisted > 100) {
				mobEntity.entityDropItem(plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity));
				mobEntity.leashNBTTag = null;
			}
		}
	}
}
