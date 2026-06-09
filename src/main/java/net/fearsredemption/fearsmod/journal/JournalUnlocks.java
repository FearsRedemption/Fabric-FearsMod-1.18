package net.fearsredemption.fearsmod.journal;

import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fearsredemption.fearsmod.block.MobBlocks;
import net.fearsredemption.fearsmod.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class JournalUnlocks {
    public static final String TAG_PREFIX = "fearsmod.journal.";

    private static final List<Discovery> DISCOVERIES = List.of(
            new Discovery("voxite_ore", "voxite_ore", player -> has(player, MobBlocks.VOXITE_ORE.asItem()) || has(player, MobBlocks.DEEPSLATE_VOXITE_ORE.asItem()) || has(player, ModItems.RAW_VOXITE)),
            new Discovery("magitek_ore", "magitek_ore", player -> has(player, MobBlocks.MAGITEK_ORE.asItem()) || has(player, MobBlocks.DEEPSLATE_MAGITEK_ORE.asItem()) || has(player, ModItems.RAW_MAGITEK)),
            new Discovery("voxite_ingot", "voxite_ingot", player -> has(player, ModItems.VOXITE_INGOT)),
            new Discovery("magitek_ingot", "magitek_ingot", player -> has(player, ModItems.MAGITEK_INGOT)),
            new Discovery("combined_ingots", "combined_ingots", player -> has(player, ModItems.VOXITE_INGOT) && has(player, ModItems.MAGITEK_INGOT)),
            new Discovery("resonance_workbench", "resonance_workbench", player -> has(player, MobBlocks.RESONANCE_WORKBENCH.asItem())),
            new Discovery("power_blocks", "power_blocks", player -> has(player, MobBlocks.VOXITE_BLOCK.asItem()) && has(player, MobBlocks.MAGITEK_BLOCK.asItem())),
            new Discovery("starter_structure", "starter_structure", player -> has(player, MobBlocks.RESONANCE_WORKBENCH.asItem()) && has(player, MobBlocks.VOXITE_BLOCK.asItem()) && has(player, MobBlocks.MAGITEK_BLOCK.asItem())),
            new Discovery("ritual_basics", "ritual_basics", player -> has(player, Items.REDSTONE) && has(player, MobBlocks.RESONANCE_WORKBENCH.asItem())),
            new Discovery("staff_ritual", "staff_ritual", player -> has(player, Items.STICK) && has(player, ModItems.VOXITE_INGOT) && has(player, ModItems.MAGITEK_INGOT) && hasAnyShard(player)),
            new Discovery("first_staff", "first_staff", JournalUnlocks::hasAnyStaff),
            new Discovery("apparatus_rituals", "apparatus_rituals", player -> hasAnyStaff(player) || has(player, ModItems.FOCUSING_LENS)),
            new Discovery("stack_handling", "stack_handling", player -> hasTag(player, "ritual_basics")),
            new Discovery("crystal_growth", "crystal_growth", JournalUnlocks::hasAnyModShard),
            new Discovery("future", "future", player -> hasTag(player, "first_staff"))
    );

    private JournalUnlocks() {
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> server.getPlayerList().getPlayers().forEach(JournalUnlocks::update));
    }

    public static boolean hasTag(ServerPlayer player, String id) {
        return player.entityTags().contains(TAG_PREFIX + id);
    }

    public static void unlock(ServerPlayer player, String id) {
        if (!player.addTag(TAG_PREFIX + id)) {
            return;
        }

        player.sendOverlayMessage(Component.translatable("item.fearsmod.resonance_journal.note"));
        if (isHoldingJournal(player)) {
            player.playSound(SoundEvents.BOOK_PAGE_TURN, 0.8F, 1.1F);
            player.playSound(SoundEvents.BRUSH_GENERIC, 0.45F, 1.25F);
        }
    }

    private static void update(ServerPlayer player) {
        if (!has(player, ModItems.RESONANCE_JOURNAL)) {
            return;
        }

        unlock(player, "start");
        for (Discovery discovery : DISCOVERIES) {
            if (discovery.condition().matches(player)) {
                unlock(player, discovery.tag());
            }
        }
    }

    private static boolean isHoldingJournal(ServerPlayer player) {
        return player.getMainHandItem().getItem() == ModItems.RESONANCE_JOURNAL
                || player.getOffhandItem().getItem() == ModItems.RESONANCE_JOURNAL;
    }

    private static boolean has(ServerPlayer player, Item item) {
        return player.getInventory().contains(stack -> stack.getItem() == item);
    }

    private static boolean hasAnyShard(ServerPlayer player) {
        return has(player, Items.AMETHYST_SHARD) || hasAnyModShard(player);
    }

    private static boolean hasAnyModShard(ServerPlayer player) {
        return has(player, ModItems.AGATE_SHARD)
                || has(player, ModItems.AMBER_SHARD)
                || has(player, ModItems.AQUAMARINE_SHARD)
                || has(player, ModItems.RUBY_SHARD)
                || has(player, ModItems.TOPAZ_SHARD);
    }

    private static boolean hasAnyStaff(ServerPlayer player) {
        return player.getInventory().contains(ModItems::isResonanceStaff);
    }

    private record Discovery(String tag, String page, Condition condition) {
    }

    @FunctionalInterface
    private interface Condition {
        boolean matches(ServerPlayer player);
    }
}
