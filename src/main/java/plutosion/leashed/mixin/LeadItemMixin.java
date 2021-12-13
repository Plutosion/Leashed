package plutosion.leashed.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LeadItem.class)
public class LeadItemMixin {

	/**
	 * @author Mrbysco
	 * @reason There's no other good way to do it
	 */
	@Overwrite
	public static InteractionResult bindPlayerMobs(Player player, Level world, BlockPos pos) {
		LeashFenceKnotEntity leashknotentity = null;
		boolean flag = false;
		double d0 = 7.0D;
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for(Mob mobentity : world.getEntitiesOfClass(Mob.class, new AABB((double)i - d0, (double)j - d0, (double)k - d0, (double)i + d0, (double)j + d0, (double)k + d0))) {
			if (mobentity.getLeashHolder() == player) {
				if (leashknotentity == null) {
					Item boundLeash = plutosion.leashed.util.LeadUtil.getUsedLeash(mobentity);
					if(boundLeash instanceof plutosion.leashed.item.CustomLeadItem) {
						leashknotentity = plutosion.leashed.entity.CustomLeashKnotEntity.getOrCreateCustomKnot(world, pos);
						leashknotentity.playPlacementSound();
					} else {
						leashknotentity = LeashFenceKnotEntity.getOrCreateKnot(world, pos);
					}
				}

				mobentity.setLeashedTo(leashknotentity, true);
				flag = true;
			}
		}

		return flag ? InteractionResult.SUCCESS : InteractionResult.PASS;
	}
}
