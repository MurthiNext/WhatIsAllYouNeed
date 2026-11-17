package com.murthinext.wiayn.item.configuration_card;

import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record StoredConfiguration(ResourceLocation handler, Component name, CompoundTag data) {

    public static final String TAG = "configuration";
    private static final String HANDLER = "handler";
    private static final String NAME = "name";
    private static final String DATA = "data";

    public static StoredConfiguration of(ConfigurationSnapshot snapshot) {
        return new StoredConfiguration(snapshot.handler(), snapshot.name(), snapshot.data());
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
        return new StoredConfiguration(handler, readName(tag.getString(NAME)), tag.getCompound(DATA));
    }

    public void write(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putString(HANDLER, handler.toString());
        String json = Component.Serializer.toJson(name);
        tag.putString(NAME, json == null ? "" : json);
        tag.put(DATA, data);
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
