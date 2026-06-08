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
    //RESONANCE ITEMS
    public static final Item RESONANT_COPPER = register("resonant_copper", Item::new, new Item.Properties());

    public static final Item STABILIZED_IRON_PLATE = register("stabilized_iron_plate", Item::new, new Item.Properties());

    public static final Item CHARGED_MAGITEK_CORE = register("charged_magitek_core", Item::new, new Item.Properties());

    public static final Item FOCUSING_LENS = register("focusing_lens", Item::new, new Item.Properties());

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

    //CRYSTAL ITEMS
    public static final Item AGATE_SHARD = register("agate_shard", Item::new, new Item.Properties());

    public static final Item AMBER_SHARD = register("amber_shard", Item::new, new Item.Properties());

    public static final Item AQUAMARINE_SHARD = register("aquamarine_shard", Item::new, new Item.Properties());

    public static final Item RUBY_SHARD = register("ruby_shard", Item::new, new Item.Properties());

    public static final Item TOPAZ_SHARD = register("topaz_shard", Item::new, new Item.Properties());

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

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.CRYSTALS_KEY).register(entries -> {
            entries.accept(AGATE_SHARD);
            entries.accept(AMBER_SHARD);
            entries.accept(AQUAMARINE_SHARD);
            entries.accept(RUBY_SHARD);
            entries.accept(TOPAZ_SHARD);
        });

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.RESONANCE_KEY).register(entries -> {
            entries.accept(RESONANT_COPPER);
            entries.accept(STABILIZED_IRON_PLATE);
            entries.accept(CHARGED_MAGITEK_CORE);
            entries.accept(FOCUSING_LENS);
        });
    }
}
