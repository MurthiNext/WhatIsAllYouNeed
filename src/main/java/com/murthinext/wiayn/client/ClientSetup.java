package com.murthinext.wiayn.client;

import com.murthinext.wiayn.item.multi_config_card.client.MultiConfigCardScreen;
import com.murthinext.wiayn.registry.ModMenus;
import com.murthinext.wiayn.wiayn;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = wiayn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {}

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenus.MULTI_CONFIG_CARD.get(), MultiConfigCardScreen::new));
    }
}
