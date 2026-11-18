package com.murthinext.wiayn.item.multi_config_card;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

public record ConfigurationTarget(
        Level level,
        BlockPos pos,
        BlockState state,
        @Nullable BlockEntity blockEntity,
        @Nullable Direction face) {

    public static ConfigurationTarget of(Level level, BlockPos pos, @Nullable Direction face) {
        return new ConfigurationTarget(level, pos, level.getBlockState(pos), level.getBlockEntity(pos), face);
    }

    public Component displayName() {
        return state.getBlock().getName();
    }

    @Nullable
    public <T> T capability(Capability<T> capability) {
        return blockEntity == null
                ? null
                : blockEntity.getCapability(capability, face).resolve().orElse(null);
    }
}
