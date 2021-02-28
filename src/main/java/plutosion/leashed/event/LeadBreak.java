package plutosion.leashed.event;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import plutosion.leashed.Leashed;
import plutosion.leashed.item.CustomLeadItem;
import plutosion.leashed.util.LeadUtil;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Leashed.MOD_ID)
public class LeadBreak {
	private static final HashMap<UUID, Item> leadItemCache = new HashMap<>();

	@SubscribeEvent
    public static void leadBreaking(PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.START && event.side.isServer()) {
            PlayerEntity player = event.player;
			List<MobEntity> nearbyMobs = player.world.getLoadedEntitiesWithinAABB(MobEntity.class, player.getBoundingBox().grow(12F, 12F, 12F));
            for(MobEntity mob : nearbyMobs) {
                if(mob.getLeashed() && mob.getLeashHolder() == player) {
                    Item leadItem;
                    if(leadItemCache.containsKey(mob.getUniqueID())) {
                        leadItem = leadItemCache.get(mob.getUniqueID());
                    } else {
                        leadItem = LeadUtil.getUsedLeash(mob);
                        leadItemCache.put(mob.getUniqueID(), leadItem);
                    }
                    leadBehavior(leadItem, player, mob);
                }
                if(!mob.getLeashed()) {
                    leadItemCache.remove(mob.getUniqueID());
                }
            }
        }
    }

    public static void leadBehavior(Item lead, PlayerEntity player, MobEntity leashedEntity) {
	    if(lead instanceof CustomLeadItem) {
	        CustomLeadItem customLead = (CustomLeadItem)lead;
            double x = player.getPosX() - leashedEntity.getPosX();
            double y = player.getPosY() - leashedEntity.getPosY();
            double z = player.getPosZ() - leashedEntity.getPosZ();

            double r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            double phi = Math.atan2(y, x);
            double theta = Math.acos(z/r);

            double maxLength = customLead.getMaxRange();

            if(r > maxLength) {
                double x2 = leashedEntity.getPosX() + maxLength*Math.sin(theta)*Math.cos(phi);
                double y2 = leashedEntity.getPosY() + maxLength*Math.sin(theta)*Math.sin(phi);
                double z2 = leashedEntity.getPosZ() + maxLength*Math.cos(theta);

                player.setPositionAndUpdate(x2, y2, z2);
            }
        }
    }

}