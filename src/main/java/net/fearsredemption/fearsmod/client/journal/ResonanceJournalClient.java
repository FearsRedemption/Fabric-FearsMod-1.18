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
import net.fearsredemption.fearsmod.item.ModItems;
import net.fearsredemption.fearsmod.journal.JournalNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.InteractionResult;

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
