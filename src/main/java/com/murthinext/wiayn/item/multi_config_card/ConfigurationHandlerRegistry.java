package com.murthinext.wiayn.item.multi_config_card;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class ConfigurationHandlerRegistry {

    private static final Map<ResourceLocation, ConfigurationHandler> HANDLERS = new LinkedHashMap<>();
    private static final List<ConfigurationHandler> ORDER = new ArrayList<>();

    private ConfigurationHandlerRegistry() {}

    public static void register(ResourceLocation id, ConfigurationHandler handler) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(handler, "handler");
        if (HANDLERS.putIfAbsent(id, handler) != null) {
            throw new IllegalStateException("Duplicate configuration handler " + id);
        }
        ORDER.add(handler);
    }

    @Nullable
    public static ConfigurationHandler get(ResourceLocation id) {
        return HANDLERS.get(id);
    }

    public static boolean isRegistered(ResourceLocation id) {
        return HANDLERS.containsKey(id);
    }

    @Nullable
    public static ConfigurationSnapshot copy(ConfigurationTarget target, Player player, boolean regular) {
        for (ConfigurationHandler handler : ORDER) {
            if (handler.canCopy(target, player)) {
                return handler.copy(target, player, regular);
            }
        }
        return null;
    }
}
