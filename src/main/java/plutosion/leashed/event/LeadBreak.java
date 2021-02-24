package plutosion.leashed.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import plutosion.leashed.Leashed;
import plutosion.leashed.init.ModItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Leashed.MOD_ID)
public class LeadBreak {
    private static final HashMap<UUID, List<UUID>> leashedMap = new HashMap<>();

    @SubscribeEvent
    public static void leadBreaking(PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.START && event.side.isServer()) {
            PlayerEntity player = event.player;
            if(!leashedMap.isEmpty() && leashedMap.containsKey(player.getUniqueID())) {
                ServerWorld world = (ServerWorld)event.player.world;
                List<UUID> leashedUUIDs = leashedMap.get(player.getUniqueID());
                for(UUID leashedUUID : leashedUUIDs) {
                    MobEntity leashedEntity = (MobEntity)world.getEntityByUuid(leashedUUID);
                    if(leashedEntity != null && leashedEntity.isAlive() && leashedEntity.getLeashed()) {
                        double x = event.player.getPosX() - leashedEntity.getPosX();
                        double y = event.player.getPosY() - leashedEntity.getPosY();
                        double z = event.player.getPosZ() - leashedEntity.getPosZ();

                        double r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                        double phi = Math.atan2(y, x);
                        double theta = Math.acos(z/r);

                        double maxLength = 9.5F;

                        if(r > maxLength) {
                            double x2 = leashedEntity.getPosX() + maxLength*Math.sin(theta)*Math.cos(phi);
                            double y2 = leashedEntity.getPosY() + maxLength*Math.sin(theta)*Math.sin(phi);
                            double z2 = leashedEntity.getPosZ() + maxLength*Math.cos(theta);

                            event.player.setPositionAndUpdate(x2, y2, z2);
                        }
                    }
                }
            }
        }
    }

    private static void dropLead(MobEntity mobEntity, World world) {
        mobEntity.clearLeashed(true, false);
        for(Map.Entry<UUID, List<UUID>> entry : leashedMap.entrySet()) {
            List<UUID> leashedUUIDS = entry.getValue();
            if(leashedUUIDS.remove(mobEntity.getUniqueID())) {
                entry.setValue(leashedUUIDS);
            }
        }
        Entity diamondLead = new ItemEntity(world, mobEntity.getPosX(), mobEntity.getPosY(), mobEntity.getPosZ(), new ItemStack(ModItems.DIAMOND_LEAD.get()));
        world.addEntity(diamondLead);
    }

    private static boolean isMobLeashed(MobEntity mobEntity) {
        for(Map.Entry<UUID, List<UUID>> entry : leashedMap.entrySet()) {
            if(entry.getValue().contains(mobEntity.getUniqueID())) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void rightClickLead(PlayerInteractEvent.EntityInteract event) {
        PlayerEntity player = event.getPlayer();
        Entity entity = event.getTarget();

        if (entity instanceof MobEntity) {
            ItemStack itemStack = event.getItemStack();
            World world = event.getWorld();
            MobEntity mobEntity = (MobEntity) entity;

            if(isMobLeashed(mobEntity) && !(itemStack.getItem() == ModItems.DIAMOND_LEAD.get() || itemStack.isEmpty())) {
                event.setCanceled(true);
                dropLead(mobEntity, world);
            }

            if (itemStack.getItem() == ModItems.DIAMOND_LEAD.get()) {
                event.setCanceled(true);

                if (mobEntity.getLeashHolder() != player) {
                    mobEntity.setLeashHolder(player, true);
                    itemStack.shrink(1);
                    if(leashedMap.containsKey(player.getUniqueID())) {
                        List<UUID> leashedUUIDS = leashedMap.get(player.getUniqueID());
                        leashedUUIDS.add(mobEntity.getUniqueID());
                        leashedMap.put(player.getUniqueID(), leashedUUIDS);
                    } else {
                        List<UUID> leashedUUIDS = new ArrayList<>();
                        leashedUUIDS.add(mobEntity.getUniqueID());
                        leashedMap.put(player.getUniqueID(), leashedUUIDS);
                    }
                } else {
                    dropLead(mobEntity, world);
                }
            } else if (itemStack.isEmpty()) {
                event.setCanceled(true);
            }
        }
    }

}