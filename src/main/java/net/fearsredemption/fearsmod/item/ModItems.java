package net.fearsredemption.fearsmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fearsredemption.fearsmod.FearsMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item VOXITE_INGOT = registerItem("voxite_ingot",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item VOXITE_NUGGET = registerItem("voxite_nugget",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item RAW_VOXITE = registerItem("raw_voxite",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(FearsMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        FearsMod.LOGGER.info("Registering Mod Items for" + FearsMod.MOD_ID);
    }
}
