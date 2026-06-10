package net.fearsredemption.fearsmod.item.custom;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.fearsredemption.fearsmod.block.custom.ResonanceSocketBlock;
import net.fearsredemption.fearsmod.block.custom.ResonanceWorkbenchBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ResonanceStaffItem extends Item {
    public ResonanceStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if ((slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND) || entity.getRandom().nextInt(5) != 0) {
            return;
        }

        Vec3 look = entity.getLookAngle();
        double side = slot == EquipmentSlot.MAINHAND ? 0.18D : -0.18D;
        double yawRadians = Math.toRadians(entity.getYRot());
        double sideX = Math.cos(yawRadians) * side;
        double sideZ = Math.sin(yawRadians) * side;
        double x = entity.getX() + look.x * 1.45D + sideX + (entity.getRandom().nextDouble() - 0.5D) * 0.08D;
        double y = entity.getY() + entity.getBbHeight() * 0.72D + look.y * 0.95D + entity.getRandom().nextDouble() * 0.12D;
        double z = entity.getZ() + look.z * 1.45D + sideZ + (entity.getRandom().nextDouble() - 0.5D) * 0.08D;
        level.sendParticles(new DustParticleOptions(0xD3262E, 0.55F), x, y, z, 1, 0.015D, 0.015D, 0.015D, 0.0D);
        if (entity.getRandom().nextInt(3) == 0) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, 0.02D, 0.02D, 0.02D, 0.0D);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        InteractionResult smelterResult = ResonanceSocketBlock.activateSmelterFrameFromStaff(context.getLevel(), context.getClickedPos(), player, true);
        if (smelterResult != InteractionResult.PASS) {
            return smelterResult;
        }

        return ResonanceWorkbenchBlock.activateNearestWithStaff(context.getLevel(), context.getClickedPos(), player, context.getItemInHand(), context.getHand());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        HitResult hit = player.pick(8.0D, 0.0F, false);
        if (hit instanceof BlockHitResult blockHit) {
            InteractionResult smelterResult = ResonanceSocketBlock.activateSmelterFrameFromStaff(level, blockHit.getBlockPos(), player, true);
            if (smelterResult != InteractionResult.PASS) {
                return smelterResult;
            }

            return ResonanceWorkbenchBlock.activateNearestWithStaff(level, blockHit.getBlockPos(), player, stack, hand);
        }

        player.sendSystemMessage(Component.translatable("item.fearsmod.resonance_staff.no_apparatus"));
        return InteractionResult.SUCCESS;
    }
}
