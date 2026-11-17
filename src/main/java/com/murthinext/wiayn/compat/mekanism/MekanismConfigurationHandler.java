package com.murthinext.wiayn.compat.mekanism;

import com.murthinext.wiayn.item.configuration_card.ConfigurationHandlerRegistry;
import com.murthinext.wiayn.item.configuration_card.ConfigurationSnapshot;
import com.murthinext.wiayn.item.configuration_card.ConfigurationTarget;
import com.murthinext.wiayn.item.configuration_card.ConfigurationHandler;
import mekanism.api.IConfigCardAccess;
import mekanism.api.security.ISecurityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class MekanismConfigurationHandler implements ConfigurationHandler {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("mekanism", "config_card");

    private static final String DATA_TYPE = "wiayn:data_type";

    private static final Capability<IConfigCardAccess> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private static final MekanismConfigurationHandler INSTANCE = new MekanismConfigurationHandler();

    private MekanismConfigurationHandler() {}

    public static void register() {
        ConfigurationHandlerRegistry.register(ID, INSTANCE);
    }

    @Override
    public boolean canCopy(ConfigurationTarget target, Player player) {
        return access(target) != null && canAccess(target, player);
    }

    @Nullable
    @Override
    public ConfigurationSnapshot copy(ConfigurationTarget target, Player player) {
        IConfigCardAccess access = access(target);
        if (access == null) {
            return null;
        }
        BlockEntityType<?> type = access.getConfigurationDataType();
        ResourceLocation typeId = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(type);
        if (typeId == null) {
            return null;
        }
        CompoundTag data = access.getConfigurationData(player);
        data.putString(DATA_TYPE, typeId.toString());
        return new ConfigurationSnapshot(ID, Component.translatable(access.getConfigCardName()), data);
    }

    @Override
    public boolean canPaste(ConfigurationTarget target, Player player, CompoundTag data) {
        IConfigCardAccess access = access(target);
        if (access == null) {
            return false;
        }
        BlockEntityType<?> type = readType(data);
        return type != null && access.isConfigurationDataCompatible(type) && canAccess(target, player);
    }

    @Override
    public void paste(ConfigurationTarget target, Player player, CompoundTag data) {
        IConfigCardAccess access = access(target);
        if (access != null) {
            access.setConfigurationData(player, data);
            access.configurationDataSet();
        }
    }

    @Nullable
    private static IConfigCardAccess access(ConfigurationTarget target) {
        return target.capability(CAPABILITY);
    }

    private static boolean canAccess(ConfigurationTarget target, Player player) {
        BlockEntity blockEntity = target.blockEntity();
        return blockEntity != null && ISecurityUtils.INSTANCE.canAccessOrDisplayError(player, blockEntity);
    }

    @Nullable
    private static BlockEntityType<?> readType(CompoundTag data) {
        ResourceLocation typeId = ResourceLocation.tryParse(data.getString(DATA_TYPE));
        return typeId == null ? null : ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(typeId);
    }
}
