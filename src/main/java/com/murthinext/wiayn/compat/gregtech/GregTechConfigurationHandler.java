package com.murthinext.wiayn.compat.gregtech;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IAutoOutputFluid;
import com.gregtechceu.gtceu.api.machine.feature.IAutoOutputItem;
import com.gregtechceu.gtceu.api.machine.feature.IMufflableMachine;
import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationHandler;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationHandlerRegistry;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationSnapshot;
import com.murthinext.wiayn.item.multi_config_card.ConfigurationTarget;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class GregTechConfigurationHandler implements ConfigurationHandler {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("gtceu", "memory_card");

    private static final String ITEM_OUTPUT_SIDE = "item_output_side";
    private static final String ITEM_AUTO_OUTPUT = "item_auto_output";
    private static final String ITEM_ALLOW_INPUT = "item_allow_input_from_output";
    private static final String FLUID_OUTPUT_SIDE = "fluid_output_side";
    private static final String FLUID_AUTO_OUTPUT = "fluid_auto_output";
    private static final String FLUID_ALLOW_INPUT = "fluid_allow_input_from_output";
    private static final String MUFFLED = "muffled";

    private static final GregTechConfigurationHandler INSTANCE = new GregTechConfigurationHandler();

    private GregTechConfigurationHandler() {}

    public static void register() {
        ConfigurationHandlerRegistry.register(ID, INSTANCE);
    }

    @Override
    public boolean canCopy(ConfigurationTarget target, Player player) {
        MetaMachine machine = machine(target);
        return machine != null && MachineOwner.canOpenOwnerMachine(player, machine);
    }

    @Nullable
    @Override
    public ConfigurationSnapshot copy(ConfigurationTarget target, Player player, boolean regular) {
        MetaMachine machine = machine(target);
        if (machine == null) {
            return null;
        }
        CompoundTag data = new CompoundTag();
        if (!regular) {
            return new ConfigurationSnapshot(ID, target.displayName(), data);
        }
        if (machine instanceof IAutoOutputItem itemOutput) {
            Direction side = itemOutput.getOutputFacingItems();
            if (side != null) {
                data.putString(ITEM_OUTPUT_SIDE, side.getName());
            }
            data.putBoolean(ITEM_AUTO_OUTPUT, itemOutput.isAutoOutputItems());
            data.putBoolean(ITEM_ALLOW_INPUT, itemOutput.isAllowInputFromOutputSideItems());
        }
        if (machine instanceof IAutoOutputFluid fluidOutput) {
            Direction side = fluidOutput.getOutputFacingFluids();
            if (side != null) {
                data.putString(FLUID_OUTPUT_SIDE, side.getName());
            }
            data.putBoolean(FLUID_AUTO_OUTPUT, fluidOutput.isAutoOutputFluids());
            data.putBoolean(FLUID_ALLOW_INPUT, fluidOutput.isAllowInputFromOutputSideFluids());
        }
        if (machine instanceof IMufflableMachine mufflable) {
            data.putBoolean(MUFFLED, mufflable.isMuffled());
        }
        return data.isEmpty() ? null : new ConfigurationSnapshot(ID, target.displayName(), data);
    }

    @Override
    public boolean canPaste(ConfigurationTarget target, Player player, CompoundTag data) {
        MetaMachine machine = machine(target);
        return !data.isEmpty() && machine != null && MachineOwner.canOpenOwnerMachine(player, machine);
    }

    @Override
    public void paste(ConfigurationTarget target, Player player, CompoundTag data) {
        MetaMachine machine = machine(target);
        if (machine == null) {
            return;
        }
        if (machine instanceof IAutoOutputItem itemOutput) {
            if (data.contains(ITEM_OUTPUT_SIDE)) {
                Direction side = readDirection(data, ITEM_OUTPUT_SIDE);
                if (side != null) {
                    itemOutput.setOutputFacingItems(side);
                }
            }
            if (data.contains(ITEM_AUTO_OUTPUT)) {
                itemOutput.setAutoOutputItems(data.getBoolean(ITEM_AUTO_OUTPUT));
            }
            if (data.contains(ITEM_ALLOW_INPUT)) {
                itemOutput.setAllowInputFromOutputSideItems(data.getBoolean(ITEM_ALLOW_INPUT));
            }
        }
        if (machine instanceof IAutoOutputFluid fluidOutput) {
            if (data.contains(FLUID_OUTPUT_SIDE)) {
                Direction side = readDirection(data, FLUID_OUTPUT_SIDE);
                if (side != null) {
                    fluidOutput.setOutputFacingFluids(side);
                }
            }
            if (data.contains(FLUID_AUTO_OUTPUT)) {
                fluidOutput.setAutoOutputFluids(data.getBoolean(FLUID_AUTO_OUTPUT));
            }
            if (data.contains(FLUID_ALLOW_INPUT)) {
                fluidOutput.setAllowInputFromOutputSideFluids(data.getBoolean(FLUID_ALLOW_INPUT));
            }
        }
        if (machine instanceof IMufflableMachine mufflable && data.contains(MUFFLED)) {
            mufflable.setMuffled(data.getBoolean(MUFFLED));
        }
    }

    @Nullable
    private static MetaMachine machine(ConfigurationTarget target) {
        return target.blockEntity() instanceof IMachineBlockEntity blockEntity ? blockEntity.getMetaMachine() : null;
    }

    @Nullable
    private static Direction readDirection(CompoundTag data, String key) {
        return Direction.byName(data.getString(key));
    }
}
