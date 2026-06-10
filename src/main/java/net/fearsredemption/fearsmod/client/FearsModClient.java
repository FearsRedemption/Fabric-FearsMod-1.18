package net.fearsredemption.fearsmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fearsredemption.fearsmod.client.journal.ResonanceJournalClient;
import net.fearsredemption.fearsmod.client.screen.ResonanceSmelterScreen;
import net.fearsredemption.fearsmod.screen.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;

public class FearsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenus.RESONANCE_SMELTER, ResonanceSmelterScreen::new);
        ResonanceJournalClient.initialize();
    }
}
