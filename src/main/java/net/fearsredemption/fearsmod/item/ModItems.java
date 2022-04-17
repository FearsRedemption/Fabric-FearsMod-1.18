package net.fearsredemption.fearsmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fearsredemption.fearsmod.FearsMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public class ModItems {
    //MEGITEK ITEMS
    public static final Item MAGITEK_INGOT = registerItem("magitek_ingot",
            new Item(new FabricItemSettings().group(ModItemGroup.MAGITEK)));

    public static final Item MAGITEK_NUGGET = registerItem("magitek_nugget",
            new Item(new FabricItemSettings().group(ModItemGroup.MAGITEK)));

    public static final Item RAW_MAGITEK = registerItem("raw_magitek",
            new Item(new FabricItemSettings().group(ModItemGroup.MAGITEK)));

    //VOXITE ITEMS
    public static final Item VOXITE_INGOT = registerItem("voxite_ingot",
            new Item(new FabricItemSettings().group(ModItemGroup.VOXITE)));

    public static final Item VOXITE_NUGGET = registerItem("voxite_nugget",
            new Item(new FabricItemSettings().group(ModItemGroup.VOXITE)));

    public static final Item RAW_VOXITE = registerItem("raw_voxite",
            new Item(new FabricItemSettings().group(ModItemGroup.VOXITE)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
    }

    public static void registerModItems() {
        FearsMod.LOGGER.info("Registering Mod Items for" + MOD_ID);
    }
}
