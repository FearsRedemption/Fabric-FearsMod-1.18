package net.fearsredemption.fearsmod;

import net.fabricmc.api.ModInitializer;
import net.fearsredemption.fearsmod.block.MobBlocks;
import net.fearsredemption.fearsmod.item.ModItems;
import net.fearsredemption.fearsmod.world.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FearsMod implements ModInitializer {
	public static final String MOD_ID = "fearsmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		MobBlocks.initialize();
		ModWorldGeneration.initialize();
	}
}
