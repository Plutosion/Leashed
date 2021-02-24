package plutosion.leashed;

import static plutosion.leashed.Leashed.MOD_ID;

import net.minecraftforge.fml.common.Mod;
import plutosion.leashed.event.LeadBreak;

/**
 * The main class of the mod, this is the class that looks like a mod to forge.
 */
@Mod(MOD_ID)
public class Leashed {

    /**
     * The modid of this mod, this has to match the modid in the mods.toml and has to be in the format defined in {@link net.minecraftforge.fml.loading.moddiscovery.ModInfo}
     */
    public static final String MOD_ID = "leashed";

    LeadBreak leadBreak = new LeadBreak();
}