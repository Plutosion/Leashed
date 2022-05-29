package plutosion.leashed.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

public abstract class LeashEvent extends Event {
	public static class leashMobEvent extends LeashEvent {
		private final Mob mobEntity;
		private final Entity leashOwner;

		public leashMobEvent(@Nonnull Mob mobEntity, Entity leashOwner) {
			this.mobEntity = mobEntity;
			this.leashOwner = leashOwner;
		}

		public Mob getMobEntity() {
			return mobEntity;
		}

		public Entity getLeashOwner() {
			return leashOwner;
		}
	}

	public static class unleashMobEvent extends LeashEvent {
		private final Mob mobEntity;
		private final Entity leashOwner;

		public unleashMobEvent(@Nonnull Mob mobEntity, Entity leashOwner) {
			this.mobEntity = mobEntity;
			this.leashOwner = leashOwner;
		}

		public Mob getMobEntity() {
			return mobEntity;
		}

		public Entity getLeashOwner() {
			return leashOwner;
		}
	}
}
