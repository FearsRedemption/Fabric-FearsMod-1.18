package net.fearsredemption.fearsmod.block.custom;

import net.fearsredemption.fearsmod.block.MobBlocks;
import net.fearsredemption.fearsmod.block.entity.ResonanceSocketBlockEntity;
import net.fearsredemption.fearsmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ResonanceWorkbenchBlock extends Block {
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
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (stack.isEmpty()) {
            reportSetupStatus(level, pos, player);
            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() == ModItems.DOWSING_ROD) {
            return activateWithStaff(level, pos, player, stack, hand);
        }

        player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.place_on_sockets"));
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
            player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.pattern_incomplete", pattern.missingSlots().size()));
            pattern.missingSlots().stream().findFirst().ifPresent(slot -> player.sendSystemMessage(Component.translatable(
                    "block.fearsmod.resonance_workbench.pattern_missing",
                    slot.type().name(),
                    slot.offset().getX(),
                    slot.offset().getZ()
            )));
            return InteractionResult.SUCCESS;
        }

        RecipeMatch match = findRecipe(pattern.sockets());
        if (match == null) {
            player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.no_socket_recipe"));
            return InteractionResult.SUCCESS;
        }

        match.consumedSockets().forEach(socket -> socket.removeStoredItem());
        finishRecipe(level, pos, player, match.recipe().output());
        if (!player.isCreative()) {
            staffStack.hurtAndBreak(1, player, hand);
        }

        return InteractionResult.SUCCESS;
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
        player.sendSystemMessage(Component.translatable(
                "block.fearsmod.resonance_workbench.status",
                pattern.complete(),
                socketedItems,
                pattern.missingSlots().size()
        ));
    }

    private static void finishRecipe(Level level, BlockPos pos, Player player, Item outputItem) {
        ItemStack outputStack = new ItemStack(outputItem);
        Component outputName = outputStack.getDisplayName();
        if (!player.addItem(outputStack)) {
            player.drop(outputStack, false);
        }

        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0f, 0.9f);
        level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.35f, 1.6f);
        player.sendSystemMessage(Component.translatable("block.fearsmod.resonance_workbench.complete", outputName));
    }

    private static Block expectedBlock(ResonanceSocketType type) {
        return switch (type) {
            case CORE -> MobBlocks.MAGITEK_CORE;
            case STABILIZER -> MobBlocks.VOXITE_STABILIZER;
            case FOCUS -> MobBlocks.AMETHYST_FOCUS;
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
}
