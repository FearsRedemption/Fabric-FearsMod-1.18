package net.fearsredemption.fearsmod.item.custom;

import net.fearsredemption.fearsmod.block.MobBlocks;
import net.fearsredemption.fearsmod.block.custom.ResonanceWorkbenchBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class DowsingRodItem extends Item {

    public DowsingRodItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == MobBlocks.RESONANCE_WORKBENCH) {
            if (context.getLevel().isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            return ResonanceWorkbenchBlock.activateWithStaff(
                    context.getLevel(),
                    context.getClickedPos(),
                    player,
                    context.getItemInHand(),
                    context.getHand()
            );
        }

        if (!context.getLevel().isClientSide()) {
            player.sendSystemMessage(Component.translatable("item.fearsmod.resonance_staff.no_apparatus"));
        }

        return InteractionResult.SUCCESS;
    }
}
