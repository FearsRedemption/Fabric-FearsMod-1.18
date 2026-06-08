package net.fearsredemption.fearsmod.item;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public class ModItemGroup {
    public static final ResourceKey<CreativeModeTab> MAGITEK_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            Identifier.fromNamespaceAndPath(MOD_ID, "magitek")
    );
    public static final CreativeModeTab MAGITEK = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.MAGITEK_INGOT))
            .title(Component.translatable("itemGroup.fearsmod.magitek"))
            .build();

    public static final ResourceKey<CreativeModeTab> VOXITE_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            Identifier.fromNamespaceAndPath(MOD_ID, "voxite")
    );
    public static final CreativeModeTab VOXITE = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.VOXITE_INGOT))
            .title(Component.translatable("itemGroup.fearsmod.voxite"))
            .build();

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MAGITEK_KEY, MAGITEK);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, VOXITE_KEY, VOXITE);
    }
}
