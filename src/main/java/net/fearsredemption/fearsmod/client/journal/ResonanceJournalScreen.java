package net.fearsredemption.fearsmod.client.journal;

import java.util.ArrayList;
import java.util.List;

import net.fearsredemption.fearsmod.block.MobBlocks;
import net.fearsredemption.fearsmod.item.ModItems;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ResonanceJournalScreen extends Screen {
    private static final int BOOK_WIDTH = 342;
    private static final int BOOK_HEIGHT = 218;
    private static final int PAGE_TEXT_WIDTH = 124;
    private static final int PAGE_TOP_MARGIN = 45;
    private static final int PAGE_BOTTOM = 172;
    private static final int TEXT_COLOR = 0xFF151018;
    private static final int MUTED_TEXT_COLOR = 0xFF4F4054;

    private List<ResonanceJournalClient.JournalPage> pages;
    private int pageIndex;
    private Button previousButton;
    private Button nextButton;

    public ResonanceJournalScreen(List<ResonanceJournalClient.JournalPage> pages) {
        super(Component.translatable("item.fearsmod.resonance_journal"));
        this.pages = pages;
    }

    @Override
    protected void init() {
        int left = (width - BOOK_WIDTH) / 2;
        int top = (height - BOOK_HEIGHT) / 2;
        previousButton = addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (pageIndex > 0) {
                pageIndex--;
                updateButtons();
            }
        }).bounds(left + 18, top + BOOK_HEIGHT - 30, 24, 20).build());
        nextButton = addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if (pageIndex < pages.size() - 1) {
                pageIndex++;
                updateButtons();
            }
        }).bounds(left + BOOK_WIDTH - 42, top + BOOK_HEIGHT - 30, 24, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), button -> onClose())
                .bounds(left + BOOK_WIDTH / 2 - 32, top + BOOK_HEIGHT - 30, 64, 20)
                .build());
        updateButtons();
    }

    public void setPages(List<ResonanceJournalClient.JournalPage> pages) {
        int oldSize = this.pages.size();
        this.pages = pages;
        if (pages.size() > oldSize) {
            pageIndex = pages.size() - 1;
        }
        if (pageIndex >= pages.size()) {
            pageIndex = Math.max(0, pages.size() - 1);
        }
        updateButtons();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractTransparentBackground(graphics);
        int left = (width - BOOK_WIDTH) / 2;
        int top = (height - BOOK_HEIGHT) / 2;

        drawBook(graphics, left, top);

        ResonanceJournalClient.JournalPage page = pages.get(pageIndex);
        drawCenteredText(graphics, page.title(), left + BOOK_WIDTH / 2, top + 24, TEXT_COLOR);
        graphics.horizontalLine(left + 48, left + BOOK_WIDTH - 48, top + 38, 0xFF9D78B6);

        drawPageContent(graphics, page, left, top);
        drawCenteredText(graphics, (pageIndex + 1) + " / " + pages.size(), left + BOOK_WIDTH / 2, top + BOOK_HEIGHT - 45, MUTED_TEXT_COLOR);
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

    private void drawBook(GuiGraphicsExtractor graphics, int left, int top) {
        graphics.fill(left - 8, top - 8, left + BOOK_WIDTH + 8, top + BOOK_HEIGHT + 8, 0xDD08060B);
        graphics.fill(left, top, left + BOOK_WIDTH, top + BOOK_HEIGHT, 0xFF161019);
        graphics.fill(left + 5, top + 5, left + BOOK_WIDTH - 5, top + BOOK_HEIGHT - 5, 0xFF30203C);
        graphics.fill(left + 14, top + 12, left + BOOK_WIDTH - 14, top + BOOK_HEIGHT - 42, 0xFFF0DEC0);
        graphics.fill(left + BOOK_WIDTH / 2 - 3, top + 11, left + BOOK_WIDTH / 2 + 3, top + BOOK_HEIGHT - 41, 0xFFD3B98E);
        graphics.fill(left + BOOK_WIDTH / 2 - 1, top + 12, left + BOOK_WIDTH / 2 + 1, top + BOOK_HEIGHT - 42, 0xFF8B6F8E);
        graphics.outline(left, top, BOOK_WIDTH, BOOK_HEIGHT, 0xFFB996E5);
        graphics.outline(left + 13, top + 11, BOOK_WIDTH - 26, BOOK_HEIGHT - 52, 0xFF7F6089);
        graphics.horizontalLine(left + 20, left + BOOK_WIDTH - 20, top + BOOK_HEIGHT - 38, 0xFF0F0A12);
    }

    private void drawPageContent(GuiGraphicsExtractor graphics, ResonanceJournalClient.JournalPage page, int left, int top) {
        int leftPageX = left + 27;
        int rightPageX = left + BOOK_WIDTH / 2 + 17;
        int textTop = top + PAGE_TOP_MARGIN;
        List<String> lines = wrap(page.text(), PAGE_TEXT_WIDTH);

        if (hasDiagram(page.unlock())) {
            drawLines(graphics, lines, leftPageX, textTop, top + PAGE_BOTTOM);
            drawPageDiagram(graphics, page.unlock(), rightPageX, textTop + 6);
            return;
        }

        int split = Math.min(lines.size(), Math.max(1, (lines.size() + 1) / 2));
        drawLines(graphics, lines.subList(0, split), leftPageX, textTop, top + PAGE_BOTTOM);
        drawLines(graphics, lines.subList(split, lines.size()), rightPageX, textTop, top + PAGE_BOTTOM);
    }

    private void drawLines(GuiGraphicsExtractor graphics, List<String> lines, int x, int y, int bottom) {
        for (String line : lines) {
            drawText(graphics, line, x, y, TEXT_COLOR);
            y += 10;
            if (y > bottom) {
                break;
            }
        }
    }

    private boolean hasDiagram(String unlock) {
        return switch (unlock) {
            case "combined_ingots", "starter_structure", "staff_ritual" -> true;
            default -> false;
        };
    }

    private void drawPageDiagram(GuiGraphicsExtractor graphics, String unlock, int x, int y) {
        switch (unlock) {
            case "combined_ingots" -> {
                drawCenteredText(graphics, "Workbench", x + 55, y, MUTED_TEXT_COLOR);
                drawGrid(graphics, x + 19, y + 17, new ItemStack[][]{
                        {new ItemStack(ModItems.VOXITE_INGOT), new ItemStack(Items.AMETHYST_SHARD), new ItemStack(ModItems.VOXITE_INGOT)},
                        {new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.CRAFTING_TABLE), new ItemStack(Items.COPPER_INGOT)},
                        {new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.COPPER_INGOT)}
                });
            }
            case "starter_structure" -> {
                drawCenteredText(graphics, "Ground Pattern", x + 55, y, MUTED_TEXT_COLOR);
                drawGrid(graphics, x + 19, y + 17, new ItemStack[][]{
                        {ItemStack.EMPTY, new ItemStack(MobBlocks.VOXITE_BLOCK), ItemStack.EMPTY},
                        {new ItemStack(MobBlocks.MAGITEK_BLOCK), new ItemStack(MobBlocks.RESONANCE_WORKBENCH), new ItemStack(MobBlocks.MAGITEK_BLOCK)},
                        {ItemStack.EMPTY, new ItemStack(MobBlocks.VOXITE_BLOCK), ItemStack.EMPTY}
                });
            }
            case "staff_ritual" -> {
                drawCenteredText(graphics, "Toss Nearby", x + 55, y, MUTED_TEXT_COLOR);
                drawIconRow(graphics, x + 55, y + 28,
                    new ItemStack(Items.STICK),
                    new ItemStack(ModItems.VOXITE_INGOT),
                    new ItemStack(ModItems.MAGITEK_INGOT),
                    new ItemStack(Items.AMETHYST_SHARD),
                    new ItemStack(Items.REDSTONE));
            }
            default -> {
            }
        }
    }

    private void drawGrid(GuiGraphicsExtractor graphics, int x, int y, ItemStack[][] stacks) {
        int gap = 24;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slotX = x + column * gap;
                int slotY = y + row * gap;
                graphics.fill(slotX - 2, slotY - 2, slotX + 18, slotY + 18, 0x55FFFFFF);
                graphics.outline(slotX - 2, slotY - 2, 20, 20, 0xFF8B6F8E);
                if (!stacks[row][column].isEmpty()) {
                    graphics.item(stacks[row][column], slotX, slotY);
                }
            }
        }
    }

    private void drawIconRow(GuiGraphicsExtractor graphics, int centerX, int y, ItemStack... stacks) {
        int gap = 20;
        int startX = centerX - (stacks.length * gap - 4) / 2;
        for (int i = 0; i < stacks.length; i++) {
            int x = startX + i * gap;
            graphics.fill(x - 1, y - 1, x + 17, y + 17, 0x44FFFFFF);
            graphics.item(stacks[i], x, y);
        }
    }

    private void drawCenteredText(GuiGraphicsExtractor graphics, String text, int centerX, int y, int color) {
        drawText(graphics, text, centerX - font.width(text) / 2, y, color);
    }

    private void drawText(GuiGraphicsExtractor graphics, String text, int x, int y, int color) {
        if (text.isEmpty()) {
            return;
        }

        graphics.text(font, text, x, y, color, false);
    }
}
