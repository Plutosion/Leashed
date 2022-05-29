package plutosion.leashed.item;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;

public class CustomLeadItem extends LeadItem {
	private final float maxRange;
	public CustomLeadItem(Properties builder, float range) {
		super(builder);
		this.maxRange = range;
	}

	public float getMaxRange() {
		return maxRange;
	}

	public boolean canLeashMob(Mob entity, Player player) {
		return true;
	}
}
