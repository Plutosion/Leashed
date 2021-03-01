package plutosion.leashed.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

public abstract class LeashEvent extends Event {
	public static class leashMobEvent extends LeashEvent {
		private MobEntity mobEntity;
		private Entity leashOwner;

		public leashMobEvent(@Nonnull MobEntity mobEntity, Entity leashOwner) {
			this.mobEntity = mobEntity;
			this.leashOwner = leashOwner;
		}

		public MobEntity getMobEntity() {
			return mobEntity;
		}

		public Entity getLeashOwner() {
			return leashOwner;
		}
	}

	public static class unleashMobEvent extends LeashEvent {
		private MobEntity mobEntity;
		private Entity leashOwner;

		public unleashMobEvent(@Nonnull MobEntity mobEntity, Entity leashOwner) {
			this.mobEntity = mobEntity;
			this.leashOwner = leashOwner;
		}

		public MobEntity getMobEntity() {
			return mobEntity;
		}

		public Entity getLeashOwner() {
			return leashOwner;
		}
	}
}
