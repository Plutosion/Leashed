package plutosion.leashed.handler;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import plutosion.leashed.Leashed;
import plutosion.leashed.events.LeashEvent;
import plutosion.leashed.item.CustomLeadItem;
import plutosion.leashed.util.LeadUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Leashed.MOD_ID)
public class LeadHandler {
	private static final HashMap<UUID, Item> leadItemCache = new HashMap<>();

    @SubscribeEvent
    public static void onLeash(LeashEvent.leashMobEvent event) {
        if(!event.getMobEntity().world.isRemote) {
            leadItemCache.put(event.getMobEntity().getUniqueID(), LeadUtil.getUsedLeash(event.getMobEntity()));
        }
    }

    @SubscribeEvent
    public static void onUnleash(LeashEvent.unleashMobEvent event) {
        if(!event.getMobEntity().world.isRemote) {
            leadItemCache.remove(event.getMobEntity().getUniqueID());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if(!player.world.isRemote) {
            List<MobEntity> nearbyMobs = player.world.getLoadedEntitiesWithinAABB(MobEntity.class, player.getBoundingBox().expand(20F, 20F, 20F).expand(-20F, -20F, -20F));
            for(MobEntity mob : nearbyMobs) {
                if(mob.getLeashed()) {
                    leadItemCache.put(mob.getUniqueID(), LeadUtil.getUsedLeash(mob));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        PlayerEntity player = event.getPlayer();
        if(!player.world.isRemote) {
            List<MobEntity> nearbyMobs = player.world.getLoadedEntitiesWithinAABB(MobEntity.class, player.getBoundingBox().expand(20F, 20F, 20F).expand(-20F, -20F, -20F));
            for(MobEntity mob : nearbyMobs) {
                if(mob.getLeashed() && mob.getLeashHolder() == player) {
                    leadItemCache.remove(mob.getUniqueID());
                }
            }
        }
    }

	@SubscribeEvent
    public static void leadBreaking(PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.START && event.side.isServer()) {
            PlayerEntity player = event.player;
            ServerWorld world = (ServerWorld)player.world;
            if(!leadItemCache.isEmpty()) {
                for(Map.Entry<UUID, Item> entry : leadItemCache.entrySet()) {
                    MobEntity mob = (MobEntity) world.getEntityByUuid(entry.getKey());
                    if(mob.getLeashed() && mob.getLeashHolder() == player) {
                        Item leadItem = leadItemCache.get(mob.getUniqueID());
                        leadBehavior(leadItem, player, mob);
                    }
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

            double r = leashedEntity.getDistance(player);
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