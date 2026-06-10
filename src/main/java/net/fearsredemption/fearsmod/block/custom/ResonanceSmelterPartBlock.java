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

        if (forward == 1 && right == 0 && y == 0) {
            return SmelterPart.FRONT_PORT;
        }
        if (forward == 1) {
            return SmelterPart.FRONT_CASING;
        }
        if (y == 1) {
            return SmelterPart.TOP;
        }
        if (y == -1) {
            return SmelterPart.BOTTOM;
        }
        if (Math.abs(x) == 1 && Math.abs(z) == 1) {
            return SmelterPart.CORNER;
        }
        if ((Math.abs(x) + Math.abs(z) + Math.abs(y)) >= 2) {
            return SmelterPart.EDGE;
        }
        return SmelterPart.CASING;
    }

    public enum SmelterPart implements StringRepresentable {
        CASING("casing"),
        CORNER("corner"),
        EDGE("edge"),
        TOP("top"),
        BOTTOM("bottom"),
        FRONT_CASING("front_casing"),
        FRONT_PORT("front_port");

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
