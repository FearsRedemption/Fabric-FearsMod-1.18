package net.fearsredemption.fearsmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fearsredemption.fearsmod.block.entity.ModBlockEntities;
import net.fearsredemption.fearsmod.client.renderer.ResonanceSocketRenderer;

public class FearsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.RESONANCE_SOCKET, ResonanceSocketRenderer::new);
    }
}
