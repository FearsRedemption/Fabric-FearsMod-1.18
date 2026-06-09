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

    public static final ResourceKey<PlacedFeature> AGATE_GEODE_PLACED_KEY = geodeKey("agate_geode");
    public static final ResourceKey<PlacedFeature> AMBER_GEODE_PLACED_KEY = geodeKey("amber_geode");
    public static final ResourceKey<PlacedFeature> AQUAMARINE_GEODE_PLACED_KEY = geodeKey("aquamarine_geode");
    public static final ResourceKey<PlacedFeature> RUBY_GEODE_PLACED_KEY = geodeKey("ruby_geode");
    public static final ResourceKey<PlacedFeature> TOPAZ_GEODE_PLACED_KEY = geodeKey("topaz_geode");

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

        addOverworldGeode(AGATE_GEODE_PLACED_KEY);
        addOverworldGeode(AMBER_GEODE_PLACED_KEY);
        addOverworldGeode(AQUAMARINE_GEODE_PLACED_KEY);
        addOverworldGeode(RUBY_GEODE_PLACED_KEY);
        addOverworldGeode(TOPAZ_GEODE_PLACED_KEY);
    }

    private static ResourceKey<PlacedFeature> geodeKey(String name) {
        return ResourceKey.create(
                Registries.PLACED_FEATURE,
                Identifier.fromNamespaceAndPath(MOD_ID, name)
        );
    }

    private static void addOverworldGeode(ResourceKey<PlacedFeature> key) {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_DECORATION,
                key
        );
    }
}
