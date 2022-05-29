package plutosion.leashed;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import plutosion.leashed.client.ClientHandler;
import plutosion.leashed.init.ModRegistry;
import plutosion.leashed.networking.PacketHandler;

/**
 * The main class of the mod, this is the class that looks like a mod to forge.
 */
@Mod(Leashed.MOD_ID)
public class Leashed {

    /**
     * The modid of this mod, this has to match the modid in the mods.toml and has to be in the format defined in {@link net.minecraftforge.fml.loading.moddiscovery.ModInfo}
     */
    public static final String MOD_ID = "leashed";

    public static final Logger LOGGER = LogUtils.getLogger();

    public Leashed() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.ITEMS.register(eventBus);
        ModRegistry.ENTITIES.register(eventBus);

        eventBus.addListener(this::setup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::registerEntityRenders);
            eventBus.addListener(ClientHandler::registerLayerDefinitions);
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
    }
}