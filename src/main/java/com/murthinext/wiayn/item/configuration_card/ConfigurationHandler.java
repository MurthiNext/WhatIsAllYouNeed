package com.murthinext.wiayn.item.configuration_card;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface ConfigurationHandler {

    boolean canCopy(ConfigurationTarget target, Player player);

    @Nullable
    ConfigurationSnapshot copy(ConfigurationTarget target, Player player);

    default boolean canPaste(ConfigurationTarget target, Player player, CompoundTag data) {
        return canCopy(target, player);
    }

    void paste(ConfigurationTarget target, Player player, CompoundTag data);
}
