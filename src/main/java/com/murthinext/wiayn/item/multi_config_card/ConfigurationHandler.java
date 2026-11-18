package com.murthinext.wiayn.item.multi_config_card;

import com.murthinext.wiayn.item.multi_config_card.upgrade.UpgradeInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface ConfigurationHandler {

    boolean canCopy(ConfigurationTarget target, Player player);

    @Nullable
    ConfigurationSnapshot copy(ConfigurationTarget target, Player player, boolean regular);

    default boolean canPaste(ConfigurationTarget target, Player player, CompoundTag data) {
        return canCopy(target, player);
    }

    void paste(ConfigurationTarget target, Player player, CompoundTag data);

    @Nullable
    default UpgradeInventory upgrades(ConfigurationTarget target) {
        return null;
    }
}
