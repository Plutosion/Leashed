package plutosion.leashed.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.LeadItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import plutosion.leashed.Leashed;
import plutosion.leashed.item.CustomLeadItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Leashed.MOD_ID);

    public static final RegistryObject<Item> DIAMOND_LEAD = ITEMS.register("diamond_lead" , () -> new CustomLeadItem(new Item.Properties().group(ItemGroup.MISC), 9.5F));
}