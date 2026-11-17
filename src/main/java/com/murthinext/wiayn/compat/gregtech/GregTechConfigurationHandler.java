package com.murthinext.wiayn.compat.gregtech;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMufflableMachine;
import com.gregtechceu.gtceu.api.machine.trait.AutoOutputTrait;
import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;
import com.murthinext.wiayn.item.configuration_card.ConfigurationHandler;
import com.murthinext.wiayn.item.configuration_card.ConfigurationHandlerRegistry;
import com.murthinext.wiayn.item.configuration_card.ConfigurationSnapshot;
import com.murthinext.wiayn.item.configuration_card.ConfigurationTarget;
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
    public ConfigurationSnapshot copy(ConfigurationTarget target, Player player) {
        MetaMachine machine = machine(target);
        if (machine == null) {
            return null;
        }
        CompoundTag data = new CompoundTag();

        AutoOutputTrait output = machine.getTraitHolder().getTrait(AutoOutputTrait.TYPE);
        if (output != null) {
            Direction itemSide = output.getItemOutputDirection();
            if (itemSide != null) {
                data.putString(ITEM_OUTPUT_SIDE, itemSide.getName());
                data.putBoolean(ITEM_AUTO_OUTPUT, output.isAutoOutputItems());
                data.putBoolean(ITEM_ALLOW_INPUT, output.allowsItemInputFromOutputSide());
            }
            Direction fluidSide = output.getFluidOutputDirection();
            if (fluidSide != null) {
                data.putString(FLUID_OUTPUT_SIDE, fluidSide.getName());
                data.putBoolean(FLUID_AUTO_OUTPUT, output.isAutoOutputFluids());
                data.putBoolean(FLUID_ALLOW_INPUT, output.allowsFluidInputFromOutputSide());
            }
        }
        if (machine instanceof IMufflableMachine mufflable) {
            data.putBoolean(MUFFLED, mufflable.isMuffled());
        }
        return new ConfigurationSnapshot(ID, target.displayName(), data);
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
        AutoOutputTrait output = machine.getTraitHolder().getTrait(AutoOutputTrait.TYPE);
        if (output != null) {
            if (data.contains(ITEM_OUTPUT_SIDE)) {
                output.setItemOutputDirection(readDirection(data, ITEM_OUTPUT_SIDE));
            }
            if (data.contains(ITEM_AUTO_OUTPUT)) {
                output.setAllowAutoOutputItems(data.getBoolean(ITEM_AUTO_OUTPUT));
            }
            if (data.contains(ITEM_ALLOW_INPUT)) {
                output.setAllowItemInputFromOutputSide(data.getBoolean(ITEM_ALLOW_INPUT));
            }
            if (data.contains(FLUID_OUTPUT_SIDE)) {
                output.setFluidOutputDirection(readDirection(data, FLUID_OUTPUT_SIDE));
            }
            if (data.contains(FLUID_AUTO_OUTPUT)) {
                output.setAllowAutoOutputFluids(data.getBoolean(FLUID_AUTO_OUTPUT));
            }
            if (data.contains(FLUID_ALLOW_INPUT)) {
                output.setAllowFluidInputFromOutputSide(data.getBoolean(FLUID_ALLOW_INPUT));
            }
        }
        if (machine instanceof IMufflableMachine mufflable && data.contains(MUFFLED)) {
            mufflable.setMuffled(data.getBoolean(MUFFLED));
        }
    }

    @Nullable
    private static MetaMachine machine(ConfigurationTarget target) {
        return target.blockEntity() instanceof MetaMachine machine ? machine : null;
    }

    @Nullable
    private static Direction readDirection(CompoundTag data, String key) {
        return Direction.byName(data.getString(key));
    }
}
