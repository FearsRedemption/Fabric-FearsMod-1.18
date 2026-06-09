package net.fearsredemption.fearsmod.item.custom;

import net.fearsredemption.fearsmod.block.custom.ResonanceWorkbenchBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ResonanceStaffItem extends Item {
    public ResonanceStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        HitResult hit = player.pick(8.0D, 0.0F, false);
        if (hit instanceof BlockHitResult blockHit) {
            return ResonanceWorkbenchBlock.activateNearestWithStaff(level, blockHit.getBlockPos(), player, stack, hand);
        }

        player.sendSystemMessage(Component.translatable("item.fearsmod.resonance_staff.no_apparatus"));
        return InteractionResult.SUCCESS;
    }
}
