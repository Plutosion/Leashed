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

	@Inject(at = @At("HEAD"), method = "checkAndHandleImportantInteractions(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResultType;", cancellable = true)
	public void checkAndHandleImportantInteractions(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
		MobEntity entity = (MobEntity) (Object) this;
		ItemStack itemstack = player.getItemInHand(hand);
		if(itemstack.getItem() instanceof LeadItem && plutosion.leashed.util.LeadUtil.canBeCustomleashed(entity, player, itemstack)) {
			CompoundNBT persistentNBT = entity.getPersistentData();
			persistentNBT.putString("LeadItem", itemstack.getItem().getRegistryName().toString());
			entity.setLeashedTo(player, true);
			plutosion.leashed.events.LeashHooks.onMobLeashed(entity, player);
			itemstack.shrink(1);
			cir.setReturnValue(ActionResultType.sidedSuccess(entity.level.isClientSide));
		}
	}

	/**
	 * @author Mrbysco
	 * @reason There's no other good way to do it
	 */
	@Overwrite()
	public void dropLeash(boolean sendPacket, boolean dropLead) {
		MobEntity mobEntity = (MobEntity) (Object) this;
		if (mobEntity.leashHolder != null) {
			plutosion.leashed.events.LeashHooks.onMobUnleashed(mobEntity, mobEntity.leashHolder);
			mobEntity.forcedLoading = false;
			if (!(mobEntity.leashHolder instanceof PlayerEntity)) {
				mobEntity.leashHolder.forcedLoading = false;
			}

			mobEntity.leashHolder = null;
			mobEntity.leashInfoTag = null;
			if (!mobEntity.level.isClientSide && dropLead) {
				mobEntity.spawnAtLocation(plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity));
			}

			if (!mobEntity.level.isClientSide && sendPacket && mobEntity.level instanceof ServerWorld) {
				((ServerWorld)mobEntity.level).getChunkSource().broadcast(mobEntity, new SMountEntityPacket(mobEntity, (Entity)null));
			}
		}
	}

	/**
	 * @author Mrbysco
	 * @reason There's no other good way to do it
	 */
	@Overwrite
	private void restoreLeashFromSave() {
		MobEntity mobEntity = (MobEntity) (Object) this;
		if (mobEntity.leashInfoTag != null && mobEntity.level instanceof ServerWorld) {
			plutosion.leashed.events.LeashHooks.onMobLeashed(mobEntity, mobEntity.leashHolder);
			Item leadItem = plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity);

			if (mobEntity.leashInfoTag.hasUUID("UUID")) {
				UUID uuid = mobEntity.leashInfoTag.getUUID("UUID");
				Entity entity = ((ServerWorld)mobEntity.level).getEntity(uuid);
				if (entity != null) {
					mobEntity.setLeashedTo(entity, true);
					return;
				}
			} else if (mobEntity.leashInfoTag.contains("X", 99) && mobEntity.leashInfoTag.contains("Y", 99) && mobEntity.leashInfoTag.contains("Z", 99)) {
				BlockPos blockpos = new BlockPos(mobEntity.leashInfoTag.getInt("X"), mobEntity.leashInfoTag.getInt("Y"), mobEntity.leashInfoTag.getInt("Z"));
				if(leadItem instanceof plutosion.leashed.item.CustomLeadItem) {
					mobEntity.setLeashedTo(plutosion.leashed.entity.CustomLeashKnotEntity.createCustomLeash(mobEntity.level, blockpos), true);
				} else {
					mobEntity.setLeashedTo(LeashKnotEntity.getOrCreateKnot(mobEntity.level, blockpos), true);
				}
				return;
			}

			if (mobEntity.tickCount > 100) {
				mobEntity.spawnAtLocation(leadItem);
				mobEntity.leashInfoTag = null;
			}
		}
	}
}
