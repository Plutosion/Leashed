package plutosion.leashed.networking.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import plutosion.leashed.client.ClientUtil;
import plutosion.leashed.util.LeadUtil;

import java.util.function.Supplier;

public class MotionDeniedMessage {
	private final int entityID;
	private final double radius;

	public MotionDeniedMessage(int entityID, double radius) {
		this.entityID = entityID;
		this.radius = radius;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeDouble(radius);
	}

	public static MotionDeniedMessage decode(final FriendlyByteBuf packetBuffer) {
		return new MotionDeniedMessage(packetBuffer.readInt(), packetBuffer.readDouble());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
				Entity entity = mc.level.getEntity(entityID);
				if (entity instanceof Mob mobEntity) {
					Player player = ClientUtil.getClientPlayer(mc);
					LeadUtil.forcePlayerBack(mobEntity, player, radius);
				}
			}
		});
		ctx.setPacketHandled(true);
	}
}
