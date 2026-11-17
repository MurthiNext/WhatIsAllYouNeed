package com.murthinext.wiayn.compat;

import com.murthinext.wiayn.compat.gregtech.GregTechConfigurationHandler;
import com.murthinext.wiayn.compat.industrialforegoing.IndustrialForegoingConfigurationHandler;
import com.murthinext.wiayn.compat.mekanism.MekanismConfigurationHandler;
import com.murthinext.wiayn.compat.thermal.ThermalConfigurationHandler;
import com.murthinext.wiayn.wiayn;
import net.minecraftforge.fml.ModList;

public final class ConfigurationCompat {

    private ConfigurationCompat() {}

    public static void registerHandlers() {
        if (ModList.get().isLoaded("mekanism")) {
            try {
                MekanismConfigurationHandler.register();
            } catch (LinkageError error) {
                warnIncompatible("mekanism", error);
            }
        }
        if (ModList.get().isLoaded("cofh_core")) {
            try {
                ThermalConfigurationHandler.register();
            } catch (LinkageError error) {
                warnIncompatible("cofh_core", error);
            }
        }
        if (ModList.get().isLoaded("industrialforegoing")) {
            try {
                IndustrialForegoingConfigurationHandler.register();
            } catch (LinkageError error) {
                warnIncompatible("industrialforegoing", error);
            }
        }
        if (ModList.get().isLoaded("gtceu")) {
            try {
                GregTechConfigurationHandler.register();
            } catch (LinkageError error) {
                warnIncompatible("gtceu", error);
            }
        }
    }

    private static void warnIncompatible(String modId, LinkageError error) {
        wiayn.LOGGER.warn("Configuration card support for {} is disabled: incompatible mod version", modId, error);
    }
}
