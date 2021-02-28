package plutosion.leashed.item;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.LeadItem;

public class CustomLeadItem extends LeadItem {
	private final float maxRange;
	public CustomLeadItem(Properties builder, float range) {
		super(builder);
		this.maxRange = range;
	}

	public float getMaxRange() {
		return maxRange;
	}

	public boolean canLeashMob(MobEntity entity, PlayerEntity player) {
		return true;
	}
}
