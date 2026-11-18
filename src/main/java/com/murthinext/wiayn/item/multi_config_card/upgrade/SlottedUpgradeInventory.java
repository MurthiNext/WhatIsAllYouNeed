package com.murthinext.wiayn.item.multi_config_card.upgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public final class SlottedUpgradeInventory implements UpgradeInventory {

    private final IItemHandler inventory;
    private final BiConsumer<Integer, ItemStack> setter;
    private final Supplier<SlottedUpgradeInventory> simulationFactory;
    private final Runnable changed;

    public SlottedUpgradeInventory(
            IItemHandler inventory,
            BiConsumer<Integer, ItemStack> setter,
            Supplier<SlottedUpgradeInventory> simulationFactory,
            Runnable changed) {
        this.inventory = inventory;
        this.setter = setter;
        this.simulationFactory = simulationFactory;
        this.changed = changed;
    }

    @Override
    public List<ItemStack> contents() {
        List<ItemStack> contents = new ArrayList<>(inventory.getSlots());
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            contents.add(inventory.getStackInSlot(slot).copy());
        }
        return contents;
    }

    @Nullable
    @Override
    public UpgradeInventory simulate(boolean clear) {
        SlottedUpgradeInventory simulation = simulationFactory.get();
        if (simulation == null || simulation.inventory.getSlots() != inventory.getSlots()) {
            return null;
        }
        if (!clear) {
            simulation.apply(contents());
        }
        return simulation;
    }

    @Override
    public int insert(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < inventory.getSlots() && !remainder.isEmpty(); slot++) {
            if (inventory.isItemValid(slot, remainder)) {
                remainder = inventory.insertItem(slot, remainder, false);
            }
        }
        return stack.getCount() - remainder.getCount();
    }

    @Override
    public void apply(List<ItemStack> contents) {
        if (contents.size() != inventory.getSlots()) {
            throw new IllegalArgumentException("Mismatched upgrade slot count");
        }
        for (int slot = 0; slot < contents.size(); slot++) {
            setter.accept(slot, contents.get(slot).copy());
        }
        changed.run();
    }
}
