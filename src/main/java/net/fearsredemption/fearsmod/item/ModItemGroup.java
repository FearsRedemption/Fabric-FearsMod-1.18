package net.fearsredemption.fearsmod.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public class ModItemGroup {
    public static final ItemGroup MAGITEK = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "magitek"),
            () -> new ItemStack(ModItems.MAGITEK_INGOT));

    public static final ItemGroup VOXITE = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "voxite"),
            () -> new ItemStack(ModItems.VOXITE_INGOT));
}
