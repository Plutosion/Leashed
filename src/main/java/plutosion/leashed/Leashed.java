package plutosion.leashed;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plutosion.leashed.client.ClientHandler;
import plutosion.leashed.init.ModRegistry;
import plutosion.leashed.messages.MotionDeniedMessage;
import plutosion.leashed.messages.SyncLeadMessage;

import static plutosion.leashed.Leashed.MOD_ID;

/**
 * The main class of the mod, this is the class that looks like a mod to forge.
 */
@Mod(Leashed.MOD_ID)
public class Leashed {

    /**
     * The modid of this mod, this has to match the modid in the mods.toml and has to be in the format defined in {@link net.minecraftforge.fml.loading.moddiscovery.ModInfo}
     */
    public static final String MOD_ID = "leashed";

    public static final Logger LOGGER = LogManager.getLogger(Leashed.MOD_ID);

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Leashed.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public Leashed() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.ITEMS.register(eventBus);
        ModRegistry.ENTITIES.register(eventBus);

        eventBus.addListener(this::setup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::onClientSetup);
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        CHANNEL.registerMessage(0, MotionDeniedMessage.class, MotionDeniedMessage::encode, MotionDeniedMessage::decode, MotionDeniedMessage::handle);
        CHANNEL.registerMessage(1, SyncLeadMessage.class, SyncLeadMessage::encode, SyncLeadMessage::decode, SyncLeadMessage::handle);
    }
}