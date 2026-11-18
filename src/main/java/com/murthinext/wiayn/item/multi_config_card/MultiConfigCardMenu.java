package com.murthinext.wiayn.item.multi_config_card;

import com.murthinext.nextui.layout.PlayerInventoryLayout;
import com.murthinext.wiayn.registry.ModMenus;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class MultiConfigCardMenu extends AbstractContainerMenu {

    private final InteractionHand hand;
    private final ItemStack card;
    private final int cardSlot;
    private final SimpleContainerData data = new SimpleContainerData(3);

    public MultiConfigCardMenu(int containerId, Inventory inventory, InteractionHand hand) {
        super(ModMenus.MULTI_CONFIG_CARD.get(), containerId);
        this.hand = hand;
        card = inventory.player.getItemInHand(hand);
        cardSlot = hand == InteractionHand.MAIN_HAND ? inventory.selected : Inventory.SLOT_OFFHAND;
        ConfigurationOptions options = ConfigurationOptions.read(card);
        data.set(0, options.regular() ? 1 : 0);
        data.set(1, options.upgrades() ? 1 : 0);
        data.set(2, options.upgradeMode().ordinal());
        addDataSlots(data);
        PlayerInventoryLayout.STANDARD.forEachSlot((index, x, y) -> addSlot(new Slot(inventory, index, x, y) {
            @Override
            public boolean mayPickup(Player player) {
                return getSlotIndex() != cardSlot && super.mayPickup(player);
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return getSlotIndex() != cardSlot && super.mayPlace(stack);
            }
        }));
    }

    public ConfigurationOptions options() {
        int mode = data.get(2);
        UpgradeMode[] modes = UpgradeMode.values();
        return new ConfigurationOptions(
                data.get(0) != 0,
                data.get(1) != 0,
                mode >= 0 && mode < modes.length ? modes[mode] : UpgradeMode.BEST_EFFORT);
    }

    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (!stillValid(player)) {
            return false;
        }
        if (button == 0 || button == 1) {
            data.set(button, data.get(button) == 0 ? 1 : 0);
        } else if (button >= 2
                && button < 2 + UpgradeMode.values().length
                && options().upgrades()) {
            data.set(2, button - 2);
        } else {
            return false;
        }
        if (!player.level().isClientSide) {
            options().write(card);
            player.getInventory().setChanged();
            if (hand == InteractionHand.OFF_HAND && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, cardSlot, card));
            }
            broadcastChanges();
        }
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive()
                && player.getItemInHand(hand).getItem() instanceof MultiConfigCardItem
                && (player.level().isClientSide || player.getItemInHand(hand) == card);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (!stillValid(player) || clickType == ClickType.SWAP && button == cardSlot) {
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (!stillValid(player) || index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        boolean moved = index < PlayerInventoryLayout.MAIN_SLOTS
                ? moveItemStackTo(stack, PlayerInventoryLayout.MAIN_SLOTS, PlayerInventoryLayout.SLOT_COUNT, false)
                : moveItemStackTo(stack, 0, PlayerInventoryLayout.MAIN_SLOTS, false);
        if (!moved) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stack);
        return original;
    }
}
