package net.fearsredemption.fearsmod.client.journal;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fearsredemption.fearsmod.FearsMod;
import net.fearsredemption.fearsmod.block.MobBlocks;
import net.fearsredemption.fearsmod.item.ModItems;
import net.fearsredemption.fearsmod.journal.JournalNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class ResonanceJournalClient {
    private static final Identifier PAGES = Identifier.fromNamespaceAndPath(FearsMod.MOD_ID, "resonance_journal/pages.json");
    private static final Gson GSON = new Gson();

    private ResonanceJournalClient() {
    }

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(JournalNetworking.OpenJournalPayload.TYPE, (payload, context) ->
                context.client().execute(() -> receive(payload.unlockedPages(), payload.openJournal())));

        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!level.isClientSide() || player.getItemInHand(hand).getItem() != ModItems.RESONANCE_JOURNAL) {
                return InteractionResult.PASS;
            }

            ClientPlayNetworking.send(JournalNetworking.OpenJournalRequestPayload.INSTANCE);
            return InteractionResult.SUCCESS;
        });
    }

    private static void receive(List<String> unlockedPages, boolean openJournal) {
        Set<String> unlocked = new HashSet<>(unlockedPages);
        Minecraft minecraft = Minecraft.getInstance();
        List<JournalPage> pages = buildPages(unlocked);
        if (minecraft.screen instanceof ResonanceJournalScreen screen) {
            screen.setPages(pages);
        } else if (openJournal) {
            minecraft.setScreen(new ResonanceJournalScreen(pages));
        }
    }

    private static List<JournalPage> buildPages(Set<String> unlocked) {
        inferInventoryUnlocks(unlocked);
        List<JournalPage> pages = new ArrayList<>();
        for (JournalEntry entry : loadEntries()) {
            boolean visible = entry.alwaysVisible() || unlocked.contains(entry.unlock());
            pages.add(new JournalPage(
                    visible ? entry.title() : "Undiscovered Entry",
                    visible ? entry.text() : "The notes here are incomplete. I need to discover more before this makes sense.",
                    visible
            ));
        }

        if (pages.isEmpty()) {
            pages.add(new JournalPage(
                    "Resonance Journal",
                    "I started this journal because the materials I keep finding do not behave like normal stone or metal.",
                    true
            ));
        }
        return pages;
    }

    private static void inferInventoryUnlocks(Set<String> unlocked) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (has(player, MobBlocks.VOXITE_ORE.asItem()) || has(player, MobBlocks.DEEPSLATE_VOXITE_ORE.asItem()) || has(player, ModItems.RAW_VOXITE)) {
            unlocked.add("voxite_ore");
        }
        if (has(player, MobBlocks.MAGITEK_ORE.asItem()) || has(player, MobBlocks.DEEPSLATE_MAGITEK_ORE.asItem()) || has(player, ModItems.RAW_MAGITEK)) {
            unlocked.add("magitek_ore");
        }
        if (has(player, ModItems.VOXITE_INGOT)) {
            unlocked.add("voxite_ingot");
        }
        if (has(player, ModItems.MAGITEK_INGOT)) {
            unlocked.add("magitek_ingot");
        }
        if (has(player, ModItems.VOXITE_INGOT) && has(player, ModItems.MAGITEK_INGOT)) {
            unlocked.add("combined_ingots");
        }
        if (has(player, MobBlocks.RESONANCE_WORKBENCH.asItem())) {
            unlocked.add("resonance_workbench");
        }
        if (has(player, MobBlocks.VOXITE_BLOCK.asItem()) && has(player, MobBlocks.MAGITEK_BLOCK.asItem())) {
            unlocked.add("power_blocks");
            unlocked.add("starter_structure");
        }
        if (has(player, Items.REDSTONE) && has(player, MobBlocks.RESONANCE_WORKBENCH.asItem())) {
            unlocked.add("ritual_basics");
        }
        if (has(player, Items.STICK) && has(player, ModItems.VOXITE_INGOT) && has(player, ModItems.MAGITEK_INGOT) && hasAnyShard(player)) {
            unlocked.add("staff_ritual");
        }
        if (hasAnyStaff(player)) {
            unlocked.add("first_staff");
            unlocked.add("apparatus_rituals");
        }
        if (hasAnyModShard(player)) {
            unlocked.add("crystal_growth");
        }
    }

    private static boolean has(LocalPlayer player, Item item) {
        return player.getInventory().contains(stack -> stack.getItem() == item)
                || player.getMainHandItem().getItem() == item
                || player.getOffhandItem().getItem() == item;
    }

    private static boolean hasAnyShard(LocalPlayer player) {
        return has(player, Items.AMETHYST_SHARD) || hasAnyModShard(player);
    }

    private static boolean hasAnyModShard(LocalPlayer player) {
        return has(player, ModItems.AGATE_SHARD)
                || has(player, ModItems.AMBER_SHARD)
                || has(player, ModItems.AQUAMARINE_SHARD)
                || has(player, ModItems.RUBY_SHARD)
                || has(player, ModItems.TOPAZ_SHARD);
    }

    private static boolean hasAnyStaff(LocalPlayer player) {
        return player.getInventory().contains(ModItems::isResonanceStaff)
                || ModItems.isResonanceStaff(player.getMainHandItem())
                || ModItems.isResonanceStaff(player.getOffhandItem());
    }

    private static List<JournalEntry> loadEntries() {
        List<JournalEntry> entries = new ArrayList<>();
        Minecraft minecraft = Minecraft.getInstance();
        List<Resource> resources = minecraft.getResourceManager().getResourceStack(PAGES);
        if (resources.isEmpty()) {
            FearsMod.LOGGER.warn("No Resonance Journal page data found at assets/{}/{}. The journal will show fallback text only.", FearsMod.MOD_ID, PAGES.getPath());
            return entries;
        }

        try (Reader reader = resources.get(resources.size() - 1).openAsReader()) {
            JsonArray array = GSON.fromJson(reader, JsonArray.class);
            for (var element : array) {
                JsonObject object = element.getAsJsonObject();
                String unlock = object.has("unlock") ? object.get("unlock").getAsString() : "start";
                String title = object.get("title").getAsString();
                String text = object.get("text").getAsString();
                boolean alwaysVisible = object.has("always_visible") && object.get("always_visible").getAsBoolean();
                entries.add(new JournalEntry(unlock, title, text, alwaysVisible));
            }
        } catch (Exception exception) {
            FearsMod.LOGGER.warn("Failed to load Resonance Journal page data from {}.", PAGES, exception);
            entries.clear();
        }

        return entries;
    }

    private record JournalEntry(String unlock, String title, String text, boolean alwaysVisible) {
    }

    public record JournalPage(String title, String text, boolean unlocked) {
    }
}
