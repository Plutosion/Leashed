package plutosion.leashed.mixin;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.LeadItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LeadItem.class)
public class LeadItemMixin {

	/**
	 * @author Mrbysco
	 * @reason There's no other good way to do it
	 */
	@Overwrite
	public static ActionResultType bindPlayerMobs(PlayerEntity player, World world, BlockPos pos) {
		LeashKnotEntity leashknotentity = null;
		boolean flag = false;
		double d0 = 7.0D;
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for(MobEntity mobentity : world.getEntitiesOfClass(MobEntity.class, new AxisAlignedBB((double)i - d0, (double)j - d0, (double)k - d0, (double)i + d0, (double)j + d0, (double)k + d0))) {
			if (mobentity.getLeashHolder() == player) {
				if (leashknotentity == null) {
					Item boundLeash = plutosion.leashed.util.LeadUtil.getUsedLeash(mobentity);
					if(boundLeash instanceof plutosion.leashed.item.CustomLeadItem) {
						leashknotentity = plutosion.leashed.entity.CustomLeashKnotEntity.createCustomLeash(world, pos);
					} else {
						leashknotentity = LeashKnotEntity.getOrCreateKnot(world, pos);
					}
				}

				mobentity.setLeashedTo(leashknotentity, true);
				flag = true;
			}
		}

		return flag ? ActionResultType.SUCCESS : ActionResultType.PASS;
	}
}
