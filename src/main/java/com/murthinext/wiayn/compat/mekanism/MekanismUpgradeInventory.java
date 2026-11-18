package com.murthinext.wiayn.compat.mekanism;

import com.murthinext.wiayn.item.multi_config_card.upgrade.UpgradeInventory;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import mekanism.api.NBTConstants;
import mekanism.api.Upgrade;
import mekanism.common.item.interfaces.IUpgradeItem;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

final class MekanismUpgradeInventory implements UpgradeInventory {

    private final TileEntityMekanism tile;
    private final TileComponentUpgrade component;
    private final Map<Upgrade, Integer> installed = new EnumMap<>(Upgrade.class);

    MekanismUpgradeInventory(TileEntityMekanism tile) {
        this.tile = tile;
        component = tile.getComponent();
        for (Upgrade upgrade : Upgrade.values()) {
            int count = component.getUpgrades(upgrade);
            if (count > 0) {
                installed.put(upgrade, count);
            }
        }
    }

    @Override
    public List<ItemStack> contents() {
        List<ItemStack> contents = new ArrayList<>();
        installed.forEach((upgrade, count) -> contents.add(UpgradeUtils.getStack(upgrade, count)));
        return contents;
    }

    @Override
    public UpgradeInventory simulate(boolean clear) {
        MekanismUpgradeInventory simulation = new MekanismUpgradeInventory(tile);
        if (clear) {
            simulation.installed.clear();
        }
        return simulation;
    }

    @Override
    public int insert(ItemStack stack) {
        if (!(stack.getItem() instanceof IUpgradeItem item)) {
            return 0;
        }
        Upgrade upgrade = item.getUpgradeType(stack);
        if (!component.supports(upgrade)) {
            return 0;
        }
        int count = installed.getOrDefault(upgrade, 0);
        int accepted = Math.min(stack.getCount(), upgrade.getMax() - count);
        if (accepted > 0) {
            installed.put(upgrade, count + accepted);
        }
        return accepted;
    }

    @Override
    public void apply(List<ItemStack> contents) {
        Map<Upgrade, Integer> replacements = new EnumMap<>(Upgrade.class);
        for (ItemStack stack : contents) {
            if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeItem item) {
                replacements.merge(item.getUpgradeType(stack), stack.getCount(), Integer::sum);
            }
        }
        CompoundTag data = new CompoundTag();
        component.write(data);
        Upgrade.saveMap(replacements, data.getCompound(NBTConstants.COMPONENT_UPGRADE));
        component.read(data);
        installed.clear();
        installed.putAll(replacements);
        tile.markForSave();
        tile.sendUpdatePacket();
    }
}
