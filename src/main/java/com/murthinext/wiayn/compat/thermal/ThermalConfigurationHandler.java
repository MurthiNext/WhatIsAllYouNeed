package com.murthinext.wiayn.compat.thermal;

import cofh.lib.api.IConveyableData;
import cofh.lib.api.control.ISecurable;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationHandler;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationHandlerRegistry;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationSnapshot;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationTarget;
import com.murthinext.wiayn.item.multi_config_card.upgrade.UpgradeInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public final class ThermalConfigurationHandler implements ConfigurationHandler {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("thermal", "redprint");

    private static final ThermalConfigurationHandler INSTANCE = new ThermalConfigurationHandler();

    private ThermalConfigurationHandler() {}

    public static void register() {
        ConfigurationHandlerRegistry.register(ID, INSTANCE);
    }

    @Override
    public boolean canCopy(ConfigurationTarget target, Player player) {
        IConveyableData conveyable = conveyable(target);
        return conveyable != null
                && canAccess(target, player)
                && !write(conveyable, player).isEmpty();
    }

    @Nullable
    @Override
    public ConfigurationSnapshot copy(ConfigurationTarget target, Player player, boolean regular) {
        IConveyableData conveyable = conveyable(target);
        if (conveyable == null) {
            return null;
        }
        CompoundTag data = regular ? write(conveyable, player) : new CompoundTag();
        return new ConfigurationSnapshot(ID, target.displayName(), data);
    }

    @Override
    public boolean canPaste(ConfigurationTarget target, Player player, CompoundTag data) {
        return conveyable(target) != null && canAccess(target, player);
    }

    @Override
    public void paste(ConfigurationTarget target, Player player, CompoundTag data) {
        IConveyableData conveyable = conveyable(target);
        if (conveyable == null) {
            return;
        }
        conveyable.readConveyableData(player, data);
        if (target.blockEntity() != null) {
            target.blockEntity().setChanged();
        }
    }

    @Nullable
    @Override
    public UpgradeInventory upgrades(ConfigurationTarget target) {
        return ModList.get().isLoaded("thermal") ? ThermalUpgrades.of(target.blockEntity()) : null;
    }

    private static CompoundTag write(IConveyableData conveyable, Player player) {
        CompoundTag data = new CompoundTag();
        conveyable.writeConveyableData(player, data);
        return data;
    }

    @Nullable
    private static IConveyableData conveyable(ConfigurationTarget target) {
        return target.blockEntity() instanceof IConveyableData conveyable ? conveyable : null;
    }

    private static boolean canAccess(ConfigurationTarget target, Player player) {
        return !(target.blockEntity() instanceof ISecurable securable) || securable.canAccess(player);
    }
}
