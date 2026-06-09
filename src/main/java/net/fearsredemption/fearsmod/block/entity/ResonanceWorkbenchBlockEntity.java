package net.fearsredemption.fearsmod.block.entity;

import java.util.UUID;

import net.fearsredemption.fearsmod.block.custom.ResonanceWorkbenchBlock;
import net.fearsredemption.fearsmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class ResonanceWorkbenchBlockEntity extends BlockEntity {
    private static final String ACTIVE_KEY = "starter_ritual_active";
    private static final String TICKS_KEY = "starter_ritual_ticks";
    private static final String STAFF_VARIANT_KEY = "starter_staff_variant";
    private static final String OWNER_KEY = "starter_ritual_owner";

    private boolean ritualActive;
    private int ritualTicks;
    private int staffVariantIndex;
    private UUID ownerUuid;

    public ResonanceWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_WORKBENCH, pos, state);
    }

    public boolean isRitualActive() {
        return ritualActive;
    }

    public void startStarterRitual(Player player, Item staffItem) {
        ritualActive = true;
        ritualTicks = 0;
        staffVariantIndex = ModItems.staffVariantIndex(staffItem);
        ownerUuid = player.getUUID();
        markUpdated();
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, ResonanceWorkbenchBlockEntity workbench) {
        if (!workbench.ritualActive) {
            return;
        }

        if (!ResonanceWorkbenchBlock.hasStarterStaffStructure(level, pos)) {
            workbench.cancelRitual(level);
            return;
        }

        workbench.ritualTicks++;

        if (workbench.ritualTicks == 10 && !workbench.consumeRitualItem(level, Items.STICK)) {
            workbench.cancelRitual(level);
            return;
        }

        if (workbench.ritualTicks == 20 && !workbench.consumeRitualItem(level, ModItems.VOXITE_INGOT)) {
            workbench.cancelRitual(level);
            return;
        }

        if (workbench.ritualTicks == 30 && !workbench.consumeRitualItem(level, ModItems.MAGITEK_INGOT)) {
            workbench.cancelRitual(level);
            return;
        }

        if (workbench.ritualTicks == 40 && !workbench.consumeRitualItem(level, ModItems.shardForStaff(ModItems.staffByVariantIndex(workbench.staffVariantIndex)))) {
            workbench.cancelRitual(level);
            return;
        }

        if (workbench.ritualTicks == 60) {
            workbench.finalPulse(level);
        }

        if (workbench.ritualTicks >= 70) {
            workbench.completeRitual(level);
        }
    }

    private boolean consumeRitualItem(ServerLevel level, Item item) {
        ItemEntity entity = ResonanceWorkbenchBlock.findNearbyItemEntity(level, worldPosition, item);
        if (entity == null) {
            return false;
        }

        ItemStack stack = entity.getItem();
        Item particleItem = stack.getItem();
        stack.shrink(1);
        if (stack.isEmpty()) {
            entity.discard();
        } else {
            entity.setItem(stack);
        }

        double x = worldPosition.getX() + 0.5D;
        double y = worldPosition.getY() + 1.15D;
        double z = worldPosition.getZ() + 0.5D;
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, particleItem), x, y, z, 12, 0.25D, 0.18D, 0.25D, 0.04D);
        level.sendParticles(new DustParticleOptions(ResonanceWorkbenchBlock.colorForRitualItem(item), 1.0F), x, y, z, 12, 0.35D, 0.2D, 0.35D, 0.02D);
        level.playSound(null, worldPosition, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.75F, 0.75F + ritualTicks / 60.0F);
        markUpdated();
        return true;
    }

    private void finalPulse(ServerLevel level) {
        double x = worldPosition.getX() + 0.5D;
        double y = worldPosition.getY() + 1.25D;
        double z = worldPosition.getZ() + 0.5D;
        level.sendParticles(ParticleTypes.END_ROD, x, y, z, 28, 0.45D, 0.35D, 0.45D, 0.04D);
        level.sendParticles(new DustParticleOptions(0xB987FF, 1.4F), x, y, z, 34, 0.55D, 0.35D, 0.55D, 0.02D);
        level.playSound(null, worldPosition, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, 0.8F);
        level.playSound(null, worldPosition, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.55F, 1.55F);
    }

    private void completeRitual(ServerLevel level) {
        ItemStack staff = new ItemStack(ModItems.staffByVariantIndex(staffVariantIndex));
        Player player = ownerUuid == null ? null : level.getPlayerInAnyDimension(ownerUuid);
        if (player == null || !player.addItem(staff)) {
            ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.25D, worldPosition.getZ() + 0.5D, staff);
            level.addFreshEntity(entity);
        }

        double x = worldPosition.getX() + 0.5D;
        double y = worldPosition.getY() + 1.35D;
        double z = worldPosition.getZ() + 0.5D;
        level.sendParticles(ParticleTypes.POOF, x, y, z, 18, 0.25D, 0.2D, 0.25D, 0.04D);
        level.sendParticles(ParticleTypes.END_ROD, x, y, z, 22, 0.35D, 0.25D, 0.35D, 0.03D);
        level.playSound(null, worldPosition, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.55F, 1.45F);
        resetRitual();
        markUpdated();
    }

    private void cancelRitual(ServerLevel level) {
        level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.45F, 1.35F);
        level.sendParticles(ParticleTypes.SMOKE, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.05D, worldPosition.getZ() + 0.5D, 10, 0.25D, 0.15D, 0.25D, 0.02D);
        resetRitual();
        markUpdated();
    }

    private void resetRitual() {
        ritualActive = false;
        ritualTicks = 0;
        staffVariantIndex = 0;
        ownerUuid = null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean(ACTIVE_KEY, ritualActive);
        output.putInt(TICKS_KEY, ritualTicks);
        output.putInt(STAFF_VARIANT_KEY, staffVariantIndex);
        if (ownerUuid != null) {
            output.putString(OWNER_KEY, ownerUuid.toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ritualActive = input.getBooleanOr(ACTIVE_KEY, false);
        ritualTicks = input.getIntOr(TICKS_KEY, 0);
        staffVariantIndex = input.getIntOr(STAFF_VARIANT_KEY, 0);
        ownerUuid = input.getString(OWNER_KEY).map(UUID::fromString).orElse(null);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveCustomOnly(provider);
    }

    private void markUpdated() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
