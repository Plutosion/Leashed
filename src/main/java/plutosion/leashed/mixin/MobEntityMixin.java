package plutosion.leashed.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity {

	protected MobEntityMixin(EntityType<? extends LivingEntity> type, Level worldIn) {
		super(type, worldIn);
	}

	@Inject(at = @At("HEAD"), method = "checkAndHandleImportantInteractions(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
	public void checkAndHandleImportantInteractions(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		Mob entity = (Mob) (Object) this;
		ItemStack itemstack = player.getItemInHand(hand);
		if(itemstack.getItem() instanceof LeadItem && plutosion.leashed.util.LeadUtil.canBeCustomleashed(entity, player, itemstack)) {
			CompoundTag persistentNBT = entity.getPersistentData();
			persistentNBT.putString("LeadItem", itemstack.getItem().getRegistryName().toString());
			entity.setLeashedTo(player, true);
			plutosion.leashed.events.LeashHooks.onMobLeashed(entity, player);
			itemstack.shrink(1);
			cir.setReturnValue(InteractionResult.sidedSuccess(entity.level.isClientSide));
		}
	}

	/**
	 * @author Mrbysco
	 * @reason There's no other good way to do it
	 */
	@Overwrite()
	public void dropLeash(boolean sendPacket, boolean dropLead) {
		Mob mobEntity = (Mob) (Object) this;
		if (mobEntity.leashHolder != null) {
			plutosion.leashed.events.LeashHooks.onMobUnleashed(mobEntity, mobEntity.leashHolder);

			mobEntity.leashHolder = null;
			mobEntity.leashInfoTag = null;
			if (!mobEntity.level.isClientSide && dropLead) {
				mobEntity.spawnAtLocation(plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity));
			}

			if (!mobEntity.level.isClientSide && sendPacket && mobEntity.level instanceof ServerLevel) {
				((ServerLevel)mobEntity.level).getChunkSource().broadcast(mobEntity, new ClientboundSetEntityLinkPacket(mobEntity, null));
			}
		}
	}

	/**
	 * @author Mrbysco
	 * @reason There's no other good way to do it
	 */
	@Overwrite
	private void restoreLeashFromSave() {
		Mob mobEntity = (Mob) (Object) this;
		if (mobEntity.leashInfoTag != null && mobEntity.level instanceof ServerLevel) {
			plutosion.leashed.events.LeashHooks.onMobLeashed(mobEntity, mobEntity.leashHolder);
			Item leadItem = plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity);

			if (mobEntity.leashInfoTag.hasUUID("UUID")) {
				UUID uuid = mobEntity.leashInfoTag.getUUID("UUID");
				Entity entity = ((ServerLevel)mobEntity.level).getEntity(uuid);
				if (entity != null) {
					mobEntity.setLeashedTo(entity, true);
					return;
				}
			} else if (mobEntity.leashInfoTag.contains("X", 99) && mobEntity.leashInfoTag.contains("Y", 99) && mobEntity.leashInfoTag.contains("Z", 99)) {
				BlockPos blockpos = new BlockPos(mobEntity.leashInfoTag.getInt("X"), mobEntity.leashInfoTag.getInt("Y"), mobEntity.leashInfoTag.getInt("Z"));
				if(leadItem instanceof plutosion.leashed.item.CustomLeadItem) {
					mobEntity.setLeashedTo(plutosion.leashed.entity.CustomLeashKnotEntity.getOrCreateCustomKnot(mobEntity.level, blockpos), true);
				} else {
					mobEntity.setLeashedTo(LeashFenceKnotEntity.getOrCreateKnot(mobEntity.level, blockpos), true);
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
