package plutosion.leashed.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientUtil {
	public static Player getClientPlayer(Minecraft mc) {
		return mc.player;
	}
}
