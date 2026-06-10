package net.fearsredemption.fearsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.fearsredemption.fearsmod.block.ModBlocks;
import net.fearsredemption.fearsmod.block.entity.ResonanceSmelterBlockEntity;
import net.fearsredemption.fearsmod.block.entity.ResonanceSmelterPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ResonanceSmelterPartBlock extends BaseEntityBlock {
    public static final MapCodec<ResonanceSmelterPartBlock> CODEC = simpleCodec(ResonanceSmelterPartBlock::new);

    public ResonanceSmelterPartBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResonanceSmelterPartBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return openController(level, pos, player);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        return openController(level, pos, player);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos controller = controllerPos(level, pos);
            if (controller != null) {
                ResonanceSmelterBlock.breakAsFrameDrops(level, controller, player);
            } else {
                Block.popResource(level, pos, new ItemStack(ModBlocks.MAGITEK_STONE));
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    private InteractionResult openController(Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos controller = controllerPos(level, pos);
        if (controller != null && level.getBlockEntity(controller) instanceof ResonanceSmelterBlockEntity smelter) {
            player.openMenu(smelter);
        } else {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_smelter.missing_controller"));
        }

        return InteractionResult.SUCCESS;
    }

    private BlockPos controllerPos(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ResonanceSmelterPartBlockEntity part) {
            return part.controllerPos();
        }

        return null;
    }
}
