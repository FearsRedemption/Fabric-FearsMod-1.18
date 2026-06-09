package net.fearsredemption.fearsmod.client.journal;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

public class ResonanceJournalScreen extends Screen {
    private static final int PANEL_WIDTH = 308;
    private static final int PANEL_HEIGHT = 198;
    private static final int TEXT_WIDTH = 250;

    private List<ResonanceJournalClient.JournalPage> pages;
    private int pageIndex;
    private int refreshTicks;
    private Button previousButton;
    private Button nextButton;

    public ResonanceJournalScreen(List<ResonanceJournalClient.JournalPage> pages) {
        super(Component.translatable("item.fearsmod.resonance_journal"));
        this.pages = pages;
    }

    @Override
    protected void init() {
        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;
        previousButton = addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (pageIndex > 0) {
                pageIndex--;
                updateButtons();
            }
        }).bounds(left + 18, top + PANEL_HEIGHT - 30, 24, 20).build());
        nextButton = addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if (pageIndex < pages.size() - 1) {
                pageIndex++;
                updateButtons();
            }
        }).bounds(left + PANEL_WIDTH - 42, top + PANEL_HEIGHT - 30, 24, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), button -> onClose())
                .bounds(left + PANEL_WIDTH / 2 - 32, top + PANEL_HEIGHT - 30, 64, 20)
                .build());
        updateButtons();
    }

    public void setPages(List<ResonanceJournalClient.JournalPage> pages) {
        for (int i = 0; i < Math.min(this.pages.size(), pages.size()); i++) {
            if (!this.pages.get(i).unlocked() && pages.get(i).unlocked()) {
                pageIndex = i;
                break;
            }
        }
        this.pages = pages;
        if (pageIndex >= pages.size()) {
            pageIndex = Math.max(0, pages.size() - 1);
        }
        updateButtons();
    }

    @Override
    public void tick() {
        refreshTicks++;
        if (refreshTicks >= 20) {
            refreshTicks = 0;
            ResonanceJournalClient.requestRefresh();
        }
        super.tick();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractTransparentBackground(graphics);
        int left = (width - PANEL_WIDTH) / 2;
        int top = (height - PANEL_HEIGHT) / 2;

        graphics.fill(left - 6, top - 6, left + PANEL_WIDTH + 6, top + PANEL_HEIGHT + 6, 0xDD09070D);
        graphics.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xFF21182A);
        graphics.fill(left + 7, top + 7, left + PANEL_WIDTH - 7, top + PANEL_HEIGHT - 7, 0xFF35243F);
        graphics.fill(left + 13, top + 13, left + PANEL_WIDTH - 13, top + PANEL_HEIGHT - 39, 0xFFE5D6B8);
        graphics.outline(left, top, PANEL_WIDTH, PANEL_HEIGHT, 0xFF9B64D7);
        graphics.outline(left + 6, top + 6, PANEL_WIDTH - 12, PANEL_HEIGHT - 12, 0xFFDBD6C7);

        ResonanceJournalClient.JournalPage page = pages.get(pageIndex);
        int titleColor = 0xFF000000;
        int bodyColor = 0xFF000000;
        outlinedCenteredText(graphics, page.title(), left + PANEL_WIDTH / 2, top + 22, titleColor);
        graphics.horizontalLine(left + 36, left + PANEL_WIDTH - 36, top + 35, 0xFF8C6B9D);

        int y = top + 45;
        for (String line : wrap(page.text(), TEXT_WIDTH)) {
            outlinedText(graphics, line, left + 29, y, bodyColor);
            y += 10;
            if (y > top + PANEL_HEIGHT - 52) {
                break;
            }
        }

        outlinedCenteredText(graphics, (pageIndex + 1) + " / " + pages.size(), left + PANEL_WIDTH / 2, top + PANEL_HEIGHT - 50, 0xFFE7D7F5);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 263 && pageIndex > 0) {
            pageIndex--;
            updateButtons();
            return true;
        }
        if (event.key() == 262 && pageIndex < pages.size() - 1) {
            pageIndex++;
            updateButtons();
            return true;
        }
        return super.keyPressed(event);
    }

    private List<String> wrap(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.split("\\n", -1)) {
            if (paragraph.isBlank()) {
                lines.add("");
                continue;
            }

            StringBuilder line = new StringBuilder();
            for (String word : paragraph.split(" ")) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if (font.width(candidate) > maxWidth && !line.isEmpty()) {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                } else {
                    line = new StringBuilder(candidate);
                }
            }
            lines.add(line.toString());
        }
        return lines;
    }

    private void updateButtons() {
        if (previousButton != null) {
            previousButton.active = pageIndex > 0;
        }
        if (nextButton != null) {
            nextButton.active = pageIndex < pages.size() - 1;
        }
    }

    private void outlinedCenteredText(GuiGraphicsExtractor graphics, String text, int centerX, int y, int color) {
        outlinedText(graphics, text, centerX - font.width(text) / 2, y, color);
    }

    private void outlinedText(GuiGraphicsExtractor graphics, String text, int x, int y, int color) {
        graphics.text(font, text, x - 1, y, 0xCCFFFFFF);
        graphics.text(font, text, x + 1, y, 0xCCFFFFFF);
        graphics.text(font, text, x, y - 1, 0xCCFFFFFF);
        graphics.text(font, text, x, y + 1, 0xCCFFFFFF);
        graphics.text(font, text, x, y, color);
    }
}
