package net.fearsredemption.fearsmod.block;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fearsredemption.fearsmod.FearsMod;
import net.fearsredemption.fearsmod.item.ModItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public class MobBlocks {

    //MAGITEK BLOCKS
    public static final Block MAGITEK_BLOCK = register("magitek_block", Block::new,
            BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final Block MAGITEK_ORE = register("magitek_ore", Block::new,
            BlockBehaviour.Properties.of().strength(4.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));

    public static final Block DEEPSLATE_MAGITEK_ORE = register("deepslate_magitek_ore", Block::new,
            BlockBehaviour.Properties.of().strength(5.5f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE));

    //VOXITE BLOCKS
    public static final Block VOXITE_BLOCK = register("voxite_block", Block::new,
            BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.METAL).lightLevel(state -> 7));

    public static final Block VOXITE_ORE = register("voxite_ore", Block::new,
            BlockBehaviour.Properties.of().strength(4.5f).requiresCorrectToolForDrops().sound(SoundType.STONE).lightLevel(state -> 5));

    public static final Block DEEPSLATE_VOXITE_ORE = register("deepslate_voxite_ore", Block::new,
            BlockBehaviour.Properties.of().strength(5.5f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).lightLevel(state -> 5));

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, name));
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        Block block = blockFactory.apply(properties.setId(blockKey));
        BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    public static void initialize() {
        FearsMod.LOGGER.info("Registering ModBlocks for " + MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.MAGITEK_KEY).register(entries -> {
            entries.accept(MAGITEK_BLOCK.asItem());
            entries.accept(MAGITEK_ORE.asItem());
            entries.accept(DEEPSLATE_MAGITEK_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.VOXITE_KEY).register(entries -> {
            entries.accept(VOXITE_BLOCK.asItem());
            entries.accept(VOXITE_ORE.asItem());
            entries.accept(DEEPSLATE_VOXITE_ORE.asItem());
        });
    }
}
