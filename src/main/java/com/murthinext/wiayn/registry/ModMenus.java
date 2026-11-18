package com.murthinext.wiayn.registry;

import com.murthinext.wiayn.item.multi_config_card.MultiConfigCardMenu;
import com.murthinext.wiayn.wiayn;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {

    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, wiayn.MODID);

    public static final RegistryObject<MenuType<MultiConfigCardMenu>> MULTI_CONFIG_CARD = MENUS.register(
            "multi_config_card",
            () -> IForgeMenuType.create((id, inventory, buffer) ->
                    new MultiConfigCardMenu(id, inventory, buffer.readEnum(InteractionHand.class))));

    private ModMenus() {}

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
