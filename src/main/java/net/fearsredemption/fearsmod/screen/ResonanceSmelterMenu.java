package net.fearsredemption.fearsmod.screen;

import net.fearsredemption.fearsmod.block.ModBlocks;
import net.fearsredemption.fearsmod.block.entity.ResonanceSmelterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ResonanceSmelterMenu extends AbstractContainerMenu {
    private static final int CONTAINER_SIZE = 7;
    private static final int DATA_SIZE = 8;
    private static final int PLAYER_INVENTORY_START = CONTAINER_SIZE;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 36;

    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public ResonanceSmelterMenu(int containerId, Inventory inventory, BlockPos pos) {
        this(containerId, inventory, containerFor(inventory, pos), dataFor(inventory, pos), ContainerLevelAccess.create(inventory.player.level(), pos));
    }

    public ResonanceSmelterMenu(int containerId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(ModMenus.RESONANCE_SMELTER, containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_SIZE);
        this.container = container;
        this.data = data;
        this.access = access;

        addSlot(new Slot(container, ResonanceSmelterBlockEntity.INPUT_0, 44, 22));
        addSlot(new Slot(container, ResonanceSmelterBlockEntity.INPUT_1, 44, 44));
        addSlot(new Slot(container, ResonanceSmelterBlockEntity.INPUT_2, 44, 66));
        addSlot(new FuelSlot(container, ResonanceSmelterBlockEntity.FUEL, 18, 44));
        addSlot(new OutputSlot(container, ResonanceSmelterBlockEntity.OUTPUT_0, 118, 22));
        addSlot(new OutputSlot(container, ResonanceSmelterBlockEntity.OUTPUT_1, 118, 44));
        addSlot(new OutputSlot(container, ResonanceSmelterBlockEntity.OUTPUT_2, 118, 66));

        addStandardInventorySlots(inventory, 8, 98);
        addDataSlots(data);
    }

    public boolean isLit() {
        return data.get(0) > 0;
    }

    public int litProgress() {
        int burnTime = data.get(0);
        int burnDuration = data.get(1);
        if (burnDuration <= 0) {
            burnDuration = 200;
        }
        return burnTime * 13 / burnDuration;
    }

    public int cookProgress(int lane) {
        int progress = data.get(2 + lane);
        int total = data.get(5 + lane);
        if (total <= 0 || progress <= 0) {
            return 0;
        }
        return progress * 24 / total;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return original;
        }

        ItemStack stack = slot.getItem();
        original = stack.copy();
        if (index < CONTAINER_SIZE) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (isFuel(stack)) {
            if (!moveItemStackTo(stack, ResonanceSmelterBlockEntity.FUEL, ResonanceSmelterBlockEntity.FUEL + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, ResonanceSmelterBlockEntity.INPUT_0, ResonanceSmelterBlockEntity.INPUT_2 + 1, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(access, player, ModBlocks.RESONANCE_SMELTER);
    }

    private boolean isFuel(ItemStack stack) {
        return access.evaluate((level, pos) -> level.fuelValues().isFuel(stack), false);
    }

    private static Container containerFor(Inventory inventory, BlockPos pos) {
        if (inventory.player.level().getBlockEntity(pos) instanceof ResonanceSmelterBlockEntity smelter) {
            return smelter;
        }
        return new SimpleContainer(CONTAINER_SIZE);
    }

    private static ContainerData dataFor(Inventory inventory, BlockPos pos) {
        if (inventory.player.level().getBlockEntity(pos) instanceof ResonanceSmelterBlockEntity smelter) {
            return smelter.data();
        }
        return new SimpleContainerData(DATA_SIZE);
    }

    private final class FuelSlot extends Slot {
        private FuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return ResonanceSmelterMenu.this.isFuel(stack);
        }
    }

    private static final class OutputSlot extends Slot {
        private OutputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
