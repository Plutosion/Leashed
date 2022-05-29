package plutosion.leashed.networking.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncLeadMessage {
	private final int entityID;
	private final String leadItem;

	public SyncLeadMessage(int entityID, String leadItem) {
		this.entityID = entityID;
		this.leadItem = leadItem;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeUtf(leadItem);
	}

	public static SyncLeadMessage decode(final FriendlyByteBuf packetBuffer) {
		return new SyncLeadMessage(packetBuffer.readInt(), packetBuffer.readUtf());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				Minecraft mc = Minecraft.getInstance();
				Entity entity = mc.level.getEntity(entityID);
				if (entity instanceof Mob mobEntity) {
					if(!leadItem.isEmpty()) {
						CompoundTag persistentData = mobEntity.getPersistentData();
						persistentData.putString("LeadItem", leadItem);
					}
				}
			}
		});
		ctx.setPacketHandled(true);
	}
}
