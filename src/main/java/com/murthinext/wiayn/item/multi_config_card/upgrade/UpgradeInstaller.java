package com.murthinext.wiayn.item.multi_config_card.upgrade;

import com.murthinext.wiayn.item.multi_config_card.UpgradeMode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public final class UpgradeInstaller {

    private UpgradeInstaller() {}

    public static void apply(Player player, UpgradeInventory upgrades, List<ItemStack> desired, UpgradeMode mode) {
        UpgradeInventory simulation = upgrades.simulate(mode == UpgradeMode.EXACT);
        if (simulation == null) {
            message(player, "upgrade_unsupported", ChatFormatting.RED);
            return;
        }
        List<ItemStack> current = upgrades.contents();
        List<ItemStack> retained = current.stream().map(ItemStack::copy).toList();
        List<ItemStack> returned = new ArrayList<>();
        if (mode == UpgradeMode.EXACT) {
            current.forEach(stack -> returned.add(stack.copy()));
        }
        Inventory inventory = player.getInventory();
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < inventory.items.size(); slot++) {
            slots.add(slot);
        }
        slots.add(inventory.getContainerSize() - 1);
        List<ItemStack> materials =
                slots.stream().map(slot -> inventory.getItem(slot).copy()).toList();
        List<ItemStack> missing = new ArrayList<>();
        List<ItemStack> rejected = new ArrayList<>();
        int installed = 0;
        for (ItemStack recorded : desired) {
            if (recorded.isEmpty()) {
                continue;
            }
            int needed = recorded.getCount();
            if (mode != UpgradeMode.EXACT) {
                needed -= take(retained, recorded, needed);
            }
            if (needed == 0) {
                continue;
            }
            int available = player.getAbilities().instabuild
                    ? needed
                    : Math.min(needed, count(returned, recorded) + count(materials, recorded));
            if (available < needed) {
                missing.add(recorded.copyWithCount(needed - available));
            }
            if (available == 0) {
                continue;
            }
            int accepted = simulation.insert(recorded.copyWithCount(available));
            if (accepted < available) {
                rejected.add(recorded.copyWithCount(available - accepted));
            }
            int consumed = take(returned, recorded, accepted);
            if (!player.getAbilities().instabuild) {
                take(materials, recorded, accepted - consumed);
            }
            installed += accepted;
        }
        boolean complete = missing.isEmpty() && rejected.isEmpty();
        if (!complete && mode != UpgradeMode.BEST_EFFORT) {
            message(player, "upgrade_aborted", ChatFormatting.RED);
        } else {
            if (installed > 0 || mode == UpgradeMode.EXACT) {
                upgrades.apply(simulation.contents());
                if (!player.getAbilities().instabuild) {
                    for (int index = 0; index < slots.size(); index++) {
                        int slot = slots.get(index);
                        ItemStack remaining = materials.get(index);
                        if (!ItemStack.matches(inventory.getItem(slot), remaining)) {
                            inventory.setItem(slot, remaining);
                        }
                    }
                }
                for (ItemStack stack : returned) {
                    if (!stack.isEmpty()) {
                        ItemHandlerHelper.giveItemToPlayer(player, stack);
                    }
                }
                inventory.setChanged();
                player.inventoryMenu.broadcastChanges();
            }
            message(
                    player,
                    complete ? "upgrades_applied" : "upgrades_partial",
                    complete ? ChatFormatting.GREEN : ChatFormatting.YELLOW,
                    installed);
        }
        for (ItemStack stack : missing) {
            message(player, "upgrade_missing", ChatFormatting.YELLOW, stack.getHoverName(), stack.getCount());
        }
        for (ItemStack stack : rejected) {
            message(player, "upgrade_rejected", ChatFormatting.YELLOW, stack.getHoverName(), stack.getCount());
        }
    }

    private static int count(List<ItemStack> stacks, ItemStack requested) {
        int count = 0;
        for (ItemStack stack : stacks) {
            if (ItemStack.isSameItemSameTags(stack, requested)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static int take(List<ItemStack> stacks, ItemStack requested, int amount) {
        int remaining = amount;
        for (ItemStack stack : stacks) {
            if (remaining > 0 && ItemStack.isSameItemSameTags(stack, requested)) {
                int taken = Math.min(remaining, stack.getCount());
                stack.shrink(taken);
                remaining -= taken;
            }
        }
        return amount - remaining;
    }

    private static void message(Player player, String key, ChatFormatting color, Object... arguments) {
        player.sendSystemMessage(Component.translatable("message.wiayn.multi_config_card." + key, arguments)
                .withStyle(color));
    }
}
