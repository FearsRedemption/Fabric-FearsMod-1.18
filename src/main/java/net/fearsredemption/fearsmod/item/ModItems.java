package net.fearsredemption.fearsmod.item;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fearsredemption.fearsmod.FearsMod;
import net.fearsredemption.fearsmod.item.custom.DowsingRodItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public class ModItems {
    //MEGITEK ITEMS
    public static final Item MAGITEK_INGOT = register("magitek_ingot", Item::new, new Item.Properties());

    public static final Item MAGITEK_NUGGET = register("magitek_nugget", Item::new, new Item.Properties());

    public static final Item RAW_MAGITEK = register("raw_magitek", Item::new, new Item.Properties());

    //VOXITE ITEMS
    public static final Item VOXITE_INGOT = register("voxite_ingot", Item::new, new Item.Properties());

    public static final Item VOXITE_NUGGET = register("voxite_nugget", Item::new, new Item.Properties());

    public static final Item RAW_VOXITE = register("raw_voxite", Item::new, new Item.Properties());

    //DOWSING ROD
    public static final Item DOWSING_ROD = register("dowsing_rod", DowsingRodItem::new, new Item.Properties().durability(16));

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties properties) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        T item = itemFactory.apply(properties.setId(itemKey));
        return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }

    public static void initialize() {
        FearsMod.LOGGER.info("Registering Mod Items for " + MOD_ID);
        ModItemGroup.initialize();

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.MAGITEK_KEY).register(entries -> {
            entries.accept(MAGITEK_INGOT);
            entries.accept(MAGITEK_NUGGET);
            entries.accept(RAW_MAGITEK);
        });

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.VOXITE_KEY).register(entries -> {
            entries.accept(VOXITE_INGOT);
            entries.accept(VOXITE_NUGGET);
            entries.accept(RAW_VOXITE);
            entries.accept(DOWSING_ROD);
        });
    }
}
