package plutosion.leashed.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;

public class LeashHooks {
	public static void onMobLeashed(Mob mob, Entity leashOwner) {
		MinecraftForge.EVENT_BUS.post(new LeashEvent.leashMobEvent(mob, leashOwner));
	}

	public static void onMobUnleashed(Mob mob, Entity leashOwner) {
		MinecraftForge.EVENT_BUS.post(new LeashEvent.unleashMobEvent(mob, leashOwner));
	}
}
