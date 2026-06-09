package net.fearsredemption.fearsmod.journal;

import java.util.ArrayList;
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
            new Discovery("voxite_ore", player -> has(player, MobBlocks.VOXITE_ORE.asItem()) || has(player, MobBlocks.DEEPSLATE_VOXITE_ORE.asItem()) || has(player, ModItems.RAW_VOXITE)),
            new Discovery("magitek_ore", player -> has(player, MobBlocks.MAGITEK_ORE.asItem()) || has(player, MobBlocks.DEEPSLATE_MAGITEK_ORE.asItem()) || has(player, ModItems.RAW_MAGITEK)),
            new Discovery("voxite_ingot", player -> has(player, ModItems.VOXITE_INGOT)),
            new Discovery("magitek_ingot", player -> has(player, ModItems.MAGITEK_INGOT)),
            new Discovery("combined_ingots", player -> hasTag(player, "voxite_ingot") && hasTag(player, "magitek_ingot")),
            new Discovery("resonance_workbench", player -> has(player, MobBlocks.RESONANCE_WORKBENCH.asItem())),
            new Discovery("power_blocks", player -> has(player, MobBlocks.VOXITE_BLOCK.asItem()) && has(player, MobBlocks.MAGITEK_BLOCK.asItem())),
            new Discovery("starter_structure", player -> hasTag(player, "resonance_workbench") && has(player, MobBlocks.VOXITE_BLOCK.asItem()) && has(player, MobBlocks.MAGITEK_BLOCK.asItem())),
            new Discovery("ritual_basics", player -> hasTag(player, "resonance_workbench") && has(player, Items.REDSTONE)),
            new Discovery("staff_ritual", player -> has(player, Items.STICK) && has(player, ModItems.VOXITE_INGOT) && has(player, ModItems.MAGITEK_INGOT) && hasAnyShard(player)),
            new Discovery("first_staff", JournalUnlocks::hasAnyStaff),
            new Discovery("apparatus_rituals", player -> hasTag(player, "first_staff") || has(player, ModItems.FOCUSING_LENS)),
            new Discovery("stack_handling", player -> hasTag(player, "ritual_basics")),
            new Discovery("crystal_growth", JournalUnlocks::hasAnyModShard),
            new Discovery("future", player -> hasTag(player, "first_staff"))
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

        notifyJournalUpdate(player);
    }

    public static void refresh(ServerPlayer player) {
        update(player);
    }

    public static List<String> unlockedPageIds(ServerPlayer player) {
        List<String> ids = new ArrayList<>();
        for (String tag : player.entityTags()) {
            if (tag.startsWith(TAG_PREFIX)) {
                ids.add(tag.substring(TAG_PREFIX.length()));
            }
        }
        return ids;
    }

    private static void notifyJournalUpdate(ServerPlayer player) {
        player.sendOverlayMessage(Component.translatable("item.fearsmod.resonance_journal.note"));
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 0.65F, 1.1F);
        player.playSound(SoundEvents.BRUSH_GENERIC, 0.35F, 1.25F);
        JournalNetworking.sync(player);
    }

    private static void update(ServerPlayer player) {
        if (!has(player, ModItems.RESONANCE_JOURNAL)) {
            return;
        }

        boolean changed = addTag(player, "start");
        for (Discovery discovery : DISCOVERIES) {
            if (discovery.condition().matches(player)) {
                changed |= addTag(player, discovery.tag());
            }
        }

        if (changed) {
            notifyJournalUpdate(player);
        }
    }

    private static boolean addTag(ServerPlayer player, String id) {
        return player.addTag(TAG_PREFIX + id);
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

    private record Discovery(String tag, Condition condition) {
    }

    @FunctionalInterface
    private interface Condition {
        boolean matches(ServerPlayer player);
    }
}
