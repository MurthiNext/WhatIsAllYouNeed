package com.murthinext.wiayn.item.multi_config_card.client;

import com.murthinext.nextui.client.ContainerChrome;
import com.murthinext.nextui.client.GuiPanels;
import com.murthinext.nextui.client.GuiTheme;
import com.murthinext.nextui.layout.PlayerInventoryLayout;
import com.murthinext.wiayn.item.multi_config_card.MultiConfigCardMenu;
import com.murthinext.wiayn.item.multi_config_card.UpgradeMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class MultiConfigCardScreen extends AbstractContainerScreen<MultiConfigCardMenu> {

    private static final int TEXT = 0xFFAAB8C6;
    private static final int TEXT_DISABLED = 0xFF7A8794;
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
        addRenderableWidget(new OptionBox(
                leftPos + 8,
                topPos + 28,
                "screen.wiayn.multi_config_card.regular",
                () -> menu.options().regular(),
                () -> click(0)));
        addRenderableWidget(new OptionBox(
                leftPos + 8,
                topPos + 44,
                "screen.wiayn.multi_config_card.upgrades",
                () -> menu.options().upgrades(),
                () -> click(1)));
        for (UpgradeMode mode : UpgradeMode.values()) {
            OptionBox box = new OptionBox(
                    leftPos + 16,
                    topPos + 76 + mode.ordinal() * 14,
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

    private final class OptionBox extends AbstractButton {

        private final BooleanSupplier selected;
        private final Runnable action;
        private final Tooltip optionTooltip;

        private OptionBox(int x, int y, String key, BooleanSupplier selected, Runnable action) {
            super(x, y, imageWidth - (x - leftPos) - 8, 14, Component.translatable(key));
            this.selected = selected;
            this.action = action;
            optionTooltip = Tooltip.create(Component.translatable(key + ".tip"));
        }

        @Override
        public void onPress() {
            action.run();
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int color = !active
                    ? TEXT_DISABLED
                    : isHoveredOrFocused() ? GuiTheme.DEFAULT.titleColor() : GuiTheme.DEFAULT.accent();
            int x = getX();
            int y = getY() + 2;
            graphics.fill(x, y, x + 10, y + 10, color);
            graphics.fill(x + 1, y + 1, x + 9, y + 9, GuiPanels.COLOR_BACKGROUND);
            if (selected.getAsBoolean()) {
                graphics.fill(x + 3, y + 3, x + 7, y + 7, color);
            }
            int textX = x + 16;
            int textY = getY() + 3;
            graphics.drawString(font, getMessage(), textX, textY, active ? TEXT : TEXT_DISABLED, false);
            boolean textHovered = mouseX >= textX
                    && mouseX < textX + font.width(getMessage())
                    && mouseY >= textY
                    && mouseY < textY + font.lineHeight;
            boolean keyboardFocused = isFocused()
                    && minecraft != null
                    && minecraft.getLastInputType().isKeyboard();
            setTooltip(textHovered || keyboardFocused ? optionTooltip : null);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
            output.add(
                    net.minecraft.client.gui.narration.NarratedElementType.HINT,
                    Component.translatable(
                            "screen.wiayn.multi_config_card.option_state",
                            getMessage(),
                            Component.translatable(selected.getAsBoolean() ? "options.on" : "options.off")));
        }
    }
}
