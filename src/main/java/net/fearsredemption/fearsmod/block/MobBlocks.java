package net.fearsredemption.fearsmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fearsredemption.fearsmod.FearsMod;
import net.fearsredemption.fearsmod.item.ModItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public class MobBlocks {

    //MAGITEK BLOCKS
    public static final Block MAGITEK_BLOCK = registerBlock("magitek_block",
            new Block(FabricBlockSettings.of(Material.METAL).strength(6f).requiresTool()), ModItemGroup.MAGITEK);

    public static final Block MAGITEK_ORE = registerBlock("magitek_ore",
            new Block(FabricBlockSettings.of(Material.STONE).strength(4.5f).requiresTool()), ModItemGroup.MAGITEK);

    //VOXITE BLOCKS
    public static final Block VOXITE_BLOCK = registerBlock("voxite_block",
            new Block(FabricBlockSettings.of(Material.METAL).strength(6f).requiresTool().luminance(7)), ModItemGroup.VOXITE);

    public static final Block VOXITE_ORE = registerBlock("voxite_ore",
            new Block(FabricBlockSettings.of(Material.STONE).strength(4.5f).requiresTool().luminance(5)), ModItemGroup.VOXITE);

    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name),
            new BlockItem(block, new FabricItemSettings().group(group)));
    }

    public static void registerModBlocks() {
        FearsMod.LOGGER.info("Registering ModBlocks for " + MOD_ID);
    }
}
