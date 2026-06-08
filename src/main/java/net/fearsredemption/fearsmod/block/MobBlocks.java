package net.fearsredemption.fearsmod.block;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fearsredemption.fearsmod.FearsMod;
import net.fearsredemption.fearsmod.block.custom.BuddingCrystalBlock;
import net.fearsredemption.fearsmod.item.ModItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AmethystClusterBlock;
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

    //CRYSTAL BLOCKS
    public static final Block AGATE_BLOCK = registerCrystalBlock("agate_block");
    public static final Block AGATE_CLUSTER = registerCrystalCluster("agate_cluster");
    public static final Block BUDDING_AGATE = registerBuddingCrystal("budding_agate", AGATE_CLUSTER);

    public static final Block AMBER_BLOCK = registerCrystalBlock("amber_block");
    public static final Block AMBER_CLUSTER = registerCrystalCluster("amber_cluster");
    public static final Block BUDDING_AMBER = registerBuddingCrystal("budding_amber", AMBER_CLUSTER);

    public static final Block AQUAMARINE_BLOCK = registerCrystalBlock("aquamarine_block");
    public static final Block AQUAMARINE_CLUSTER = registerCrystalCluster("aquamarine_cluster");
    public static final Block BUDDING_AQUAMARINE = registerBuddingCrystal("budding_aquamarine", AQUAMARINE_CLUSTER);

    public static final Block RUBY_BLOCK = registerCrystalBlock("ruby_block");
    public static final Block RUBY_CLUSTER = registerCrystalCluster("ruby_cluster");
    public static final Block BUDDING_RUBY = registerBuddingCrystal("budding_ruby", RUBY_CLUSTER);

    public static final Block TOPAZ_BLOCK = registerCrystalBlock("topaz_block");
    public static final Block TOPAZ_CLUSTER = registerCrystalCluster("topaz_cluster");
    public static final Block BUDDING_TOPAZ = registerBuddingCrystal("budding_topaz", TOPAZ_CLUSTER);

    private static Block registerCrystalBlock(String name) {
        return register(name, Block::new,
                BlockBehaviour.Properties.of().strength(1.5f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST));
    }

    private static Block registerCrystalCluster(String name) {
        return register(name, properties -> new AmethystClusterBlock(7.0f, 3.0f, properties),
                BlockBehaviour.Properties.of().noCollision().strength(1.5f).sound(SoundType.AMETHYST_CLUSTER).lightLevel(state -> 4));
    }

    private static Block registerBuddingCrystal(String name, Block cluster) {
        return register(name, properties -> new BuddingCrystalBlock(cluster::defaultBlockState, properties),
                BlockBehaviour.Properties.of().strength(1.5f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST).randomTicks().lightLevel(state -> 3));
    }

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

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.CRYSTALS_KEY).register(entries -> {
            entries.accept(AGATE_BLOCK.asItem());
            entries.accept(BUDDING_AGATE.asItem());
            entries.accept(AGATE_CLUSTER.asItem());
            entries.accept(AMBER_BLOCK.asItem());
            entries.accept(BUDDING_AMBER.asItem());
            entries.accept(AMBER_CLUSTER.asItem());
            entries.accept(AQUAMARINE_BLOCK.asItem());
            entries.accept(BUDDING_AQUAMARINE.asItem());
            entries.accept(AQUAMARINE_CLUSTER.asItem());
            entries.accept(RUBY_BLOCK.asItem());
            entries.accept(BUDDING_RUBY.asItem());
            entries.accept(RUBY_CLUSTER.asItem());
            entries.accept(TOPAZ_BLOCK.asItem());
            entries.accept(BUDDING_TOPAZ.asItem());
            entries.accept(TOPAZ_CLUSTER.asItem());
        });
    }
}
