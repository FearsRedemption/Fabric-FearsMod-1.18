package net.fearsredemption.fearsmod.item;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fearsredemption.fearsmod.FearsMod;
import net.fearsredemption.fearsmod.item.custom.ResonanceJournalItem;
import net.fearsredemption.fearsmod.item.custom.ResonanceStaffItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public class ModItems {
    //RESONANCE ITEMS
    public static final Item RESONANT_COPPER = register("resonant_copper", Item::new, new Item.Properties());

    public static final Item STABILIZED_IRON_PLATE = register("stabilized_iron_plate", Item::new, new Item.Properties());

    public static final Item CHARGED_MAGITEK_CORE = register("charged_magitek_core", Item::new, new Item.Properties());

    public static final Item FOCUSING_LENS = register("focusing_lens", Item::new, new Item.Properties());

    public static final Item RESONANCE_JOURNAL = register("resonance_journal", ResonanceJournalItem::new, new Item.Properties().stacksTo(1));

    //MEGITEK ITEMS
    public static final Item MAGITEK_INGOT = register("magitek_ingot", Item::new, new Item.Properties());

    public static final Item MAGITEK_NUGGET = register("magitek_nugget", Item::new, new Item.Properties());

    public static final Item RAW_MAGITEK = register("raw_magitek", Item::new, new Item.Properties());

    //VOXITE ITEMS
    public static final Item VOXITE_INGOT = register("voxite_ingot", Item::new, new Item.Properties());

    public static final Item VOXITE_NUGGET = register("voxite_nugget", Item::new, new Item.Properties());

    public static final Item RAW_VOXITE = register("raw_voxite", Item::new, new Item.Properties());

    //RESONANCE STAFFS
    public static final Item RESONANCE_STAFF = register("resonance_staff", ResonanceStaffItem::new, new Item.Properties().durability(64));

    public static final Item AGATE_RESONANCE_STAFF = register("agate_resonance_staff", ResonanceStaffItem::new, new Item.Properties().durability(64));

    public static final Item AMBER_RESONANCE_STAFF = register("amber_resonance_staff", ResonanceStaffItem::new, new Item.Properties().durability(64));

    public static final Item AQUAMARINE_RESONANCE_STAFF = register("aquamarine_resonance_staff", ResonanceStaffItem::new, new Item.Properties().durability(64));

    public static final Item RUBY_RESONANCE_STAFF = register("ruby_resonance_staff", ResonanceStaffItem::new, new Item.Properties().durability(64));

    public static final Item TOPAZ_RESONANCE_STAFF = register("topaz_resonance_staff", ResonanceStaffItem::new, new Item.Properties().durability(64));

    // Deprecated migration holdover. Hidden from creative tabs and no longer used as the staff.
    public static final Item DOWSING_ROD = register("dowsing_rod", Item::new, new Item.Properties());

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

    public static boolean isResonanceStaff(ItemStack stack) {
        Item item = stack.getItem();
        return item == RESONANCE_STAFF
                || item == AGATE_RESONANCE_STAFF
                || item == AMBER_RESONANCE_STAFF
                || item == AQUAMARINE_RESONANCE_STAFF
                || item == RUBY_RESONANCE_STAFF
                || item == TOPAZ_RESONANCE_STAFF;
    }

    public static Item staffForShard(Item shard) {
        if (shard == Items.AMETHYST_SHARD) {
            return RESONANCE_STAFF;
        }
        if (shard == AGATE_SHARD) {
            return AGATE_RESONANCE_STAFF;
        }
        if (shard == AMBER_SHARD) {
            return AMBER_RESONANCE_STAFF;
        }
        if (shard == AQUAMARINE_SHARD) {
            return AQUAMARINE_RESONANCE_STAFF;
        }
        if (shard == RUBY_SHARD) {
            return RUBY_RESONANCE_STAFF;
        }
        if (shard == TOPAZ_SHARD) {
            return TOPAZ_RESONANCE_STAFF;
        }

        return null;
    }

    public static Item shardForStaff(Item staff) {
        if (staff == RESONANCE_STAFF) {
            return Items.AMETHYST_SHARD;
        }
        if (staff == AGATE_RESONANCE_STAFF) {
            return AGATE_SHARD;
        }
        if (staff == AMBER_RESONANCE_STAFF) {
            return AMBER_SHARD;
        }
        if (staff == AQUAMARINE_RESONANCE_STAFF) {
            return AQUAMARINE_SHARD;
        }
        if (staff == RUBY_RESONANCE_STAFF) {
            return RUBY_SHARD;
        }
        if (staff == TOPAZ_RESONANCE_STAFF) {
            return TOPAZ_SHARD;
        }

        return null;
    }

    public static int staffVariantIndex(Item staff) {
        if (staff == RESONANCE_STAFF) {
            return 0;
        }
        if (staff == AGATE_RESONANCE_STAFF) {
            return 1;
        }
        if (staff == AMBER_RESONANCE_STAFF) {
            return 2;
        }
        if (staff == AQUAMARINE_RESONANCE_STAFF) {
            return 3;
        }
        if (staff == RUBY_RESONANCE_STAFF) {
            return 4;
        }
        if (staff == TOPAZ_RESONANCE_STAFF) {
            return 5;
        }

        return -1;
    }

    public static Item staffByVariantIndex(int index) {
        return switch (index) {
            case 0 -> RESONANCE_STAFF;
            case 1 -> AGATE_RESONANCE_STAFF;
            case 2 -> AMBER_RESONANCE_STAFF;
            case 3 -> AQUAMARINE_RESONANCE_STAFF;
            case 4 -> RUBY_RESONANCE_STAFF;
            case 5 -> TOPAZ_RESONANCE_STAFF;
            default -> RESONANCE_STAFF;
        };
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
        });

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.CRYSTALS_KEY).register(entries -> {
            entries.accept(AGATE_SHARD);
            entries.accept(AMBER_SHARD);
            entries.accept(AQUAMARINE_SHARD);
            entries.accept(RUBY_SHARD);
            entries.accept(TOPAZ_SHARD);
        });

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.RESONANCE_KEY).register(entries -> {
            entries.accept(RESONANCE_JOURNAL);
            entries.accept(RESONANCE_STAFF);
            entries.accept(AGATE_RESONANCE_STAFF);
            entries.accept(AMBER_RESONANCE_STAFF);
            entries.accept(AQUAMARINE_RESONANCE_STAFF);
            entries.accept(RUBY_RESONANCE_STAFF);
            entries.accept(TOPAZ_RESONANCE_STAFF);
            entries.accept(RESONANT_COPPER);
            entries.accept(STABILIZED_IRON_PLATE);
            entries.accept(CHARGED_MAGITEK_CORE);
            entries.accept(FOCUSING_LENS);
        });
    }
}
