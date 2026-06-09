package net.fearsredemption.fearsmod;

import net.fabricmc.api.ModInitializer;
import net.fearsredemption.fearsmod.block.entity.ModBlockEntities;
import net.fearsredemption.fearsmod.block.ModBlocks;
import net.fearsredemption.fearsmod.item.ModItems;
import net.fearsredemption.fearsmod.journal.JournalNetworking;
import net.fearsredemption.fearsmod.journal.JournalUnlocks;
import net.fearsredemption.fearsmod.world.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FearsMod implements ModInitializer {
	public static final String MOD_ID = "fearsmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		JournalNetworking.initializeServer();
		JournalUnlocks.initialize();
		ModWorldGeneration.initialize();
	}
}
