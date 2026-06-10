package net.fearsredemption.fearsmod.block.entity;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fearsredemption.fearsmod.screen.ResonanceSmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ResonanceSmelterBlockEntity extends BaseContainerBlockEntity implements ExtendedMenuProvider<BlockPos> {
    public static final int INPUT_0 = 0;
    public static final int INPUT_1 = 1;
    public static final int INPUT_2 = 2;
    public static final int FUEL = 3;
    public static final int OUTPUT_0 = 4;
    public static final int OUTPUT_1 = 5;
    public static final int OUTPUT_2 = 6;
    public static final int SMELT_TIME = 160;

    private static final String BURN_TIME_KEY = "burn_time";
    private static final String BURN_DURATION_KEY = "burn_duration";
    private static final String PROGRESS_KEY = "progress";

    private NonNullList<ItemStack> items = NonNullList.withSize(7, ItemStack.EMPTY);
    private int burnTime;
    private int burnDuration;
    private final int[] progress = new int[3];

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnDuration;
                case 2 -> progress[0];
                case 3 -> progress[1];
                case 4 -> progress[2];
                case 5, 6, 7 -> SMELT_TIME;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnDuration = value;
                case 2 -> progress[0] = value;
                case 3 -> progress[1] = value;
                case 4 -> progress[2] = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public ResonanceSmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_SMELTER, pos, state);
    }

    public ContainerData data() {
        return data;
    }

    @Override
    public BlockPos getScreenOpeningData(net.minecraft.server.level.ServerPlayer player) {
        return worldPosition;
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, ResonanceSmelterBlockEntity smelter) {
        boolean changed = false;
        boolean hasWork = false;

        for (int lane = 0; lane < 3; lane++) {
            if (smelter.canSmelt(level, lane)) {
                hasWork = true;
                break;
            }
        }

        if (smelter.burnTime <= 0 && hasWork) {
            changed |= smelter.consumeFuel(level);
        }

        if (smelter.burnTime > 0) {
            smelter.burnTime--;
            changed = true;
            for (int lane = 0; lane < 3; lane++) {
                if (smelter.canSmelt(level, lane)) {
                    smelter.progress[lane]++;
                    if (smelter.progress[lane] >= SMELT_TIME) {
                        smelter.progress[lane] = 0;
                        smelter.smeltLane(level, lane);
                    }
                } else if (smelter.progress[lane] != 0) {
                    smelter.progress[lane] = 0;
                }
            }
        } else {
            for (int lane = 0; lane < 3; lane++) {
                if (smelter.progress[lane] != 0) {
                    smelter.progress[lane] = 0;
                    changed = true;
                }
            }
        }

        if (changed) {
            smelter.setChanged();
        }
    }

    private boolean consumeFuel(ServerLevel level) {
        ItemStack fuel = items.get(FUEL);
        int duration = level.fuelValues().burnDuration(fuel);
        if (duration <= 0) {
            return false;
        }

        burnTime = duration;
        burnDuration = duration;
        if (fuel.getItem() == Items.LAVA_BUCKET) {
            items.set(FUEL, new ItemStack(Items.BUCKET));
        } else {
            fuel.shrink(1);
            if (fuel.isEmpty()) {
                items.set(FUEL, ItemStack.EMPTY);
            }
        }

        return true;
    }

    private boolean canSmelt(ServerLevel level, int lane) {
        ItemStack input = items.get(INPUT_0 + lane);
        if (input.isEmpty()) {
            return false;
        }

        ItemStack result = smeltResult(level, input);
        if (result.isEmpty()) {
            return false;
        }

        ItemStack output = items.get(OUTPUT_0 + lane);
        if (output.isEmpty()) {
            return true;
        }

        return ItemStack.isSameItemSameComponents(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private void smeltLane(ServerLevel level, int lane) {
        ItemStack result = smeltResult(level, items.get(INPUT_0 + lane));
        if (result.isEmpty()) {
            return;
        }

        ItemStack output = items.get(OUTPUT_0 + lane);
        if (output.isEmpty()) {
            items.set(OUTPUT_0 + lane, result.copy());
        } else if (ItemStack.isSameItemSameComponents(output, result)) {
            output.grow(result.getCount());
        }

        items.get(INPUT_0 + lane).shrink(1);
        if (items.get(INPUT_0 + lane).isEmpty()) {
            items.set(INPUT_0 + lane, ItemStack.EMPTY);
        }
    }

    private ItemStack smeltResult(ServerLevel level, ItemStack input) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        return level.getServer().getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, recipeInput, level)
                .map(RecipeHolder<SmeltingRecipe>::value)
                .filter(recipe -> recipe.matches(recipeInput, level))
                .map(recipe -> recipe.assemble(recipeInput))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == FUEL) {
            return level != null && level.fuelValues().isFuel(stack);
        }

        return slot >= INPUT_0 && slot <= INPUT_2;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.fearsmod.resonance_smelter");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ResonanceSmelterMenu(containerId, inventory, this, data, net.minecraft.world.inventory.ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        burnTime = input.getIntOr(BURN_TIME_KEY, 0);
        burnDuration = input.getIntOr(BURN_DURATION_KEY, 0);
        int[] savedProgress = input.getIntArray(PROGRESS_KEY).orElse(new int[0]);
        for (int i = 0; i < progress.length && i < savedProgress.length; i++) {
            progress[i] = savedProgress[i];
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt(BURN_TIME_KEY, burnTime);
        output.putInt(BURN_DURATION_KEY, burnDuration);
        output.putIntArray(PROGRESS_KEY, progress);
    }
}
