package net.fearsredemption.fearsmod.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fearsredemption.fearsmod.FearsMod;
import net.fearsredemption.fearsmod.block.MobBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public class ModBlockEntities {
    public static final BlockEntityType<ResonanceSocketBlockEntity> RESONANCE_SOCKET = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "resonance_socket"),
            FabricBlockEntityTypeBuilder.create(
                    ResonanceSocketBlockEntity::new,
                    MobBlocks.MAGITEK_CORE,
                    MobBlocks.VOXITE_STABILIZER,
                    MobBlocks.AMETHYST_FOCUS
            ).build()
    );

    public static void initialize() {
        FearsMod.LOGGER.info("Registering ModBlockEntities for " + MOD_ID);
    }
}
