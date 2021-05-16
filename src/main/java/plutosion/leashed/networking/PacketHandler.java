package plutosion.leashed.networking;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import plutosion.leashed.Leashed;
import plutosion.leashed.networking.messages.MotionDeniedMessage;
import plutosion.leashed.networking.messages.SyncLeadMessage;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Leashed.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	public static void init() {
		CHANNEL.registerMessage(0, MotionDeniedMessage.class, MotionDeniedMessage::encode, MotionDeniedMessage::decode, MotionDeniedMessage::handle);
		CHANNEL.registerMessage(1, SyncLeadMessage.class, SyncLeadMessage::encode, SyncLeadMessage::decode, SyncLeadMessage::handle);
	}
}
