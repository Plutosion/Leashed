package plutosion.leashed.networking.messages;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import plutosion.leashed.client.ClientUtil;
import plutosion.leashed.util.LeadUtil;

import java.util.function.Supplier;

public class MotionDeniedMessage {
	private int entityID;
	private double radius;

	public MotionDeniedMessage(int entityID, double radius) {
		this.entityID = entityID;
		this.radius = radius;
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(entityID);
		buf.writeDouble(radius);
	}

	public static MotionDeniedMessage decode(final PacketBuffer packetBuffer) {
		return new MotionDeniedMessage(packetBuffer.readInt(), packetBuffer.readDouble());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
				Entity entity = mc.level.getEntity(entityID);
				if (entity instanceof MobEntity) {
					MobEntity mobEntity = (MobEntity)entity;
					PlayerEntity player = ClientUtil.getClientPlayer(mc);
					LeadUtil.forcePlayerBack(mobEntity, player, radius);
				}
			}
		});
		ctx.setPacketHandled(true);
	}
}
