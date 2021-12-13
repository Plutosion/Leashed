package plutosion.leashed.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import plutosion.leashed.init.ModRegistry;

import java.util.List;

public class CustomLeashKnotEntity extends LeashKnotEntity {
	public CustomLeashKnotEntity(EntityType<? extends LeashKnotEntity> entityType, World world) {
		super(entityType, world);
	}

	public CustomLeashKnotEntity(World worldIn, BlockPos hangingPositionIn) {
		super(worldIn, hangingPositionIn);
		this.setPos((double)hangingPositionIn.getX() + 0.5D, (double)hangingPositionIn.getY() + 0.5D, (double)hangingPositionIn.getZ() + 0.5D);
		float f = 0.125F;
		float f1 = 0.1875F;
		float f2 = 0.25F;
		this.setBoundingBox(new AxisAlignedBB(this.getX() - 0.1875D, this.getY() - 0.25D + 0.125D, this.getZ() - 0.1875D, this.getX() + 0.1875D, this.getY() + 0.25D + 0.125D, this.getZ() + 0.1875D));
		this.forcedLoading = true;
	}

	public CustomLeashKnotEntity(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
		this(worldIn, new BlockPos(spawnEntity.getPosX(), spawnEntity.getPosY(), spawnEntity.getPosZ()));
	}

	@Override
	public EntityType<?> getType() {
		return ModRegistry.LEASH_KNOT.get();
	}

	public ActionResultType interact(PlayerEntity player, Hand hand) {
		if (this.level.isClientSide) {
			return ActionResultType.SUCCESS;
		} else {
			boolean flag = false;
			double d0 = 7.0D;
			List<MobEntity> list = this.level.getEntitiesOfClass(MobEntity.class, new AxisAlignedBB(this.getX() - 7.0D, this.getY() - 7.0D, this.getZ() - 7.0D, this.getX() + 7.0D, this.getY() + 7.0D, this.getZ() + 7.0D));

			for(MobEntity mobentity : list) {
				if (mobentity.getLeashHolder() == player) {
					mobentity.setLeashedTo(this, true);
					flag = true;
				}
			}

			if (!flag) {
				this.remove();
				if (player.abilities.instabuild) {
					for(MobEntity mobentity1 : list) {
						if (mobentity1.isLeashed() && mobentity1.getLeashHolder() == this) {
							mobentity1.dropLeash(true, false);
						}
					}
				}
			}

			return ActionResultType.CONSUME;
		}
	}

	public static CustomLeashKnotEntity createCustomLeash(World world, BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for(CustomLeashKnotEntity CustomLeashKnotEntity : world.getEntitiesOfClass(CustomLeashKnotEntity.class, new AxisAlignedBB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D))) {
			if (CustomLeashKnotEntity.getPos().equals(pos)) {
				return CustomLeashKnotEntity;
			}
		}

		CustomLeashKnotEntity CustomLeashKnotEntity1 = new CustomLeashKnotEntity(world, pos);
		world.addFreshEntity(CustomLeashKnotEntity1);
		CustomLeashKnotEntity1.playPlacementSound();
		return CustomLeashKnotEntity1;
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
