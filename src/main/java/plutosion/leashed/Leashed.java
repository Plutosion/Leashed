package plutosion.leashed;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plutosion.leashed.init.ModItems;

import static plutosion.leashed.Leashed.MOD_ID;

/**
 * The main class of the mod, this is the class that looks like a mod to forge.
 */
@Mod(MOD_ID)
public class Leashed {

    /**
     * The modid of this mod, this has to match the modid in the mods.toml and has to be in the format defined in {@link net.minecraftforge.fml.loading.moddiscovery.ModInfo}
     */
    public static final String MOD_ID = "leashed";

    public Leashed() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(eventBus);
    }
}