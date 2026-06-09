package net.fearsredemption.fearsmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fearsredemption.fearsmod.block.entity.ResonanceSocketBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public class ResonanceSocketRenderer implements BlockEntityRenderer<ResonanceSocketBlockEntity, ResonanceSocketRenderState> {
    private static final float ITEM_SCALE = 0.65f;

    private final ItemModelResolver itemModelResolver;

    public ResonanceSocketRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ResonanceSocketRenderState createRenderState() {
        return new ResonanceSocketRenderState();
    }

    @Override
    public void extractRenderState(ResonanceSocketBlockEntity socket, ResonanceSocketRenderState state, float tickDelta, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(socket, state, tickDelta, cameraPos, crumblingOverlay);
        state.item.clear();

        ItemStack stored = socket.getStoredItem();
        if (stored.isEmpty()) {
            return;
        }

        if (socket.getLevel() != null) {
            state.ageInTicks = socket.getLevel().getGameTime() + tickDelta;
        }
        state.seed = (int) (socket.getBlockPos().asLong() ^ Item.getId(stored.getItem()));
        itemModelResolver.updateForTopItem(state.item, stored, ItemDisplayContext.GROUND, socket.getLevel(), null, state.seed);
    }

    @Override
    public void submit(ResonanceSocketRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (state.item.isEmpty()) {
            return;
        }

        float bob = Mth.sin((state.ageInTicks + state.seed * 0.01f) * 0.12f) * 0.08f;
        float spin = (state.ageInTicks * 2.2f + state.seed) % 360.0f;

        poseStack.pushPose();
        poseStack.translate(0.5f, 1.12f + bob, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
