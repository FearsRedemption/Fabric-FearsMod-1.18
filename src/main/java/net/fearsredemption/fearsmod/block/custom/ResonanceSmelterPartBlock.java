package net.fearsredemption.fearsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.fearsredemption.fearsmod.block.ModBlocks;
import net.fearsredemption.fearsmod.block.entity.ResonanceSmelterBlockEntity;
import net.fearsredemption.fearsmod.block.entity.ResonanceSmelterPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ResonanceSmelterPartBlock extends BaseEntityBlock {
    public static final MapCodec<ResonanceSmelterPartBlock> CODEC = simpleCodec(ResonanceSmelterPartBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<SmelterPart> PART = EnumProperty.create("part", SmelterPart.class);

    public ResonanceSmelterPartBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, SmelterPart.CASING));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
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
                ResonanceSmelterBlock.disassembleToFrame(level, controller, player);
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

    public static SmelterPart partForOffset(int x, int y, int z, Direction facing) {
        int forward = x * facing.getStepX() + z * facing.getStepZ();
        Direction rightDirection = facing.getClockWise();
        int right = x * rightDirection.getStepX() + z * rightDirection.getStepZ();

        if (forward == 1) {
            if (y == 1) {
                return right < 0 ? SmelterPart.FRONT_TOP_LEFT : right > 0 ? SmelterPart.FRONT_TOP_RIGHT : SmelterPart.FRONT_TOP;
            }
            if (y == -1) {
                return right < 0 ? SmelterPart.FRONT_BOTTOM_LEFT : right > 0 ? SmelterPart.FRONT_BOTTOM_RIGHT : SmelterPart.FRONT_BOTTOM;
            }
            if (right < 0) {
                return SmelterPart.FRONT_LEFT;
            }
            if (right > 0) {
                return SmelterPart.FRONT_RIGHT;
            }
            return SmelterPart.FRONT_PORT;
        }
        if (forward == -1) {
            if (y == 1) {
                return right < 0 ? SmelterPart.BACK_TOP_LEFT : right > 0 ? SmelterPart.BACK_TOP_RIGHT : SmelterPart.BACK_TOP;
            }
            if (y == -1) {
                return right < 0 ? SmelterPart.BACK_BOTTOM_LEFT : right > 0 ? SmelterPart.BACK_BOTTOM_RIGHT : SmelterPart.BACK_BOTTOM;
            }
            if (right < 0) {
                return SmelterPart.BACK_LEFT;
            }
            if (right > 0) {
                return SmelterPart.BACK_RIGHT;
            }
            return SmelterPart.BACK_VENT;
        }
        if (y == -1) {
            return right < 0 ? SmelterPart.BOTTOM_LEFT : right > 0 ? SmelterPart.BOTTOM_RIGHT : SmelterPart.BOTTOM_CORE;
        }
        if (y == 1) {
            return right < 0 ? SmelterPart.TOP_LEFT : right > 0 ? SmelterPart.TOP_RIGHT : SmelterPart.TOP_CORE;
        }
        if (right < 0) {
            return SmelterPart.LEFT_SIDE;
        }
        if (right > 0) {
            return SmelterPart.RIGHT_SIDE;
        }
        return SmelterPart.CASING;
    }

    public enum SmelterPart implements StringRepresentable {
        CASING("casing"),
        FRONT_TOP_LEFT("front_top_left"),
        FRONT_TOP("front_top"),
        FRONT_TOP_RIGHT("front_top_right"),
        FRONT_LEFT("front_left"),
        FRONT_PORT("front_port"),
        FRONT_RIGHT("front_right"),
        FRONT_BOTTOM_LEFT("front_bottom_left"),
        FRONT_BOTTOM("front_bottom"),
        FRONT_BOTTOM_RIGHT("front_bottom_right"),
        BACK_TOP_LEFT("back_top_left"),
        BACK_TOP("back_top"),
        BACK_TOP_RIGHT("back_top_right"),
        BACK_LEFT("back_left"),
        BACK_VENT("back_vent"),
        BACK_RIGHT("back_right"),
        BACK_BOTTOM_LEFT("back_bottom_left"),
        BACK_BOTTOM("back_bottom"),
        BACK_BOTTOM_RIGHT("back_bottom_right"),
        TOP_LEFT("top_left"),
        TOP_CORE("top_core"),
        TOP_RIGHT("top_right"),
        BOTTOM_LEFT("bottom_left"),
        BOTTOM_CORE("bottom_core"),
        BOTTOM_RIGHT("bottom_right"),
        LEFT_SIDE("left_side"),
        RIGHT_SIDE("right_side");

        private final String name;

        SmelterPart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
