package com.murthinext.wiayn.compat.industrialforegoing;

import com.hrznstudio.titanium.block.tile.MachineTile;
import com.murthinext.wiayn.item.multi_config_card.upgrade.SlottedUpgradeInventory;
import com.murthinext.wiayn.item.multi_config_card.upgrade.UpgradeInventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

final class IndustrialForegoingUpgrades {

    private IndustrialForegoingUpgrades() {}

    @Nullable
    static UpgradeInventory of(@Nullable BlockEntity blockEntity) {
        return blockEntity instanceof MachineTile<?> machine ? inventory(machine, false) : null;
    }

    private static SlottedUpgradeInventory inventory(MachineTile<?> machine, boolean simulation) {
        var augments = machine.getAugmentInventory();
        if (simulation) {
            augments.setComponentHarness(null);
            augments.setOnSlotChanged((stack, slot) -> {});
        }
        return new SlottedUpgradeInventory(
                augments,
                augments::setStackInSlot,
                () -> {
                    BlockEntity copy = machine.getType().create(machine.getBlockPos(), machine.getBlockState());
                    return copy instanceof MachineTile<?> detached ? inventory(detached, true) : null;
                },
                () -> {
                    if (!simulation) {
                        machine.setChanged();
                        machine.markForUpdate();
                    }
                });
    }
}
