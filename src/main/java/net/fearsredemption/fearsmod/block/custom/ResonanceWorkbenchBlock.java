package net.fearsredemption.fearsmod.block.custom;

import net.fearsredemption.fearsmod.block.ModBlocks;
import net.fearsredemption.fearsmod.block.entity.ModBlockEntities;
import net.fearsredemption.fearsmod.block.entity.ResonanceSocketBlockEntity;
import net.fearsredemption.fearsmod.block.entity.ResonanceWorkbenchBlockEntity;
import net.fearsredemption.fearsmod.item.ModItems;
import net.fearsredemption.fearsmod.journal.JournalUnlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ResonanceWorkbenchBlock extends Block implements EntityBlock {
    public static final int RITUAL_STAFF = 0;
    public static final int RITUAL_AMETHYST_FOCUS = 1;
    public static final int RITUAL_MAGITEK_CORE = 2;
    public static final int RITUAL_VOXITE_STABILIZER = 3;

    private static final List<PatternSlot> APPARATUS_PATTERN = createPattern();
    private static final RecipeDefinition[] RECIPES = {
            new RecipeDefinition(
                    ModItems.CHARGED_MAGITEK_CORE,
                    List.of(
                            new Requirement(ResonanceSocketType.CORE, ModItems.MAGITEK_INGOT),
                            new Requirement(ResonanceSocketType.FOCUS, Items.AMETHYST_SHARD)
                    )
            ),
            new RecipeDefinition(
                    ModItems.STABILIZED_IRON_PLATE,
                    List.of(new Requirement(ResonanceSocketType.STABILIZER, Items.IRON_INGOT))
            ),
            new RecipeDefinition(
                    ModItems.RESONANT_COPPER,
                    List.of(new Requirement(ResonanceSocketType.FOCUS, Items.COPPER_INGOT))
            )
    };

    public ResonanceWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResonanceWorkbenchBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide() || blockEntityType != ModBlockEntities.RESONANCE_WORKBENCH) {
            return null;
        }

        return (tickLevel, tickPos, tickState, blockEntity) ->
                ResonanceWorkbenchBlockEntity.serverTick((ServerLevel) tickLevel, tickPos, tickState, (ResonanceWorkbenchBlockEntity) blockEntity);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (stack.isEmpty()) {
            reportSetupStatus(level, pos, player);
            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() == Items.REDSTONE) {
            return startRitual(level, pos, player, stack);
        }

        if (ModItems.isResonanceStaff(stack)) {
            return activateWithStaff(level, pos, player, stack, hand);
        }

        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_workbench.place_on_sockets"));
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult startRitual(Level level, BlockPos pos, Player player, ItemStack redstoneStack) {
        if (!(level instanceof ServerLevel serverLevel) || !(level.getBlockEntity(pos) instanceof ResonanceWorkbenchBlockEntity workbench)) {
            return InteractionResult.SUCCESS;
        }

        if (workbench.isRitualActive()) {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_workbench.ritual_active"));
            return InteractionResult.SUCCESS;
        }

        if (!hasRitualStructure(level, pos)) {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_workbench.starter_structure_invalid"));
            return InteractionResult.SUCCESS;
        }

        RitualPlan plan = findRitualPlan(serverLevel, pos);
        if (plan == null) {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_workbench.ritual_no_recipe"));
            return InteractionResult.SUCCESS;
        }

        if (!player.isCreative()) {
            redstoneStack.shrink(1);
        }

        protectRitualStacks(serverLevel, pos, ingredientsFor(plan.recipeIndex(), plan.output()));
        workbench.startRitual(player, plan.recipeIndex(), plan.output());
        serverLevel.sendParticles(new DustParticleOptions(0xF04B45, 1.1F), pos.getX() + 0.5D, pos.getY() + 1.15D, pos.getZ() + 0.5D, 20, 0.35D, 0.25D, 0.35D, 0.03D);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, 10, 0.25D, 0.2D, 0.25D, 0.02D);
        level.playSound(null, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.55F, 1.6F);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.75F, 1.0F);
        if (player instanceof ServerPlayer serverPlayer) {
            JournalUnlocks.unlock(serverPlayer, "ritual_basics");
            JournalUnlocks.unlock(serverPlayer, "stack_handling");
        }
        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_workbench.ritual_started", new ItemStack(plan.output()).getDisplayName()));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            reportSetupStatus(level, pos, player);
        }

        return InteractionResult.SUCCESS;
    }

    public static InteractionResult activateWithStaff(Level level, BlockPos pos, Player player, ItemStack staffStack, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        PatternState pattern = inspectPattern(level, pos);
        if (!pattern.complete()) {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_workbench.pattern_incomplete", pattern.missingSlots().size()));
            return InteractionResult.SUCCESS;
        }

        RecipeMatch match = findRecipe(pattern.sockets());
        if (match == null) {
            player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_workbench.no_socket_recipe"));
            return InteractionResult.SUCCESS;
        }

        match.consumedSockets().forEach(socket -> socket.removeStoredItem());
        finishRecipe(level, pos, player, match.recipe().output());
        if (player instanceof ServerPlayer serverPlayer) {
            JournalUnlocks.unlock(serverPlayer, "apparatus_rituals");
        }

        return InteractionResult.SUCCESS;
    }

    public static InteractionResult activateNearestWithStaff(Level level, BlockPos origin, Player player, ItemStack staffStack, InteractionHand hand) {
        for (BlockPos candidate : BlockPos.betweenClosed(origin.offset(-8, -4, -8), origin.offset(8, 4, 8))) {
            if (level.getBlockState(candidate).getBlock() == ModBlocks.RESONANCE_WORKBENCH) {
                return activateWithStaff(level, candidate.immutable(), player, staffStack, hand);
            }
        }

        player.sendOverlayMessage(Component.translatable("item.fearsmod.resonance_staff.no_apparatus"));
        return InteractionResult.SUCCESS;
    }

    public static boolean hasRitualStructure(Level level, BlockPos pos) {
        return level.getBlockState(pos.north()).getBlock() == ModBlocks.VOXITE_BLOCK
                && level.getBlockState(pos.south()).getBlock() == ModBlocks.VOXITE_BLOCK
                && level.getBlockState(pos.east()).getBlock() == ModBlocks.MAGITEK_BLOCK
                && level.getBlockState(pos.west()).getBlock() == ModBlocks.MAGITEK_BLOCK;
    }

    public static ItemEntity findNearbyItemEntity(Level level, BlockPos pos, Item item) {
        if (item == null) {
            return null;
        }

        AABB searchBox = new AABB(pos).inflate(2.0D, 1.5D, 2.0D);
        return level.getEntitiesOfClass(ItemEntity.class, searchBox, entity ->
                        !entity.isRemoved()
                                && !entity.getItem().isEmpty()
                                && entity.getItem().getItem() == item)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public static List<Item> ingredientsFor(int recipeIndex, Item output) {
        if (recipeIndex == RITUAL_STAFF) {
            return List.of(Items.STICK, ModItems.VOXITE_INGOT, ModItems.MAGITEK_INGOT, ModItems.shardForStaff(output));
        }
        if (recipeIndex == RITUAL_AMETHYST_FOCUS) {
            return List.of(Items.AMETHYST_SHARD, Items.COPPER_INGOT, ModItems.FOCUSING_LENS, ModItems.VOXITE_INGOT);
        }
        if (recipeIndex == RITUAL_MAGITEK_CORE) {
            return List.of(Items.COPPER_INGOT, Items.AMETHYST_SHARD, ModItems.MAGITEK_INGOT);
        }
        if (recipeIndex == RITUAL_VOXITE_STABILIZER) {
            return List.of(ModItems.VOXITE_INGOT, Items.AMETHYST_SHARD);
        }

        return List.of();
    }

    public static int colorForRitualItem(Item item) {
        if (item == Items.STICK) {
            return 0x8B5A2B;
        }
        if (item == ModItems.VOXITE_INGOT) {
            return 0xE8F0EC;
        }
        if (item == ModItems.MAGITEK_INGOT) {
            return 0xA843B8;
        }
        if (item == Items.AMETHYST_SHARD) {
            return 0xB987FF;
        }
        if (item == ModItems.AGATE_SHARD) {
            return 0xC9D5D0;
        }
        if (item == ModItems.AMBER_SHARD) {
            return 0xF3A033;
        }
        if (item == ModItems.AQUAMARINE_SHARD) {
            return 0x63D7E3;
        }
        if (item == ModItems.RUBY_SHARD) {
            return 0xE94A6E;
        }
        if (item == ModItems.TOPAZ_SHARD) {
            return 0xE7D253;
        }

        return 0xD8B4FF;
    }

    private static RitualPlan findRitualPlan(ServerLevel level, BlockPos pos) {
        Item shard = findStarterShard(level, pos);
        Item staffItem = shard == null ? null : ModItems.staffForShard(shard);
        if (staffItem != null && hasRitualIngredients(level, pos, ingredientsFor(RITUAL_STAFF, staffItem))) {
            return new RitualPlan(RITUAL_STAFF, staffItem);
        }

        if (hasRitualIngredients(level, pos, ingredientsFor(RITUAL_AMETHYST_FOCUS, ModBlocks.AMETHYST_FOCUS.asItem()))) {
            return new RitualPlan(RITUAL_AMETHYST_FOCUS, ModBlocks.AMETHYST_FOCUS.asItem());
        }

        if (hasRitualIngredients(level, pos, ingredientsFor(RITUAL_MAGITEK_CORE, ModBlocks.MAGITEK_CORE.asItem()))) {
            return new RitualPlan(RITUAL_MAGITEK_CORE, ModBlocks.MAGITEK_CORE.asItem());
        }

        if (hasRitualIngredients(level, pos, ingredientsFor(RITUAL_VOXITE_STABILIZER, ModBlocks.VOXITE_STABILIZER.asItem()))) {
            return new RitualPlan(RITUAL_VOXITE_STABILIZER, ModBlocks.VOXITE_STABILIZER.asItem());
        }

        return null;
    }

    private static boolean hasRitualIngredients(ServerLevel level, BlockPos pos, List<Item> ingredients) {
        return ingredients.stream().allMatch(item -> findNearbyItemEntity(level, pos, item) != null);
    }

    private static void protectRitualStacks(ServerLevel level, BlockPos pos, List<Item> ingredients) {
        for (Item ingredient : ingredients) {
            ItemEntity entity = findNearbyItemEntity(level, pos, ingredient);
            if (entity != null) {
                entity.setExtendedLifetime();
            }
        }
    }

    private static Item findStarterShard(ServerLevel level, BlockPos pos) {
        Item[] validShards = {
                Items.AMETHYST_SHARD,
                ModItems.AGATE_SHARD,
                ModItems.AMBER_SHARD,
                ModItems.AQUAMARINE_SHARD,
                ModItems.RUBY_SHARD,
                ModItems.TOPAZ_SHARD
        };

        for (Item shard : validShards) {
            if (findNearbyItemEntity(level, pos, shard) != null) {
                return shard;
            }
        }

        return null;
    }

    private static PatternState inspectPattern(Level level, BlockPos workbenchPos) {
        List<PatternSlot> missingSlots = new ArrayList<>();
        List<SocketRef> sockets = new ArrayList<>();

        for (PatternSlot slot : APPARATUS_PATTERN) {
            BlockPos socketPos = workbenchPos.offset(slot.offset());
            Block expectedBlock = expectedBlock(slot.type());
            Block actualBlock = level.getBlockState(socketPos).getBlock();
            if (actualBlock != expectedBlock) {
                missingSlots.add(slot);
                continue;
            }

            if (level.getBlockEntity(socketPos) instanceof ResonanceSocketBlockEntity socket) {
                sockets.add(new SocketRef(slot.type(), socketPos, socket));
            }
        }

        return new PatternState(missingSlots.isEmpty(), sockets, missingSlots);
    }

    private static RecipeMatch findRecipe(List<SocketRef> sockets) {
        for (RecipeDefinition recipe : RECIPES) {
            Set<ResonanceSocketBlockEntity> usedSockets = new HashSet<>();
            List<ResonanceSocketBlockEntity> consumedSockets = new ArrayList<>();
            boolean matches = true;

            for (Requirement requirement : recipe.requirements()) {
                Optional<SocketRef> socket = findSocket(sockets, requirement, usedSockets);
                if (socket.isEmpty()) {
                    matches = false;
                    break;
                }

                usedSockets.add(socket.get().socket());
                consumedSockets.add(socket.get().socket());
            }

            if (matches) {
                return new RecipeMatch(recipe, consumedSockets);
            }
        }

        return null;
    }

    private static Optional<SocketRef> findSocket(List<SocketRef> sockets, Requirement requirement, Set<ResonanceSocketBlockEntity> usedSockets) {
        return sockets.stream()
                .filter(socket -> socket.type() == requirement.type())
                .filter(socket -> !usedSockets.contains(socket.socket()))
                .filter(socket -> socket.socket().containsItem(requirement.item()))
                .findFirst();
    }

    private static void reportSetupStatus(Level level, BlockPos pos, Player player) {
        PatternState pattern = inspectPattern(level, pos);
        long socketedItems = pattern.sockets().stream().filter(socket -> !socket.socket().isEmpty()).count();
        player.sendOverlayMessage(Component.translatable(
                "block.fearsmod.resonance_workbench.status",
                pattern.complete(),
                socketedItems,
                pattern.missingSlots().size()
        ));
    }

    private static void finishRecipe(Level level, BlockPos pos, Player player, Item outputItem) {
        ItemStack outputStack = new ItemStack(outputItem);
        Component outputName = outputStack.getDisplayName();
        if (!level.isClientSide()) {
            spawnRitualOutput(level, pos, outputStack);
        }

        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0f, 0.9f);
        level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.35f, 1.6f);
        player.sendOverlayMessage(Component.translatable("block.fearsmod.resonance_workbench.complete", outputName));
    }

    private static Block expectedBlock(ResonanceSocketType type) {
        return switch (type) {
            case CORE -> ModBlocks.MAGITEK_CORE;
            case STABILIZER -> ModBlocks.VOXITE_STABILIZER;
            case FOCUS -> ModBlocks.AMETHYST_FOCUS;
        };
    }

    private static List<PatternSlot> createPattern() {
        String[] rows = {
                "XXXFCFXXX",
                "XXSXXXSXX",
                "XSXXXXXSX",
                "FXXXXXXXF",
                "CXXXBXXXC",
                "FXXXXXXXF",
                "XSXXXXXSX",
                "XXSXXXSXX",
                "XXXFCFXXX"
        };

        List<PatternSlot> slots = new ArrayList<>();
        for (int z = 0; z < rows.length; z++) {
            String row = rows[z];
            for (int x = 0; x < row.length(); x++) {
                ResonanceSocketType type = switch (row.charAt(x)) {
                    case 'C' -> ResonanceSocketType.CORE;
                    case 'S' -> ResonanceSocketType.STABILIZER;
                    case 'F' -> ResonanceSocketType.FOCUS;
                    default -> null;
                };

                if (type != null) {
                    slots.add(new PatternSlot(type, new BlockPos(x - 4, 0, z - 4)));
                }
            }
        }

        return slots;
    }

    private record PatternSlot(ResonanceSocketType type, BlockPos offset) {
    }

    private record SocketRef(ResonanceSocketType type, BlockPos pos, ResonanceSocketBlockEntity socket) {
    }

    private record PatternState(boolean complete, List<SocketRef> sockets, List<PatternSlot> missingSlots) {
    }

    private record Requirement(ResonanceSocketType type, Item item) {
    }

    private record RecipeDefinition(Item output, List<Requirement> requirements) {
    }

    private record RecipeMatch(RecipeDefinition recipe, List<ResonanceSocketBlockEntity> consumedSockets) {
    }

    private record RitualPlan(int recipeIndex, Item output) {
    }

    public static void spawnRitualOutput(Level level, BlockPos pos, ItemStack outputStack) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.35D, pos.getZ() + 0.5D, outputStack);
        RandomSource random = RandomSource.create();
        double dx = (random.nextDouble() - 0.5D) * 0.08D;
        double dz = (random.nextDouble() - 0.5D) * 0.08D;
        entity.setDeltaMovement(dx, 0.22D, dz);
        level.addFreshEntity(entity);
    }
}
