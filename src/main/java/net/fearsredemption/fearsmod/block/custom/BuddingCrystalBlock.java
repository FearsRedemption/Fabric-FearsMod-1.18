package net.fearsredemption.fearsmod.block.custom;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BuddingCrystalBlock extends Block {
    private final Supplier<BlockState> clusterState;

    public BuddingCrystalBlock(Supplier<BlockState> clusterState, BlockBehaviour.Properties properties) {
        super(properties);
        this.clusterState = clusterState;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) != 0) {
            return;
        }

        Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
        BlockPos targetPos = pos.relative(direction);
        if (!level.getBlockState(targetPos).isAir()) {
            return;
        }

        level.setBlockAndUpdate(targetPos, clusterState.get().setValue(AmethystClusterBlock.FACING, direction));
    }
}
