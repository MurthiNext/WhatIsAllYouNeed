package com.murthinext.wiayn.item.multi_config_card;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public record ConfigurationOptions(boolean regular, boolean upgrades, UpgradeMode upgradeMode) {

    private static final String TAG = "configuration_options";

    public static ConfigurationOptions read(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(TAG);
        if (tag == null) {
            return new ConfigurationOptions(true, false, UpgradeMode.BEST_EFFORT);
        }
        int mode = tag.getInt("upgrade_mode");
        UpgradeMode[] modes = UpgradeMode.values();
        return new ConfigurationOptions(
                tag.getBoolean("regular"),
                tag.getBoolean("upgrades"),
                mode >= 0 && mode < modes.length ? modes[mode] : UpgradeMode.BEST_EFFORT);
    }

    public void write(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("regular", regular);
        tag.putBoolean("upgrades", upgrades);
        tag.putInt("upgrade_mode", upgradeMode.ordinal());
        stack.getOrCreateTag().put(TAG, tag);
    }
}
