package plutosion.leashed.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import plutosion.leashed.item.CustomLeadItem;

import java.util.HashMap;
import java.util.Map;

public class LeadUtil {
	private static final Map<String, Item> cachedValues = new HashMap<>();

	public static Item getUsedLeash(Mob mobEntity) {
		CompoundTag persistentNBT = mobEntity.getPersistentData();
		if(persistentNBT.contains("LeadItem")) {
			String leadItemValue = persistentNBT.getString("LeadItem");
			if(cachedValues.containsKey(leadItemValue)) {
				return cachedValues.get(leadItemValue);
			} else {
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(persistentNBT.getString("LeadItem")));
				if(item != null) {
					cachedValues.put(leadItemValue, item);
					return item;
				}
			}
		}
		return Items.LEAD;
	}

	public static boolean canBeCustomleashed(Mob mobEntity, Player player, ItemStack stack) {
		if(stack.getItem() instanceof CustomLeadItem customLead) {
			return !mobEntity.isLeashed() && customLead.canLeashMob(mobEntity, player);
		} else {
			return mobEntity.canBeLeashed(player);
		}
	}

	public static void forcePlayerBack(Mob leashedEntity, Player player, double radius) {
		double d0 = (leashedEntity.getX() - player.getX()) / radius;
		double d1 = (leashedEntity.getY() - player.getY()) / radius;
		double d2 = (leashedEntity.getZ() - player.getZ()) / radius;
		player.setDeltaMovement(player.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));
	}
}
