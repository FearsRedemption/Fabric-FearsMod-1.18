package net.fearsredemption.fearsmod.client.journal;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fearsredemption.fearsmod.FearsMod;
import net.fearsredemption.fearsmod.item.ModItems;
import net.fearsredemption.fearsmod.journal.JournalUnlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.InteractionResult;

public final class ResonanceJournalClient {
    private static final Identifier PAGES = Identifier.fromNamespaceAndPath(FearsMod.MOD_ID, "resonance_journal/pages.json");
    private static final Gson GSON = new Gson();

    private ResonanceJournalClient() {
    }

    public static void initialize() {
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!level.isClientSide() || player.getItemInHand(hand).getItem() != ModItems.RESONANCE_JOURNAL) {
                return InteractionResult.PASS;
            }

            Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.BookAccess(buildPages(player.entityTags()))));
            return InteractionResult.SUCCESS;
        });
    }

    private static List<Component> buildPages(Set<String> tags) {
        List<Component> pages = new ArrayList<>();
        for (JournalPage page : loadPages()) {
            if (page.alwaysVisible() || tags.contains(JournalUnlocks.TAG_PREFIX + page.unlock())) {
                pages.add(Component.literal(page.title() + "\n\n" + page.text()));
            } else {
                pages.add(Component.literal("Undiscovered Entry\n\nThe notes here are incomplete. I need to discover more before this makes sense."));
            }
        }

        if (pages.isEmpty()) {
            pages.add(Component.literal("Resonance Journal\n\nI started this journal because the materials I keep finding do not behave like normal stone or metal."));
        }
        return pages;
    }

    private static List<JournalPage> loadPages() {
        List<JournalPage> pages = new ArrayList<>();
        Minecraft minecraft = Minecraft.getInstance();
        List<Resource> resources = minecraft.getResourceManager().getResourceStack(PAGES);
        if (resources.isEmpty()) {
            return pages;
        }

        try (Reader reader = resources.get(resources.size() - 1).openAsReader()) {
            JsonArray array = GSON.fromJson(reader, JsonArray.class);
            for (var element : array) {
                JsonObject object = element.getAsJsonObject();
                String unlock = object.has("unlock") ? object.get("unlock").getAsString() : "start";
                String title = object.get("title").getAsString();
                String text = object.get("text").getAsString();
                boolean alwaysVisible = object.has("always_visible") && object.get("always_visible").getAsBoolean();
                pages.add(new JournalPage(unlock, title, text, alwaysVisible));
            }
        } catch (Exception ignored) {
            pages.clear();
        }

        return pages;
    }

    private record JournalPage(String unlock, String title, String text, boolean alwaysVisible) {
    }
}
