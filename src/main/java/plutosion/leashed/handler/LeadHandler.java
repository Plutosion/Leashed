package plutosion.leashed.handler;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import plutosion.leashed.Leashed;
import plutosion.leashed.events.LeashEvent;
import plutosion.leashed.item.CustomLeadItem;
import plutosion.leashed.networking.PacketHandler;
import plutosion.leashed.util.LeadUtil;

import java.util.HashMap;
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
    public static void onTrackEntity(StartTracking event) {
        if (event.getEntity().world.isRemote)
            return;

        if(event.getTarget() instanceof MobEntity) {
            PlayerEntity player = event.getPlayer();
            MobEntity mob = (MobEntity)event.getTarget();
            CompoundNBT persistentNBT = mob.getPersistentData();
            String leadItem = persistentNBT.getString("LeadItem");
            if(leadItem.isEmpty()) {
                leadItem = "minecraft:lead";
            }
            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new plutosion.leashed.networking.messages.SyncLeadMessage(mob.getEntityId(), leadItem));

            if(mob.getLeashed() && mob.getLeashHolder() == player) {
                leadItemCache.put(mob.getUniqueID(), LeadUtil.getUsedLeash(mob));
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        leadItemCache.clear();
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
            double r = leashedEntity.getDistance(player);
            double maxLength = customLead.getMaxRange();

            if(r > maxLength) {
                LeadUtil.forcePlayerBack(leashedEntity, player, r);
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new plutosion.leashed.networking.messages.MotionDeniedMessage(leashedEntity.getEntityId(), r));
            }
        }
    }
}