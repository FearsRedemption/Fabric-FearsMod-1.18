package net.fearsredemption.fearsmod.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fearsredemption.fearsmod.FearsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public class ModWorldGeneration {
    public static final ResourceKey<PlacedFeature> VOXITE_ORE_PLACED_KEY = ResourceKey.create(
            Registries.PLACED_FEATURE,
            Identifier.fromNamespaceAndPath(MOD_ID, "voxite_ore_placed")
    );

    public static final ResourceKey<PlacedFeature> MAGITEK_ORE_PLACED_KEY = ResourceKey.create(
            Registries.PLACED_FEATURE,
            Identifier.fromNamespaceAndPath(MOD_ID, "magitek_ore_placed")
    );

    public static final ResourceKey<PlacedFeature> DEEPSLATE_VOXITE_ORE_PLACED_KEY = ResourceKey.create(
            Registries.PLACED_FEATURE,
            Identifier.fromNamespaceAndPath(MOD_ID, "deepslate_voxite_ore_placed")
    );

    public static final ResourceKey<PlacedFeature> DEEPSLATE_MAGITEK_ORE_PLACED_KEY = ResourceKey.create(
            Registries.PLACED_FEATURE,
            Identifier.fromNamespaceAndPath(MOD_ID, "deepslate_magitek_ore_placed")
    );

    public static void initialize() {
        FearsMod.LOGGER.info("Registering Mod World Generation for " + MOD_ID);

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                VOXITE_ORE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                MAGITEK_ORE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                DEEPSLATE_VOXITE_ORE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                DEEPSLATE_MAGITEK_ORE_PLACED_KEY
        );
    }
}
