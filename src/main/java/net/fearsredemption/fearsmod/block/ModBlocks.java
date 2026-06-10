package net.fearsredemption.fearsmod.block;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fearsredemption.fearsmod.FearsMod;
import net.fearsredemption.fearsmod.block.custom.BuddingCrystalBlock;
import net.fearsredemption.fearsmod.block.custom.ResonanceSmelterBlock;
import net.fearsredemption.fearsmod.block.custom.ResonanceSmelterPartBlock;
import net.fearsredemption.fearsmod.block.custom.ResonanceSocketBlock;
import net.fearsredemption.fearsmod.block.custom.ResonanceSocketType;
import net.fearsredemption.fearsmod.block.custom.ResonanceWorkbenchBlock;
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

public class ModBlocks {
    //RESONANCE APPARATUS
    public static final Block RESONANCE_WORKBENCH = register("resonance_workbench", ResonanceWorkbenchBlock::new,
            BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops().sound(SoundType.COPPER));

    public static final Block MAGITEK_CORE = register("magitek_core", properties -> new ResonanceSocketBlock(ResonanceSocketType.CORE, properties),
            BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).lightLevel(state -> 4));

    public static final Block VOXITE_STABILIZER = register("voxite_stabilizer", properties -> new ResonanceSocketBlock(ResonanceSocketType.STABILIZER, properties),
            BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).lightLevel(state -> 2));

    public static final Block AMETHYST_FOCUS = register("amethyst_focus", properties -> new ResonanceSocketBlock(ResonanceSocketType.FOCUS, properties),
            BlockBehaviour.Properties.of().strength(1.5f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST).lightLevel(state -> 5));

    public static final Block RESONANCE_SMELTER = register("resonance_smelter", ResonanceSmelterBlock::new,
            BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).lightLevel(state -> 8));

    public static final Block RESONANCE_SMELTER_PART = register("resonance_smelter_part", ResonanceSmelterPartBlock::new,
            BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).lightLevel(state -> 5));

    //MAGITEK BLOCKS
    public static final Block MAGITEK_BLOCK = register("magitek_block", Block::new,
            BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final Block MAGITEK_STONE = register("magitek_stone", Block::new,
            BlockBehaviour.Properties.of().strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE).lightLevel(state -> 2));

    public static final Block MAGITEK_ORE = register("magitek_ore", Block::new,
            BlockBehaviour.Properties.of().strength(4.5f).requiresCorrectToolForDrops().sound(SoundType.STONE));

    public static final Block DEEPSLATE_MAGITEK_ORE = register("deepslate_magitek_ore", Block::new,
            BlockBehaviour.Properties.of().strength(5.5f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE));

    //VOXITE BLOCKS
    public static final Block VOXITE_BLOCK = register("voxite_block", Block::new,
            BlockBehaviour.Properties.of().strength(6f).requiresCorrectToolForDrops().sound(SoundType.METAL).lightLevel(state -> 7));

    public static final Block VOXITE_STONE = register("voxite_stone", Block::new,
            BlockBehaviour.Properties.of().strength(3.5f).requiresCorrectToolForDrops().sound(SoundType.STONE).lightLevel(state -> 2));

    public static final Block VOXITE_ORE = register("voxite_ore", Block::new,
            BlockBehaviour.Properties.of().strength(4.5f).requiresCorrectToolForDrops().sound(SoundType.STONE).lightLevel(state -> 5));

    public static final Block DEEPSLATE_VOXITE_ORE = register("deepslate_voxite_ore", Block::new,
            BlockBehaviour.Properties.of().strength(5.5f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).lightLevel(state -> 5));

    //CRYSTAL BLOCKS
    public static final Block AGATE_BLOCK = registerCrystalBlock("agate_block");
    public static final Block SMALL_AGATE_BUD = registerCrystalBud("small_agate_bud", 3.0f, 4.0f, SoundType.SMALL_AMETHYST_BUD, 1);
    public static final Block MEDIUM_AGATE_BUD = registerCrystalBud("medium_agate_bud", 4.0f, 3.0f, SoundType.MEDIUM_AMETHYST_BUD, 2);
    public static final Block LARGE_AGATE_BUD = registerCrystalBud("large_agate_bud", 5.0f, 3.0f, SoundType.LARGE_AMETHYST_BUD, 3);
    public static final Block AGATE_CLUSTER = registerCrystalCluster("agate_cluster");
    public static final Block BUDDING_AGATE = registerBuddingCrystal("budding_agate", SMALL_AGATE_BUD, MEDIUM_AGATE_BUD, LARGE_AGATE_BUD, AGATE_CLUSTER);

    public static final Block AMBER_BLOCK = registerCrystalBlock("amber_block");
    public static final Block SMALL_AMBER_BUD = registerCrystalBud("small_amber_bud", 3.0f, 4.0f, SoundType.SMALL_AMETHYST_BUD, 1);
    public static final Block MEDIUM_AMBER_BUD = registerCrystalBud("medium_amber_bud", 4.0f, 3.0f, SoundType.MEDIUM_AMETHYST_BUD, 2);
    public static final Block LARGE_AMBER_BUD = registerCrystalBud("large_amber_bud", 5.0f, 3.0f, SoundType.LARGE_AMETHYST_BUD, 3);
    public static final Block AMBER_CLUSTER = registerCrystalCluster("amber_cluster");
    public static final Block BUDDING_AMBER = registerBuddingCrystal("budding_amber", SMALL_AMBER_BUD, MEDIUM_AMBER_BUD, LARGE_AMBER_BUD, AMBER_CLUSTER);

    public static final Block AQUAMARINE_BLOCK = registerCrystalBlock("aquamarine_block");
    public static final Block SMALL_AQUAMARINE_BUD = registerCrystalBud("small_aquamarine_bud", 3.0f, 4.0f, SoundType.SMALL_AMETHYST_BUD, 1);
    public static final Block MEDIUM_AQUAMARINE_BUD = registerCrystalBud("medium_aquamarine_bud", 4.0f, 3.0f, SoundType.MEDIUM_AMETHYST_BUD, 2);
    public static final Block LARGE_AQUAMARINE_BUD = registerCrystalBud("large_aquamarine_bud", 5.0f, 3.0f, SoundType.LARGE_AMETHYST_BUD, 3);
    public static final Block AQUAMARINE_CLUSTER = registerCrystalCluster("aquamarine_cluster");
    public static final Block BUDDING_AQUAMARINE = registerBuddingCrystal("budding_aquamarine", SMALL_AQUAMARINE_BUD, MEDIUM_AQUAMARINE_BUD, LARGE_AQUAMARINE_BUD, AQUAMARINE_CLUSTER);

    public static final Block RUBY_BLOCK = registerCrystalBlock("ruby_block");
    public static final Block SMALL_RUBY_BUD = registerCrystalBud("small_ruby_bud", 3.0f, 4.0f, SoundType.SMALL_AMETHYST_BUD, 1);
    public static final Block MEDIUM_RUBY_BUD = registerCrystalBud("medium_ruby_bud", 4.0f, 3.0f, SoundType.MEDIUM_AMETHYST_BUD, 2);
    public static final Block LARGE_RUBY_BUD = registerCrystalBud("large_ruby_bud", 5.0f, 3.0f, SoundType.LARGE_AMETHYST_BUD, 3);
    public static final Block RUBY_CLUSTER = registerCrystalCluster("ruby_cluster");
    public static final Block BUDDING_RUBY = registerBuddingCrystal("budding_ruby", SMALL_RUBY_BUD, MEDIUM_RUBY_BUD, LARGE_RUBY_BUD, RUBY_CLUSTER);

    public static final Block TOPAZ_BLOCK = registerCrystalBlock("topaz_block");
    public static final Block SMALL_TOPAZ_BUD = registerCrystalBud("small_topaz_bud", 3.0f, 4.0f, SoundType.SMALL_AMETHYST_BUD, 1);
    public static final Block MEDIUM_TOPAZ_BUD = registerCrystalBud("medium_topaz_bud", 4.0f, 3.0f, SoundType.MEDIUM_AMETHYST_BUD, 2);
    public static final Block LARGE_TOPAZ_BUD = registerCrystalBud("large_topaz_bud", 5.0f, 3.0f, SoundType.LARGE_AMETHYST_BUD, 3);
    public static final Block TOPAZ_CLUSTER = registerCrystalCluster("topaz_cluster");
    public static final Block BUDDING_TOPAZ = registerBuddingCrystal("budding_topaz", SMALL_TOPAZ_BUD, MEDIUM_TOPAZ_BUD, LARGE_TOPAZ_BUD, TOPAZ_CLUSTER);

    private static Block registerCrystalBlock(String name) {
        return register(name, Block::new,
                BlockBehaviour.Properties.of().strength(1.5f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST));
    }

    private static Block registerCrystalCluster(String name) {
        return register(name, properties -> new AmethystClusterBlock(7.0f, 3.0f, properties),
                BlockBehaviour.Properties.of().noCollision().strength(1.5f).sound(SoundType.AMETHYST_CLUSTER).lightLevel(state -> 5));
    }

    private static Block registerCrystalBud(String name, float height, float xzOffset, SoundType sound, int lightLevel) {
        return register(name, properties -> new AmethystClusterBlock(height, xzOffset, properties),
                BlockBehaviour.Properties.of().noCollision().strength(1.5f).sound(sound).lightLevel(state -> lightLevel));
    }

    private static Block registerBuddingCrystal(String name, Block smallBud, Block mediumBud, Block largeBud, Block cluster) {
        return register(name, properties -> new BuddingCrystalBlock(smallBud, mediumBud, largeBud, cluster, properties),
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
            entries.accept(MAGITEK_STONE.asItem());
            entries.accept(MAGITEK_ORE.asItem());
            entries.accept(DEEPSLATE_MAGITEK_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.VOXITE_KEY).register(entries -> {
            entries.accept(VOXITE_BLOCK.asItem());
            entries.accept(VOXITE_STONE.asItem());
            entries.accept(VOXITE_ORE.asItem());
            entries.accept(DEEPSLATE_VOXITE_ORE.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.CRYSTALS_KEY).register(entries -> {
            entries.accept(AGATE_BLOCK.asItem());
            entries.accept(BUDDING_AGATE.asItem());
            entries.accept(SMALL_AGATE_BUD.asItem());
            entries.accept(MEDIUM_AGATE_BUD.asItem());
            entries.accept(LARGE_AGATE_BUD.asItem());
            entries.accept(AGATE_CLUSTER.asItem());
            entries.accept(AMBER_BLOCK.asItem());
            entries.accept(BUDDING_AMBER.asItem());
            entries.accept(SMALL_AMBER_BUD.asItem());
            entries.accept(MEDIUM_AMBER_BUD.asItem());
            entries.accept(LARGE_AMBER_BUD.asItem());
            entries.accept(AMBER_CLUSTER.asItem());
            entries.accept(AQUAMARINE_BLOCK.asItem());
            entries.accept(BUDDING_AQUAMARINE.asItem());
            entries.accept(SMALL_AQUAMARINE_BUD.asItem());
            entries.accept(MEDIUM_AQUAMARINE_BUD.asItem());
            entries.accept(LARGE_AQUAMARINE_BUD.asItem());
            entries.accept(AQUAMARINE_CLUSTER.asItem());
            entries.accept(RUBY_BLOCK.asItem());
            entries.accept(BUDDING_RUBY.asItem());
            entries.accept(SMALL_RUBY_BUD.asItem());
            entries.accept(MEDIUM_RUBY_BUD.asItem());
            entries.accept(LARGE_RUBY_BUD.asItem());
            entries.accept(RUBY_CLUSTER.asItem());
            entries.accept(TOPAZ_BLOCK.asItem());
            entries.accept(BUDDING_TOPAZ.asItem());
            entries.accept(SMALL_TOPAZ_BUD.asItem());
            entries.accept(MEDIUM_TOPAZ_BUD.asItem());
            entries.accept(LARGE_TOPAZ_BUD.asItem());
            entries.accept(TOPAZ_CLUSTER.asItem());
        });

        CreativeModeTabEvents.modifyOutputEvent(ModItemGroup.RESONANCE_KEY).register(entries -> {
            entries.accept(RESONANCE_WORKBENCH.asItem());
            entries.accept(MAGITEK_CORE.asItem());
            entries.accept(VOXITE_STABILIZER.asItem());
            entries.accept(AMETHYST_FOCUS.asItem());
            entries.accept(MAGITEK_STONE.asItem());
            entries.accept(VOXITE_STONE.asItem());
            entries.accept(RESONANCE_SMELTER.asItem());
        });
    }
}
