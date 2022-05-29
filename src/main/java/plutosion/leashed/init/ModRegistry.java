package plutosion.leashed.init;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import plutosion.leashed.Leashed;
import plutosion.leashed.entity.CustomLeashKnotEntity;
import plutosion.leashed.item.CustomLeadItem;

public class ModRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Leashed.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Leashed.MOD_ID);

    public static final RegistryObject<Item> DIAMOND_LEAD = ITEMS.register("diamond_lead" , () -> new CustomLeadItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC), 9.5F));

    public static final net.minecraftforge.registries.RegistryObject<EntityType<CustomLeashKnotEntity>> LEASH_KNOT = ENTITIES.register("leash_knot", () ->
            register("leash_knot", EntityType.Builder.<CustomLeashKnotEntity>of(CustomLeashKnotEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
                    .setCustomClientFactory(CustomLeashKnotEntity::new)));

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return builder.build(id);
    }
}