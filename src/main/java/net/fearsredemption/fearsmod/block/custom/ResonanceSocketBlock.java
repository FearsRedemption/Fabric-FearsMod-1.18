package net.fearsredemption.fearsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.fearsredemption.fearsmod.block.ModBlocks;
import net.fearsredemption.fearsmod.item.ModItems;
import net.fearsredemption.fearsmod.journal.JournalUnlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ResonanceSocketBlock extends Block {
    public static final MapCodec<ResonanceSocketBlock> CODEC = simpleCodec(properties -> new ResonanceSocketBlock(ResonanceSocketType.FOCUS, properties));

    private final ResonanceSocketType socketType;

    public ResonanceSocketBlock(ResonanceSocketType socketType, Properties properties) {
        super(properties);
        this.socketType = socketType;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public ResonanceSocketType socketType() {
        return socketType;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (stack.isEmpty()) {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_socket.structure_component"));
            return InteractionResult.SUCCESS;
        }

        if (ModItems.isResonanceStaff(stack)) {
            if (socketType == ResonanceSocketType.CORE) {
                return activateSmelterFrameFromStaff(level, pos, player, false);
            }

            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_socket.structure_component"));
            return InteractionResult.SUCCESS;
        }

        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_socket.structure_component"));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_socket.structure_component"));
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult activateSmelterFrameFromStaff(Level level, BlockPos clickedPos, Player player, boolean searchNearby) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos corePos = findSmelterCoreCandidate(level, clickedPos, searchNearby);
        if (corePos == null) {
            return InteractionResult.PASS;
        }

        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_smelter.staff_detected"));
        SmelterFrameValidation validation = validateSmelterFrame(level, corePos);
        if (!validation.complete()) {
            player.sendOverlayMessage(validation.message());
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            JournalUnlocks.unlock(serverPlayer, "resonance_smelter_structure");
        }

        Direction facing = player.getDirection().getOpposite();
        ResonanceSmelterBlock.assembleSmelter(serverLevel, corePos, facing);
        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_smelter.activated"));

        if (player instanceof ServerPlayer serverPlayer) {
            JournalUnlocks.unlock(serverPlayer, "resonance_smelter_active");
        }

        return InteractionResult.SUCCESS;
    }

    public static boolean isCompleteSmelterFrame(Level level, BlockPos corePos) {
        return validateSmelterFrame(level, corePos).complete();
    }

    public static SmelterFrameValidation validateSmelterFrame(Level level, BlockPos corePos) {
        if (level.getBlockState(corePos).getBlock() != ModBlocks.MAGITEK_CORE) {
            return SmelterFrameValidation.incomplete(Component.translatable(
                    "block.fearsmod.resonance_smelter.expected_center",
                    blockName(ModBlocks.MAGITEK_CORE),
                    blockName(level.getBlockState(corePos).getBlock())
            ));
        }

        for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    Block expected = frameBlockForOffset(x, y, z);
                    Block actual = level.getBlockState(corePos.offset(x, y, z)).getBlock();
                    if (actual != expected) {
                        return SmelterFrameValidation.incomplete(Component.translatable(
                                "block.fearsmod.resonance_smelter.structure_incomplete_detail",
                                x,
                                y,
                                z,
                                blockName(expected),
                                blockName(actual)
                        ));
                    }
                }
            }
        }

        return SmelterFrameValidation.success();
    }

    private static BlockPos findSmelterCoreCandidate(Level level, BlockPos clickedPos, boolean searchNearby) {
        if (level.getBlockState(clickedPos).getBlock() == ModBlocks.MAGITEK_CORE) {
            return clickedPos;
        }

        if (!searchNearby) {
            return null;
        }

        for (BlockPos candidate : BlockPos.betweenClosed(clickedPos.offset(-1, -1, -1), clickedPos.offset(1, 1, 1))) {
            if (level.getBlockState(candidate).getBlock() == ModBlocks.MAGITEK_CORE) {
                return candidate.immutable();
            }
        }

        return null;
    }

    public static Block frameBlockForOffset(int x, int y, int z) {
        boolean middleLayer = y == 0;
        boolean corner = Math.abs(x) == 1 && Math.abs(z) == 1;
        boolean centerOfOuterLayer = x == 0 && z == 0;
        if (middleLayer) {
            return corner ? ModBlocks.VOXITE_STONE : ModBlocks.MAGITEK_STONE;
        }

        return (corner || centerOfOuterLayer) ? ModBlocks.MAGITEK_STONE : ModBlocks.VOXITE_STONE;
    }

    private static Component blockName(Block block) {
        return block.getName();
    }

    public record SmelterFrameValidation(boolean complete, Component message) {
        private static SmelterFrameValidation success() {
            return new SmelterFrameValidation(true, Component.translatable("block.fearsmod.resonance_smelter.structure_complete"));
        }

        private static SmelterFrameValidation incomplete(Component message) {
            return new SmelterFrameValidation(false, message);
        }
    }
}
