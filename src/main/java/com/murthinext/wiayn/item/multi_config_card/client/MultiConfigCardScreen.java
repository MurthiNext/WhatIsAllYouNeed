package com.murthinext.wiayn.item.multi_config_card.client;

import com.murthinext.nextui.client.ContainerChrome;
import com.murthinext.nextui.client.GuiTheme;
import com.murthinext.nextui.client.OptionBox;
import com.murthinext.nextui.layout.PlayerInventoryLayout;
import com.murthinext.wiayn.item.multi_config_card.MultiConfigCardMenu;
import com.murthinext.wiayn.item.multi_config_card.UpgradeMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class MultiConfigCardScreen extends AbstractContainerScreen<MultiConfigCardMenu> {

    private static final int TEXT = 0xFFAAB8C6;
    private final List<OptionBox> modeBoxes = new ArrayList<>();

    public MultiConfigCardScreen(MultiConfigCardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = ContainerChrome.DEFAULT_WIDTH;
        imageHeight = ContainerChrome.DEFAULT_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        modeBoxes.clear();
        addRenderableWidget(optionBox(
                8,
                28,
                "screen.wiayn.multi_config_card.regular",
                () -> menu.options().regular(),
                () -> click(0)));
        addRenderableWidget(optionBox(
                8,
                44,
                "screen.wiayn.multi_config_card.upgrades",
                () -> menu.options().upgrades(),
                () -> click(1)));
        for (UpgradeMode mode : UpgradeMode.values()) {
            OptionBox box = optionBox(
                    16,
                    76 + mode.ordinal() * 14,
                    mode.translationKey(),
                    () -> menu.options().upgradeMode() == mode,
                    () -> {
                        if (menu.options().upgradeMode() != mode) {
                            click(2 + mode.ordinal());
                        }
                    });
            modeBoxes.add(addRenderableWidget(box));
        }
        updateModeBoxes();
    }

    @Override
    protected void containerTick() {
        updateModeBoxes();
    }

    private void updateModeBoxes() {
        for (OptionBox box : modeBoxes) {
            box.active = menu.options().upgrades();
        }
    }

    private void click(int button) {
        if (minecraft != null
                && minecraft.gameMode != null
                && minecraft.player != null
                && menu.clickMenuButton(minecraft.player, button)) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
            updateModeBoxes();
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        ContainerChrome.drawBackground(
                graphics, leftPos, topPos, imageWidth, imageHeight, PlayerInventoryLayout.STANDARD, GuiTheme.DEFAULT);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        ContainerChrome.drawTitle(graphics, font, title, GuiTheme.DEFAULT);
        graphics.drawString(
                font, Component.translatable("screen.wiayn.multi_config_card.installation"), 8, 63, TEXT, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private OptionBox optionBox(int x, int y, String key, BooleanSupplier selected, Runnable action) {
        return new OptionBox(
                leftPos + x,
                topPos + y,
                imageWidth - x - 8,
                Component.translatable(key),
                Component.translatable(key + ".tip"),
                selected,
                action);
    }
}
