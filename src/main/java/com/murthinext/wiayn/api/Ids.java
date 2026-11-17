package com.murthinext.wiayn.api;

import com.murthinext.wiayn.wiayn;
import net.minecraft.resources.ResourceLocation;

public final class Ids {

    private Ids() {}

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(wiayn.MODID, path);
    }
}
