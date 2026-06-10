package net.fearsredemption.fearsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.fearsredemption.fearsmod.block.ModBlocks;
import net.fearsredemption.fearsmod.block.entity.ResonanceSocketBlockEntity;
import net.fearsredemption.fearsmod.item.ModItems;
import net.fearsredemption.fearsmod.journal.JournalUnlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ResonanceSocketBlock extends Block implements EntityBlock {
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResonanceSocketBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (stack.isEmpty()) {
            return takeStoredItem(level, pos, player);
        }

        if (ModItems.isResonanceStaff(stack)) {
            if (socketType == ResonanceSocketType.CORE) {
                return activateSmelterFrame(level, pos, player);
            }

            describeSocket(level, pos, player);
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

        return takeStoredItem(level, pos, player);
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        if (levelAccessor instanceof Level level && level.getBlockEntity(pos) instanceof ResonanceSocketBlockEntity socket && !socket.isEmpty()) {
            Block.popResource(level, pos, socket.removeStoredItem());
        }

        super.destroy(levelAccessor, pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof ResonanceSocketBlockEntity socket) || socket.isEmpty()) {
            return;
        }

        ItemStack stored = socket.getStoredItem();
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.35;
        double y = pos.getY() + 1.05 + random.nextDouble() * 0.35;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.35;
        level.addParticle(new DustParticleOptions(colorFor(stored), 0.9f), x, y, z, 0.0, 0.035, 0.0);

        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.15, pos.getZ() + 0.5, 0.0, 0.02, 0.0);
        }

        if (random.nextInt(8) == 0) {
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stored.getItem()), x, y, z, 0.0, 0.02, 0.0);
        }
    }

    private InteractionResult takeStoredItem(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof ResonanceSocketBlockEntity socket)) {
            return InteractionResult.PASS;
        }

        if (socket.isEmpty()) {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_socket.empty"));
            return InteractionResult.SUCCESS;
        }

        ItemStack removed = socket.removeStoredItem();
        Component itemName = removed.getDisplayName();
        if (!player.addItem(removed)) {
            player.drop(removed, false);
        }

        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.45f, 0.8f);
        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_socket.removed", itemName));
        return InteractionResult.SUCCESS;
    }

    private void describeSocket(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof ResonanceSocketBlockEntity socket && !socket.isEmpty()) {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_socket.contains", socket.getStoredItem().getDisplayName()));
            return;
        }

        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_socket.structure_component"));
    }

    private InteractionResult activateSmelterFrame(Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        if (!isCompleteSmelterFrame(level, pos)) {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_smelter.structure_incomplete"));
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            JournalUnlocks.unlock(serverPlayer, "resonance_smelter_structure");
        }

        Direction facing = player.getDirection().getOpposite();
        level.setBlock(pos, ModBlocks.RESONANCE_SMELTER.defaultBlockState().setValue(ResonanceSmelterBlock.FACING, facing), 3);
        serverLevel.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D, 36, 0.55D, 0.55D, 0.55D, 0.04D);
        serverLevel.sendParticles(new DustParticleOptions(0xB987FF, 1.3F), pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D, 44, 0.65D, 0.65D, 0.65D, 0.02D);
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.85F, 1.1F);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, 0.75F);
        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_smelter.activated"));

        if (player instanceof ServerPlayer serverPlayer) {
            JournalUnlocks.unlock(serverPlayer, "resonance_smelter_active");
        }

        return InteractionResult.SUCCESS;
    }

    public static boolean isCompleteSmelterFrame(Level level, BlockPos corePos) {
        if (level.getBlockState(corePos).getBlock() != ModBlocks.MAGITEK_CORE) {
            return false;
        }

        for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    Block expected = expectedSmelterFrameBlock(x, y, z);
                    if (level.getBlockState(corePos.offset(x, y, z)).getBlock() != expected) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static Block expectedSmelterFrameBlock(int x, int y, int z) {
        boolean middleLayer = y == 0;
        boolean corner = Math.abs(x) == 1 && Math.abs(z) == 1;
        boolean centerOfOuterLayer = x == 0 && z == 0;
        if (middleLayer) {
            return corner ? ModBlocks.VOXITE_STONE : ModBlocks.MAGITEK_STONE;
        }

        return (corner || centerOfOuterLayer) ? ModBlocks.MAGITEK_STONE : ModBlocks.VOXITE_STONE;
    }

    public static int colorFor(ItemStack stack) {
        if (stack.getItem() == Items.COPPER_INGOT || stack.getItem() == ModItems.RESONANT_COPPER) {
            return 0xE49257;
        }

        if (stack.getItem() == Items.IRON_INGOT || stack.getItem() == ModItems.STABILIZED_IRON_PLATE) {
            return 0xDDE4E2;
        }

        if (stack.getItem() == Items.AMETHYST_SHARD || stack.getItem() == ModItems.FOCUSING_LENS) {
            return 0xB987FF;
        }

        if (stack.getItem() == ModItems.MAGITEK_INGOT || stack.getItem() == ModItems.CHARGED_MAGITEK_CORE) {
            return 0xE34FA4;
        }

        if (stack.getItem() == ModItems.VOXITE_INGOT) {
            return 0x6FE6EA;
        }

        return 0xD8B4FF;
    }
}
