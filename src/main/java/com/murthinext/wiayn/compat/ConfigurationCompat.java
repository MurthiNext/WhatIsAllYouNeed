package com.murthinext.wiayn.compat;

import com.murthinext.wiayn.compat.mekanism.MekanismConfigurationHandler;
import net.minecraftforge.fml.ModList;

public final class ConfigurationCompat {

    private ConfigurationCompat() {}

    public static void registerHandlers() {
        if (ModList.get().isLoaded("mekanism")) {
            MekanismConfigurationHandler.register();
        }
    }
}
