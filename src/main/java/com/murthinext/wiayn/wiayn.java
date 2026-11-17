package com.murthinext.wiayn;

import com.mojang.logging.LogUtils;
import com.murthinext.wiayn.compat.ConfigurationCompat;
import com.murthinext.wiayn.registry.ModCreativeTabs;
import com.murthinext.wiayn.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * What Is All You Need
 */
@Mod(wiayn.MODID)
public final class wiayn {

    public static final String MODID = "wiayn";
    public static final Logger LOGGER = LogUtils.getLogger();

    public wiayn(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ModItems.register(modBus);
        ModCreativeTabs.register(modBus);
        modBus.addListener(this::commonSetup);
        LOGGER.info("What Is All You Need initialized");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ConfigurationCompat::registerHandlers);
    }
}
