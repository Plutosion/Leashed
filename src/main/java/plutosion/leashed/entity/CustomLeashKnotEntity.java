package plutosion.leashed.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import plutosion.leashed.init.ModRegistry;

import java.util.List;

public class CustomLeashKnotEntity extends LeashFenceKnotEntity {
	public CustomLeashKnotEntity(EntityType<? extends LeashFenceKnotEntity> entityType, Level world) {
		super(entityType, world);
	}

	public CustomLeashKnotEntity(Level worldIn, BlockPos hangingPositionIn) {
		super(worldIn, hangingPositionIn);
		this.setPos(hangingPositionIn.getX(), hangingPositionIn.getY(), hangingPositionIn.getZ());
	}

	public CustomLeashKnotEntity(SpawnEntity spawnEntity, Level worldIn) {
		this(worldIn, new BlockPos(spawnEntity.getPosX(), spawnEntity.getPosY(), spawnEntity.getPosZ()));
	}

	@Override
	public EntityType<?> getType() {
		return ModRegistry.LEASH_KNOT.get();
	}

	public InteractionResult interact(Player player, InteractionHand hand) {
		if (this.level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			boolean flag = false;
			double d0 = 7.0D;
			List<Mob> list = this.level.getEntitiesOfClass(Mob.class, new AABB(this.getX() - d0, this.getY() - d0, this.getZ() - d0,
					this.getX() + d0, this.getY() + d0, this.getZ() + d0));

			for(Mob mobentity : list) {
				if (mobentity.getLeashHolder() == player) {
					mobentity.setLeashedTo(this, true);
					flag = true;
				}
			}

			if (!flag) {
				this.discard();
				if (player.getAbilities().instabuild) {
					for(Mob mob : list) {
						if (mob.isLeashed() && mob.getLeashHolder() == this) {
							mob.dropLeash(true, false);
						}
					}
				}
			}

			return InteractionResult.CONSUME;
		}
	}

	public static CustomLeashKnotEntity getOrCreateCustomKnot(Level world, BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for(CustomLeashKnotEntity CustomLeashKnotEntity : world.getEntitiesOfClass(CustomLeashKnotEntity.class, new AABB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D))) {
			if (CustomLeashKnotEntity.getPos().equals(pos)) {
				return CustomLeashKnotEntity;
			}
		}

		CustomLeashKnotEntity customLeashKnot = new CustomLeashKnotEntity(world, pos);
		world.addFreshEntity(customLeashKnot);
		return customLeashKnot;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
