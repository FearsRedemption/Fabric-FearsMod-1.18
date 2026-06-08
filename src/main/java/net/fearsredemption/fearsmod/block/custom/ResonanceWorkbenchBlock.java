package net.fearsredemption.fearsmod.block.custom;

import net.fearsredemption.fearsmod.block.MobBlocks;
import net.fearsredemption.fearsmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ResonanceWorkbenchBlock extends Block {
    private static final int APPARATUS_RADIUS = 3;

    public ResonanceWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ResonanceSetup setup = scanApparatus(level, pos);
        if (!setup.hasRequiredBasicApparatus()) {
            explainMissingApparatus(player, setup);
            return InteractionResult.SUCCESS;
        }

        Item heldItem = stack.getItem();
        if (heldItem == Items.COPPER_INGOT) {
            finishRecipe(level, pos, player, stack, ModItems.RESONANT_COPPER);
            return InteractionResult.SUCCESS;
        }

        if (heldItem == Items.IRON_INGOT) {
            if (!setup.stable()) {
                player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.unstable"));
                return InteractionResult.SUCCESS;
            }

            finishRecipe(level, pos, player, stack, ModItems.STABILIZED_IRON_PLATE);
            return InteractionResult.SUCCESS;
        }

        if (heldItem == ModItems.MAGITEK_INGOT) {
            if (!consumeInventoryItem(player, Items.AMETHYST_SHARD)) {
                player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.needs_amethyst_shard"));
                return InteractionResult.SUCCESS;
            }

            finishRecipe(level, pos, player, stack, ModItems.CHARGED_MAGITEK_CORE);
            return InteractionResult.SUCCESS;
        }

        player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.no_pattern"));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            ResonanceSetup setup = scanApparatus(level, pos);
            player.sendSystemMessage(Component.translatable(
                    "block.fearsmod.resonance_workbench.status",
                    setup.hasMagitekCore(),
                    setup.hasAmethystFocus(),
                    setup.stable()
            ));
        }

        return InteractionResult.SUCCESS;
    }

    private static ResonanceSetup scanApparatus(Level level, BlockPos workbenchPos) {
        boolean hasMagitekCore = false;
        boolean hasAmethystFocus = false;
        boolean stable = false;

        BlockPos start = workbenchPos.offset(-APPARATUS_RADIUS, -APPARATUS_RADIUS, -APPARATUS_RADIUS);
        BlockPos end = workbenchPos.offset(APPARATUS_RADIUS, APPARATUS_RADIUS, APPARATUS_RADIUS);

        for (BlockPos scanPos : BlockPos.betweenClosed(start, end)) {
            if (scanPos.equals(workbenchPos)) {
                continue;
            }

            Block block = level.getBlockState(scanPos).getBlock();
            if (block == MobBlocks.MAGITEK_CORE) {
                hasMagitekCore = true;
            } else if (block == MobBlocks.AMETHYST_FOCUS) {
                hasAmethystFocus = true;
            } else if (block == MobBlocks.VOXITE_STABILIZER) {
                stable = true;
            }
        }

        return new ResonanceSetup(hasMagitekCore, hasAmethystFocus, stable);
    }

    private static void explainMissingApparatus(Player player, ResonanceSetup setup) {
        if (!setup.hasMagitekCore()) {
            player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.needs_core"));
        }

        if (!setup.hasAmethystFocus()) {
            player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.needs_focus"));
        }
    }

    private static void finishRecipe(Level level, BlockPos pos, Player player, ItemStack inputStack, Item outputItem) {
        if (!player.isCreative()) {
            inputStack.shrink(1);
        }

        ItemStack outputStack = new ItemStack(outputItem);
        if (!player.addItem(outputStack)) {
            player.drop(outputStack, false);
        }

        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.8f, 1.1f);
        player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.complete", outputStack.getDisplayName()));
    }

    private static boolean consumeInventoryItem(Player player, Item item) {
        Inventory inventory = player.getInventory();

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.getItem() != item) {
                continue;
            }

            if (!player.isCreative()) {
                stack.shrink(1);
            }

            return true;
        }

        return false;
    }

    private record ResonanceSetup(boolean hasMagitekCore, boolean hasAmethystFocus, boolean stable) {
        private boolean hasRequiredBasicApparatus() {
            return hasMagitekCore && hasAmethystFocus;
        }
    }
}
