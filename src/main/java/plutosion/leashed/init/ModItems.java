package plutosion.leashed.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import plutosion.leashed.Leashed;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Leashed.MOD_ID);

    public static final RegistryObject<Item> DIAMOND_LEAD = ITEMS.register("diamond_lead" , () -> new Item(new Item.Properties().group(ItemGroup.MISC)));
}