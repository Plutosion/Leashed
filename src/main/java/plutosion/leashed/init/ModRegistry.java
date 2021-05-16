package plutosion.leashed.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import plutosion.leashed.Leashed;
import plutosion.leashed.entity.CustomLeashKnotEntity;
import plutosion.leashed.item.CustomLeadItem;

public class ModRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Leashed.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Leashed.MOD_ID);

    public static final RegistryObject<Item> DIAMOND_LEAD = ITEMS.register("diamond_lead" , () -> new CustomLeadItem(new Item.Properties().group(ItemGroup.MISC), 9.5F));

    public static final RegistryObject<EntityType<CustomLeashKnotEntity>> LEASH_KNOT = ENTITIES.register("leash_knot", () ->
            register("leash_knot", EntityType.Builder.<CustomLeashKnotEntity>create(CustomLeashKnotEntity::new, EntityClassification.MISC)
                    .size(0.5F, 0.5F)
                    .trackingRange(10).updateInterval(Integer.MAX_VALUE)
                    .setCustomClientFactory(CustomLeashKnotEntity::new)));

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return builder.build(id);
    }
}