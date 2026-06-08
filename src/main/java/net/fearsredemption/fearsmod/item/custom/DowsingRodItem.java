package net.fearsredemption.fearsmod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DowsingRodItem extends Item {

    public DowsingRodItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        if (context.getLevel().isClientSide() && player != null) {
            BlockPos positionClicked = context.getClickedPos();
            boolean foundBlock = false;

            for (int i = 0; i <= positionClicked.getY() + 64; i++) {
                Block blockBelow = context.getLevel().getBlockState(positionClicked.below(i)).getBlock();

                if (isValuableBlock(blockBelow)) {
                    outputValuableCoordinates(positionClicked.below(i), player, blockBelow);
                    foundBlock = true;
                    break;
                }
            }

            if (!foundBlock) {
                player.sendSystemMessage(Component.translatable("item.fearsmod.dowsing_rod.no_valuables"));
            }
        }

        if (player != null) {
            context.getItemInHand().hurtAndBreak(1, player, context.getHand());
        }

        return super.useOn(context);
    }

    private void outputValuableCoordinates(BlockPos blockPos, Player player, Block blockBelow) {
        player.sendSystemMessage(Component.literal("Found " + blockBelow.getName().getString() + " at (" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + ")"));
    }

    private boolean isValuableBlock(Block block) {
        return block == Blocks.COAL_ORE || block == Blocks.COPPER_ORE || block == Blocks.DIAMOND_ORE || block == Blocks.IRON_ORE;
    }
}
