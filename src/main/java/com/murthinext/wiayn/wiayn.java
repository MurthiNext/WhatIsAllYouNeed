package com.murthinext.wiayn;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * What Is All You Need
 */
@Mod(wiayn.MODID)
public final class wiayn {

    public static final String MODID = "wiayn";
    public static final Logger LOGGER = LogUtils.getLogger();

    public wiayn() {
        LOGGER.info("What Is All You Need initialized");
    }
}
