package net.fearsredemption.fearsmod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ResonanceSocketBlockEntity extends BlockEntity {
    private static final String STORED_ITEM_KEY = "stored_item";

    private ItemStack storedItem = ItemStack.EMPTY;

    public ResonanceSocketBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_SOCKET, pos, state);
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public boolean isEmpty() {
        return storedItem.isEmpty();
    }

    public void setStoredItem(ItemStack stack) {
        storedItem = stack.copyWithCount(stack.getCount());
        markUpdated();
    }

    public ItemStack removeStoredItem() {
        ItemStack removed = storedItem;
        storedItem = ItemStack.EMPTY;
        markUpdated();
        return removed;
    }

    public boolean containsItem(net.minecraft.world.item.Item item) {
        return !storedItem.isEmpty() && storedItem.getItem() == item;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!storedItem.isEmpty()) {
            output.store(STORED_ITEM_KEY, ItemStack.OPTIONAL_CODEC, storedItem);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedItem = input.read(STORED_ITEM_KEY, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
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
