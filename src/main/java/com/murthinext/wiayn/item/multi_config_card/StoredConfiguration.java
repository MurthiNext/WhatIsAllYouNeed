package com.murthinext.wiayn.item.multi_config_card;

import com.google.gson.JsonParseException;
import com.murthinext.wiayn.item.multi_config_card.upgrade.UpgradeInventory;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record StoredConfiguration(
        ResourceLocation handler,
        Component name,
        CompoundTag data,
        boolean regular,
        @Nullable List<ItemStack> upgrades) {

    public static final String TAG = "configuration";
    private static final String HANDLER = "handler";
    private static final String NAME = "name";
    private static final String DATA = "data";

    public static StoredConfiguration of(
            ConfigurationSnapshot snapshot, ConfigurationOptions options, @Nullable UpgradeInventory upgrades) {
        return new StoredConfiguration(
                snapshot.handler(),
                snapshot.name(),
                snapshot.data(),
                options.regular(),
                options.upgrades() && upgrades != null ? upgrades.contents() : null);
    }

    @Nullable
    public static StoredConfiguration read(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(TAG);
        if (tag == null || !tag.contains(HANDLER, Tag.TAG_STRING) || !tag.contains(DATA, Tag.TAG_COMPOUND)) {
            return null;
        }
        ResourceLocation handler = ResourceLocation.tryParse(tag.getString(HANDLER));
        if (handler == null) {
            return null;
        }
        List<ItemStack> upgrades = null;
        if (tag.contains("upgrades", Tag.TAG_LIST)) {
            upgrades = new ArrayList<>();
            ListTag list = tag.getList("upgrades", Tag.TAG_COMPOUND);
            for (int index = 0; index < list.size(); index++) {
                ItemStack upgrade = ItemStack.of(list.getCompound(index));
                if (!upgrade.isEmpty()) {
                    upgrades.add(upgrade);
                }
            }
        }
        return new StoredConfiguration(
                handler, readName(tag.getString(NAME)), tag.getCompound(DATA), tag.getBoolean("regular"), upgrades);
    }

    public void write(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putString(HANDLER, handler.toString());
        String json = Component.Serializer.toJson(name);
        tag.putString(NAME, json == null ? "" : json);
        tag.put(DATA, data);
        tag.putBoolean("regular", regular);
        if (upgrades != null) {
            ListTag list = new ListTag();
            for (ItemStack upgrade : upgrades) {
                if (!upgrade.isEmpty()) {
                    list.add(upgrade.save(new CompoundTag()));
                }
            }
            tag.put("upgrades", list);
        }
        stack.getOrCreateTag().put(TAG, tag);
    }

    private static Component readName(String json) {
        try {
            Component name = Component.Serializer.fromJson(json);
            return name == null ? Component.empty() : name;
        } catch (JsonParseException e) {
            return Component.empty();
        }
    }
}
