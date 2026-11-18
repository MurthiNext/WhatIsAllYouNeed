package com.murthinext.wiayn.compat.thermal;

import cofh.lib.common.inventory.ItemStorageCoFH;
import cofh.lib.common.inventory.SimpleItemHandler;
import cofh.thermal.lib.common.block.entity.AugmentableBlockEntity;
import com.murthinext.wiayn.item.multi_config_card.upgrade.SlottedUpgradeInventory;
import com.murthinext.wiayn.item.multi_config_card.upgrade.UpgradeInventory;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

final class ThermalUpgrades {

    private ThermalUpgrades() {}

    @Nullable
    static UpgradeInventory of(@Nullable BlockEntity blockEntity) {
        return blockEntity instanceof AugmentableBlockEntity tile && tile.augSize() > 0 ? inventory(tile, false) : null;
    }

    private static SlottedUpgradeInventory inventory(AugmentableBlockEntity tile, boolean simulation) {
        int start = tile.invSize() - tile.augSize();
        List<ItemStorageCoFH> slots = new ArrayList<>(tile.augSize());
        for (int slot = 0; slot < tile.augSize(); slot++) {
            slots.add(tile.getItemInv().getSlot(start + slot));
        }
        return new SlottedUpgradeInventory(
                new SimpleItemHandler(slots),
                (slot, stack) -> tile.getItemInv().set(start + slot, stack),
                () -> {
                    BlockEntity copy = tile.getType().create(tile.getBlockPos(), tile.getBlockState());
                    return copy instanceof AugmentableBlockEntity detached ? inventory(detached, true) : null;
                },
                () -> {
                    if (!simulation) {
                        tile.onInventoryChanged(start);
                        tile.onControlUpdate();
                        tile.setChanged();
                    }
                });
    }
}
