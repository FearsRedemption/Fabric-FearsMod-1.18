package net.fearsredemption.fearsmod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BuddingCrystalBlock extends Block {
    private final Block smallBud;
    private final Block mediumBud;
    private final Block largeBud;
    private final Block cluster;

    public BuddingCrystalBlock(Block smallBud, Block mediumBud, Block largeBud, Block cluster, BlockBehaviour.Properties properties) {
        super(properties);
        this.smallBud = smallBud;
        this.mediumBud = mediumBud;
        this.largeBud = largeBud;
        this.cluster = cluster;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) != 0) {
            return;
        }

        Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
        BlockPos targetPos = pos.relative(direction);
        BlockState targetState = level.getBlockState(targetPos);
        BlockState nextState = nextGrowthState(targetState, direction);

        if (nextState == null) {
            return;
        }

        level.setBlockAndUpdate(targetPos, nextState);
    }

    private BlockState nextGrowthState(BlockState state, Direction direction) {
        if (state.isAir()) {
            return smallBud.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction);
        }

        if (!state.hasProperty(AmethystClusterBlock.FACING) || state.getValue(AmethystClusterBlock.FACING) != direction) {
            return null;
        }

        if (state.is(smallBud)) {
            return mediumBud.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction);
        }

        if (state.is(mediumBud)) {
            return largeBud.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction);
        }

        if (state.is(largeBud)) {
            return cluster.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction);
        }

        return null;
    }
}
