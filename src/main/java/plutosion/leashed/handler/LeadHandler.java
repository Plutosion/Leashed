package plutosion.leashed.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import plutosion.leashed.Leashed;
import plutosion.leashed.events.LeashEvent;
import plutosion.leashed.item.CustomLeadItem;
import plutosion.leashed.networking.PacketHandler;
import plutosion.leashed.util.LeadUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Leashed.MOD_ID)
public class LeadHandler {
	private static final HashMap<UUID, Item> leadItemCache = new HashMap<>();

    @SubscribeEvent
    public static void onLeash(LeashEvent.leashMobEvent event) {
        if(!event.getMobEntity().level.isClientSide) {
            leadItemCache.put(event.getMobEntity().getUUID(), LeadUtil.getUsedLeash(event.getMobEntity()));
        }
    }

    @SubscribeEvent
    public static void onUnleash(LeashEvent.unleashMobEvent event) {
        if(!event.getMobEntity().level.isClientSide) {
            leadItemCache.remove(event.getMobEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onTrackEntity(StartTracking event) {
        if (event.getEntity().level.isClientSide)
            return;

        if(event.getTarget() instanceof Mob mob) {
            Player player = event.getPlayer();
            CompoundTag persistentNBT = mob.getPersistentData();
            String leadItem = persistentNBT.getString("LeadItem");
            if(leadItem.isEmpty()) {
                leadItem = "minecraft:lead";
            }
            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new plutosion.leashed.networking.messages.SyncLeadMessage(mob.getId(), leadItem));

            if(mob.isLeashed() && mob.getLeashHolder() == player) {
                leadItemCache.put(mob.getUUID(), LeadUtil.getUsedLeash(mob));
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        leadItemCache.clear();
    }

	@SubscribeEvent
    public static void leadBreaking(PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.START && event.side.isServer()) {
            Player player = event.player;
            ServerLevel world = (ServerLevel)player.level;
            if(!leadItemCache.isEmpty()) {
                List<UUID> removeList = new ArrayList<>();
                for(Map.Entry<UUID, Item> entry : leadItemCache.entrySet()) {
                    Mob mob = (Mob) world.getEntity(entry.getKey());
                    if(mob != null && mob.isLeashed() && mob.getLeashHolder() == player) {
                        Item leadItem = leadItemCache.get(mob.getUUID());
                        leadBehavior(leadItem, player, mob);
                    } else {
                        removeList.add(entry.getKey());
                    }
                }
                //Remove any mob UUID's who isn't directly leashed to a player
                removeList.forEach(leadItemCache::remove);
            }
        }
    }

    @SubscribeEvent
    public static void despawnEvent(LivingSpawnEvent.AllowDespawn event) {
        if(event.getEntityLiving() instanceof Mob mob) {
            if(mob.isLeashed() && LeadUtil.getUsedLeash(mob) instanceof CustomLeadItem) {
                event.setResult(Result.DENY);
            }
        }
    }

    public static void leadBehavior(Item lead, Player player, Mob leashedEntity) {
	    if(lead instanceof CustomLeadItem customLead) {
            double r = leashedEntity.distanceTo(player);
            double maxLength = customLead.getMaxRange();

            if(r > maxLength) {
                LeadUtil.forcePlayerBack(leashedEntity, player, r);
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new plutosion.leashed.networking.messages.MotionDeniedMessage(leashedEntity.getId(), r));
            }
        }
    }
}