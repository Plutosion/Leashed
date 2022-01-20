package plutosion.leashed.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeadItem.class)
public class LeadItemMixin {

	@Inject(method = "bindPlayerMobs(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/InteractionResult;",
			at = @At(value = "HEAD"),
			cancellable = true)
	private static void leashedBindPlayerMobs(Player player, Level level, BlockPos pos, CallbackInfoReturnable<InteractionResult> cir) {
		LeashFenceKnotEntity leashfenceknotentity = null;
		boolean flag = false;
		double d0 = 7.0D;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		for(Mob mob : level.getEntitiesOfClass(Mob.class, new AABB((double)x - d0, (double)y - d0, (double)z - d0, (double)x + d0, (double)y + d0, (double)z + d0))) {
			if (mob.getLeashHolder() == player) {
				if (leashfenceknotentity == null) {
					Item boundLeash = plutosion.leashed.util.LeadUtil.getUsedLeash(mob);
					if(boundLeash instanceof plutosion.leashed.item.CustomLeadItem) {
						leashfenceknotentity = plutosion.leashed.entity.CustomLeashKnotEntity.getOrCreateCustomKnot(level, pos);
						leashfenceknotentity.playPlacementSound();
					} else {
						leashfenceknotentity = LeashFenceKnotEntity.getOrCreateKnot(level, pos);
					}
				}

				mob.setLeashedTo(leashfenceknotentity, true);
				flag = true;
			}
		}

		cir.setReturnValue(flag ? InteractionResult.SUCCESS : InteractionResult.PASS);
	}
}
