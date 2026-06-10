package net.fearsredemption.fearsmod.screen;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fearsredemption.fearsmod.FearsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

import static net.fearsredemption.fearsmod.FearsMod.MOD_ID;

public final class ModMenus {
    public static final MenuType<ResonanceSmelterMenu> RESONANCE_SMELTER = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(MOD_ID, "resonance_smelter"),
            new ExtendedMenuType<>(ResonanceSmelterMenu::new, BlockPos.STREAM_CODEC)
    );

    private ModMenus() {
    }

    public static void initialize() {
        FearsMod.LOGGER.info("Registering ModMenus for " + MOD_ID);
    }
}
