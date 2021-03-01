package plutosion.leashed.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.common.MinecraftForge;

public class LeashHooks {
	public static void onMobLeashed(MobEntity mob, Entity leashOwner) {
		MinecraftForge.EVENT_BUS.post(new LeashEvent.leashMobEvent(mob, leashOwner));
	}

	public static void onMobUnleashed(MobEntity mob, Entity leashOwner) {
		MinecraftForge.EVENT_BUS.post(new LeashEvent.unleashMobEvent(mob, leashOwner));
	}
}
