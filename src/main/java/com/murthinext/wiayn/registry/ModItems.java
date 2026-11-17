package com.murthinext.wiayn.registry;

import com.murthinext.wiayn.item.configuration_card.ConfigurationCardItem;
import com.murthinext.wiayn.wiayn;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, wiayn.MODID);

    public static final RegistryObject<Item> CONFIGURATION_CARD = ITEMS.register(
            "configuration_card",
            () -> new ConfigurationCardItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    private ModItems() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
