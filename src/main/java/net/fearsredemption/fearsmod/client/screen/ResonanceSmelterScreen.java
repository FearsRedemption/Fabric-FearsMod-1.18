package net.fearsredemption.fearsmod.client.screen;

import net.fearsredemption.fearsmod.screen.ResonanceSmelterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ResonanceSmelterScreen extends AbstractContainerScreen<ResonanceSmelterMenu> {
    public ResonanceSmelterScreen(ResonanceSmelterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 180);
        inventoryLabelY = 88;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractTransparentBackground(graphics);
        drawPanel(graphics);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    private void drawPanel(GuiGraphicsExtractor graphics) {
        int left = leftPos;
        int top = topPos;
        graphics.fill(left, top, left + imageWidth, top + imageHeight, 0xEE130F18);
        graphics.outline(left, top, imageWidth, imageHeight, 0xFFB987FF);
        graphics.fill(left + 7, top + 15, left + imageWidth - 7, top + 86, 0xFF21162A);
        graphics.outline(left + 7, top + 15, imageWidth - 14, 71, 0xFF6F4B88);

        graphics.text(font, title, left + titleLabelX, top + titleLabelY, 0xFFEFE7F7, false);
        graphics.text(font, playerInventoryTitle, left + inventoryLabelX, top + inventoryLabelY, 0xFFEFE7F7, false);

        for (int lane = 0; lane < 3; lane++) {
            int y = top + 21 + lane * 22;
            drawSlot(graphics, left + 43, y - 1);
            drawSlot(graphics, left + 117, y - 1);
            graphics.fill(left + 66, y + 7, left + 90, y + 10, 0xFF332641);
            int progress = menu.cookProgress(lane);
            if (progress > 0) {
                graphics.fill(left + 66, y + 7, left + 66 + progress, y + 10, 0xFFB987FF);
                graphics.fill(left + 66, y + 10, left + 66 + progress, y + 12, 0xFF5EF3FF);
            }
            graphics.text(font, ">", left + 96, y + 4, 0xFFC7AECF, false);
        }

        drawSlot(graphics, left + 17, top + 43);
        graphics.fill(left + 20, top + 24, left + 31, top + 39, 0xFF2B1F34);
        int lit = menu.litProgress();
        if (menu.isLit() && lit > 0) {
            graphics.fill(left + 20, top + 39 - lit, left + 31, top + 39, 0xFFE46A42);
            graphics.fill(left + 23, top + 39 - lit, left + 28, top + 39, 0xFFFFC85A);
        }
    }

    private void drawSlot(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 18, y + 18, 0x8825162F);
        graphics.outline(x - 1, y - 1, 19, 19, 0xFF7E6689);
    }
}
