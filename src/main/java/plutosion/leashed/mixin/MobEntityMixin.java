package plutosion.leashed.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeadItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

	protected MobEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Inject(at = @At("HEAD"), method = "processInteract(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResultType;", cancellable = true)
	public void processInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
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

	/**
	 * @author Mrbysco
	 * @reason There's no other good way to do it
	 */
	@Overwrite()
	public void clearLeashed(boolean sendPacket, boolean dropLead) {
		MobEntity mobEntity = (MobEntity) (Object) this;
		if (mobEntity.leashHolder != null) {
			plutosion.leashed.events.LeashHooks.onMobUnleashed(mobEntity, mobEntity.leashHolder);
			mobEntity.forceSpawn = false;
			if (!(mobEntity.leashHolder instanceof PlayerEntity)) {
				mobEntity.leashHolder.forceSpawn = false;
			}

			mobEntity.leashHolder = null;
			mobEntity.leashNBTTag = null;
			if (!mobEntity.world.isRemote && dropLead) {
				mobEntity.entityDropItem(plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity));
			}

			if (!mobEntity.world.isRemote && sendPacket && mobEntity.world instanceof ServerWorld) {
				((ServerWorld)mobEntity.world).getChunkProvider().sendToAllTracking(mobEntity, new SMountEntityPacket(mobEntity, (Entity)null));
			}
		}
	}

	/**
	 * @author Mrbysco
	 * @reason There's no other good way to do it
	 */
	@Overwrite
	private void recreateLeash() {
		MobEntity mobEntity = (MobEntity) (Object) this;
		if (mobEntity.leashNBTTag != null && mobEntity.world instanceof ServerWorld) {
			plutosion.leashed.events.LeashHooks.onMobLeashed(mobEntity, mobEntity.leashHolder);
			Item leadItem = plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity);

			if (mobEntity.leashNBTTag.hasUniqueId("UUID")) {
				UUID uuid = mobEntity.leashNBTTag.getUniqueId("UUID");
				Entity entity = ((ServerWorld)mobEntity.world).getEntityByUuid(uuid);
				if (entity != null) {
					mobEntity.setLeashHolder(entity, true);
					return;
				}
			} else if (mobEntity.leashNBTTag.contains("X", 99) && mobEntity.leashNBTTag.contains("Y", 99) && mobEntity.leashNBTTag.contains("Z", 99)) {
				BlockPos blockpos = new BlockPos(mobEntity.leashNBTTag.getInt("X"), mobEntity.leashNBTTag.getInt("Y"), mobEntity.leashNBTTag.getInt("Z"));
				if(leadItem instanceof plutosion.leashed.item.CustomLeadItem) {
					mobEntity.setLeashHolder(plutosion.leashed.entity.CustomLeashKnotEntity.createCustomLeash(mobEntity.world, blockpos), true);
				} else {
					mobEntity.setLeashHolder(LeashKnotEntity.create(mobEntity.world, blockpos), true);
				}
				return;
			}

			if (mobEntity.ticksExisted > 100) {
				mobEntity.entityDropItem(leadItem);
				mobEntity.leashNBTTag = null;
			}
		}
	}
}
