package net.fearsredemption.fearsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.fearsredemption.fearsmod.block.ModBlocks;
import net.fearsredemption.fearsmod.block.entity.ModBlockEntities;
import net.fearsredemption.fearsmod.block.entity.ResonanceSmelterBlockEntity;
import net.fearsredemption.fearsmod.block.entity.ResonanceSmelterPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ResonanceSmelterBlock extends BaseEntityBlock {
    public static final MapCodec<ResonanceSmelterBlock> CODEC = simpleCodec(ResonanceSmelterBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ResonanceSmelterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResonanceSmelterBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide() || blockEntityType != ModBlockEntities.RESONANCE_SMELTER) {
            return null;
        }

        return (tickLevel, tickPos, tickState, blockEntity) ->
                ResonanceSmelterBlockEntity.serverTick((ServerLevel) tickLevel, tickPos, tickState, (ResonanceSmelterBlockEntity) blockEntity);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ResonanceSmelterBlockEntity smelter) {
            player.openMenu(smelter);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ResonanceSmelterBlockEntity smelter) {
            player.openMenu(smelter);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            disassembleToFrame(level, pos, player);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    public static void assembleSmelter(ServerLevel level, BlockPos controllerPos, Direction facing) {
        for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    BlockPos target = controllerPos.offset(x, y, z);
                    if (x == 0 && y == 0 && z == 0) {
                        level.setBlock(target, ModBlocks.RESONANCE_SMELTER.defaultBlockState().setValue(FACING, facing), 3);
                    } else {
                        level.setBlock(target, ModBlocks.RESONANCE_SMELTER_PART.defaultBlockState()
                                .setValue(ResonanceSmelterPartBlock.FACING, facing)
                                .setValue(ResonanceSmelterPartBlock.PART, ResonanceSmelterPartBlock.partForOffset(x, y, z, facing)), 3);
                        if (level.getBlockEntity(target) instanceof ResonanceSmelterPartBlockEntity part) {
                            part.setControllerPos(controllerPos);
                        }
                    }

                    level.sendParticles(ParticleTypes.END_ROD, target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 3, 0.25D, 0.25D, 0.25D, 0.02D);
                    level.sendParticles(new DustParticleOptions(0xB987FF, 0.85F), target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 2, 0.22D, 0.22D, 0.22D, 0.01D);
                }
            }
        }

        level.playSound(null, controllerPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.9F, 1.0F);
        level.playSound(null, controllerPos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, 0.7F);
    }

    public static void disassembleToFrame(Level level, BlockPos controllerPos, Player player) {
        if (level.getBlockEntity(controllerPos) instanceof ResonanceSmelterBlockEntity smelter) {
            Containers.dropContents(level, controllerPos, smelter);
        }

        for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    BlockPos target = controllerPos.offset(x, y, z);
                    Block block = level.getBlockState(target).getBlock();
                    if (block != ModBlocks.RESONANCE_SMELTER && block != ModBlocks.RESONANCE_SMELTER_PART) {
                        continue;
                    }

                    Block restored = x == 0 && y == 0 && z == 0
                            ? ModBlocks.MAGITEK_CORE
                            : ResonanceSocketBlock.frameBlockForOffset(x, y, z);
                    level.setBlock(target, restored.defaultBlockState(), 3);
                }
            }
        }

        if (player != null) {
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("block.fearsmod.resonance_smelter.disassembled"));
        }
    }
}
