package plutosion.leashed.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class ClientUtil {
	public static PlayerEntity getClientPlayer(Minecraft mc) {
		return mc.player;
	}
}
