package plutosion.leashed.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

	@Inject(method = "dropLeash(ZZ)V", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/Mob;leashHolder:Lnet/minecraft/world/entity/Entity;",
			shift = Shift.BEFORE,
			ordinal = 1))
	public void leashedFireUnleashed(boolean sendPacket, boolean dropLead, CallbackInfo ci) {
		Mob mobEntity = (Mob) (Object) this;
		plutosion.leashed.events.LeashHooks.onMobUnleashed(mobEntity, mobEntity.leashHolder);
	}

	@ModifyArg(method = "dropLeash(ZZ)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Mob;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"),
			index = 0)
	public ItemLike leashedChangeDropLeash(ItemLike itemLike) {
		Mob mobEntity = (Mob) (Object) this;
		return plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity);
	}

	@Inject(method = "restoreLeashFromSave()V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/nbt/CompoundTag;hasUUID(Ljava/lang/String;)Z",
			shift = Shift.BEFORE,
			ordinal = 0))
	private void LeashedFireOnLeashed(CallbackInfo ci) {
		Mob mobEntity = (Mob) (Object) this;
		plutosion.leashed.events.LeashHooks.onMobLeashed(mobEntity, mobEntity.leashHolder);
	}

	@Inject(method = "restoreLeashFromSave()V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/nbt/NbtUtils;readBlockPos(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/core/BlockPos;",
			shift = Shift.BEFORE,
			ordinal = 0), cancellable = true)
	private void LeashedSetLeashedTo(CallbackInfo ci) {
		Mob mobEntity = (Mob) (Object) this;
		BlockPos blockpos = NbtUtils.readBlockPos(mobEntity.leashInfoTag);
		Item leadItem = plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity);
		if(leadItem instanceof plutosion.leashed.item.CustomLeadItem) {
			mobEntity.setLeashedTo(plutosion.leashed.entity.CustomLeashKnotEntity.getOrCreateCustomKnot(mobEntity.level, blockpos), true);
		} else {
			mobEntity.setLeashedTo(LeashFenceKnotEntity.getOrCreateKnot(mobEntity.level, blockpos), true);
		}
		ci.cancel();
	}

	@ModifyArg(method = "restoreLeashFromSave()V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Mob;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"),
			index = 0)
	public ItemLike leashedRestoreLeashFromSaveChangeDrop(ItemLike itemLike) {
		Mob mobEntity = (Mob) (Object) this;
		return plutosion.leashed.util.LeadUtil.getUsedLeash(mobEntity);
	}

}
