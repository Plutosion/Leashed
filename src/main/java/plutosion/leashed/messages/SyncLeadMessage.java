package plutosion.leashed.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import plutosion.leashed.Leashed;

import java.util.function.Supplier;

public class SyncLeadMessage {
	private int entityID;
	private String leadItem;

	private SyncLeadMessage(PacketBuffer buf) {
		this.entityID = buf.readInt();
		this.leadItem = buf.readString();
	}

	public SyncLeadMessage(int entityID, String leadItem) {
		this.entityID = entityID;
		this.leadItem = leadItem;
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(entityID);
		buf.writeString(leadItem);
	}

	public static SyncLeadMessage decode(final PacketBuffer packetBuffer) {
		return new SyncLeadMessage(packetBuffer.readInt(), packetBuffer.readString());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				Leashed.LOGGER.info("Starting a lead sync");
				Minecraft mc = Minecraft.getInstance();
				Entity entity = mc.world.getEntityByID(entityID);
				if (entity instanceof MobEntity) {
					MobEntity mobEntity = (MobEntity)entity;
					Leashed.LOGGER.info("Found MobEntity to sync");
					if(!leadItem.isEmpty()) {
						System.out.println("hey I'm leading here");
						CompoundNBT persistentData = mobEntity.getPersistentData();
						persistentData.putString("LeadItem", leadItem);
					}
				}
			}
		});
		ctx.setPacketHandled(true);
	}
}
