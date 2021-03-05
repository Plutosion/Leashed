package plutosion.leashed.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import plutosion.leashed.item.CustomLeadItem;

import java.util.HashMap;
import java.util.Map;

public class LeadUtil {
	private static final Map<String, Item> cachedValues = new HashMap<>();

	public static Item getUsedLeash(MobEntity mobEntity) {
		CompoundNBT persistentNBT = mobEntity.getPersistentData();
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

	public static boolean canBeCustomleashed(MobEntity mobEntity, PlayerEntity player, ItemStack stack) {
		if(stack.getItem() instanceof CustomLeadItem) {
			CustomLeadItem customLead = (CustomLeadItem)stack.getItem();
			return !mobEntity.getLeashed() && customLead.canLeashMob(mobEntity, player);
		} else {
			return mobEntity.canBeLeashedTo(player);
		}
	}

	public static void forcePlayerBack(MobEntity leashedEntity, PlayerEntity player, double radius) {
		double d0 = (leashedEntity.getPosX() - player.getPosX()) / (double)radius;
		double d1 = (leashedEntity.getPosY() - player.getPosY()) / (double)radius;
		double d2 = (leashedEntity.getPosZ() - player.getPosZ()) / (double)radius;
		player.setMotion(player.getMotion().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));
	}
}
