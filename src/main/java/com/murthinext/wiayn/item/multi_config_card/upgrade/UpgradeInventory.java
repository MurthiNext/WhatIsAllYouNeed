package com.murthinext.wiayn.item.multi_config_card.upgrade;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface UpgradeInventory {

    List<ItemStack> contents();

    @Nullable
    UpgradeInventory simulate(boolean clear);

    int insert(ItemStack stack);

    void apply(List<ItemStack> contents);
}
