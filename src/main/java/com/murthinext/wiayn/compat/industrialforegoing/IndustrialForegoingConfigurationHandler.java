package com.murthinext.wiayn.compat.industrialforegoing;

import com.buuz135.industrial.api.IMachineSettings;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationHandler;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationHandlerRegistry;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationSnapshot;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationTarget;
import com.murthinext.wiayn.item.multi_config_card.upgrade.UpgradeInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class IndustrialForegoingConfigurationHandler implements ConfigurationHandler {

    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("industrialforegoing", "machine_settings");

    private static final String AUGMENTS = "MACHINE_ADDONS";

    private static final IndustrialForegoingConfigurationHandler INSTANCE =
            new IndustrialForegoingConfigurationHandler();

    private IndustrialForegoingConfigurationHandler() {}

    public static void register() {
        ConfigurationHandlerRegistry.register(ID, INSTANCE);
    }

    @Override
    public boolean canCopy(ConfigurationTarget target, Player player) {
        return machineSettings(target) != null;
    }

    @Nullable
    @Override
    public ConfigurationSnapshot copy(ConfigurationTarget target, Player player, boolean regular) {
        IMachineSettings settings = machineSettings(target);
        if (settings == null) {
            return null;
        }
        CompoundTag data = new CompoundTag();
        if (regular) {
            settings.saveSettings(player, data);
            data.remove(AUGMENTS);
        }
        return new ConfigurationSnapshot(ID, target.displayName(), data);
    }

    @Override
    public boolean canPaste(ConfigurationTarget target, Player player, CompoundTag data) {
        return machineSettings(target) != null;
    }

    @Override
    public void paste(ConfigurationTarget target, Player player, CompoundTag data) {
        IMachineSettings settings = machineSettings(target);
        if (settings != null) {
            settings.loadSettings(player, data);
        }
    }

    @Nullable
    @Override
    public UpgradeInventory upgrades(ConfigurationTarget target) {
        return IndustrialForegoingUpgrades.of(target.blockEntity());
    }

    @Nullable
    private static IMachineSettings machineSettings(ConfigurationTarget target) {
        return target.blockEntity() instanceof IMachineSettings settings ? settings : null;
    }
}
