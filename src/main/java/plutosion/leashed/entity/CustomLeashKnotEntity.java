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
		this.setPosition((double)hangingPositionIn.getX() + 0.5D, (double)hangingPositionIn.getY() + 0.5D, (double)hangingPositionIn.getZ() + 0.5D);
		float f = 0.125F;
		float f1 = 0.1875F;
		float f2 = 0.25F;
		this.setBoundingBox(new AxisAlignedBB(this.getPosX() - 0.1875D, this.getPosY() - 0.25D + 0.125D, this.getPosZ() - 0.1875D, this.getPosX() + 0.1875D, this.getPosY() + 0.25D + 0.125D, this.getPosZ() + 0.1875D));
		this.forceSpawn = true;
	}

	public CustomLeashKnotEntity(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
		this(worldIn, new BlockPos(spawnEntity.getPosX(), spawnEntity.getPosY(), spawnEntity.getPosZ()));
	}

	@Override
	public EntityType<?> getType() {
		return ModRegistry.LEASH_KNOT.get();
	}

	public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
		if (this.world.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			boolean flag = false;
			double d0 = 7.0D;
			List<MobEntity> list = this.world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(this.getPosX() - 7.0D, this.getPosY() - 7.0D, this.getPosZ() - 7.0D, this.getPosX() + 7.0D, this.getPosY() + 7.0D, this.getPosZ() + 7.0D));

			for(MobEntity mobentity : list) {
				if (mobentity.getLeashHolder() == player) {
					mobentity.setLeashHolder(this, true);
					flag = true;
				}
			}

			if (!flag) {
				this.remove();
				if (player.abilities.isCreativeMode) {
					for(MobEntity mobentity1 : list) {
						if (mobentity1.getLeashed() && mobentity1.getLeashHolder() == this) {
							mobentity1.clearLeashed(true, false);
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

		for(CustomLeashKnotEntity CustomLeashKnotEntity : world.getEntitiesWithinAABB(CustomLeashKnotEntity.class, new AxisAlignedBB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D))) {
			if (CustomLeashKnotEntity.getHangingPosition().equals(pos)) {
				return CustomLeashKnotEntity;
			}
		}

		CustomLeashKnotEntity CustomLeashKnotEntity1 = new CustomLeashKnotEntity(world, pos);
		world.addEntity(CustomLeashKnotEntity1);
		CustomLeashKnotEntity1.playPlaceSound();
		return CustomLeashKnotEntity1;
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
