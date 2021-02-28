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

public class LeadUtil {
	public static Item getUsedLeash(MobEntity mobEntity) {
		CompoundNBT persistentNBT = mobEntity.getPersistentData();
		if(persistentNBT.contains("LeadItem")) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(persistentNBT.getString("LeadItem")));
			if(item != null) {
				return item;
			}
		}
		return Items.LEAD;
	}

	public static boolean canBeCustomleashed(MobEntity mobEntity, PlayerEntity player, ItemStack stack) {
		if(stack.getItem() instanceof CustomLeadItem) {
			CustomLeadItem customLead = (CustomLeadItem)stack.getItem();
			return customLead.canLeashMob(mobEntity, player);
		} else {
			return mobEntity.canBeLeashedTo(player);
		}
	}
}
