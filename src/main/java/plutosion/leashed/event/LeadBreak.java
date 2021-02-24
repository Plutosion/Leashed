package plutosion.leashed.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import plutosion.leashed.Leashed;
import plutosion.leashed.init.ModItems;

@Mod.EventBusSubscriber(modid = Leashed.MOD_ID)
public class LeadBreak {

    public LeadBreak() {}

    private static MobEntity leashedEntity;

    @SubscribeEvent
    public static void LeadBreaking(PlayerTickEvent event) {
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

                event.player.setPosition(x2, y2, z2);
            }
        }
    }

    private static boolean holdingAir = true;

    private static void DropLead(MobEntity mobEntity, World world) {
        mobEntity.clearLeashed(true, false);
        leashedEntity = null;
        Entity diamondLead = new ItemEntity(world, mobEntity.getPosX(), mobEntity.getPosY(), mobEntity.getPosZ(), new ItemStack(ModItems.diamond_lead));
        world.addEntity(diamondLead);
    }

    @SubscribeEvent
    public static void RightClickLead(EntityInteract event) {

        Entity entity = event.getTarget();

        if (entity instanceof MobEntity) {

            ItemStack itemStack = event.getItemStack();
            World world = event.getWorld();
            MobEntity mobEntity = (MobEntity) entity;

            if(mobEntity == leashedEntity && !(itemStack.getItem() == ModItems.diamond_lead || (itemStack.getItem() == Items.AIR && !holdingAir))) {

                event.setCanceled(true);
                DropLead(mobEntity, world);

            }

            if (itemStack.getItem() == ModItems.diamond_lead) {

                event.setCanceled(true);

                PlayerEntity player = event.getPlayer();

                if (mobEntity.getLeashHolder() != player) {

                    mobEntity.setLeashHolder(player, true);
                    itemStack.shrink(1);
                    holdingAir = false;
                    leashedEntity = mobEntity;

                } else {
                    DropLead(mobEntity, world);
                }

            } else if (itemStack.getItem() == Items.AIR && !holdingAir) {
                holdingAir = true;
                event.setCanceled(true);
            }
        }
    }

}