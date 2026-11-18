package com.murthinext.wiayn.item.multi_config_card.client;

import com.murthinext.wiayn.api.Ids;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class MultiConfigCardScreen extends AbstractContainerScreen<MultiConfigCardMenu> {

    private static final int BACKGROUND = 0xFF101418;
    private static final int BORDER = 0xFF5A7A9A;
    private static final int ACCENT = 0xFF8ABBEF;
    private static final int ACCENT_DARK = 0x80294057;
    private static final int SEPARATOR = 0xFF2C3A46;
    private static final int TEXT = 0xFFAAB8C6;
    private static final int TEXT_DISABLED = 0xFF7A8794;
    private static final int TITLE_COLOR = 0xFF55FFFF;
    private static final int SLOT_SIZE = 18;
    private static final ResourceLocation SLOT_TEXTURE = Ids.of("textures/gui/components/slot.png");
    private final List<OptionBox> modeBoxes = new ArrayList<>();

    public MultiConfigCardScreen(MultiConfigCardMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 200;
        titleLabelX = 7;
        titleLabelY = 9;
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
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, BORDER);
        graphics.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, BACKGROUND);
        int dividerY = topPos + MultiConfigCardMenu.INVENTORY_Y - 3;
        graphics.fill(leftPos + 1, dividerY, leftPos + imageWidth - 1, dividerY + 1, BORDER);
        graphics.fill(leftPos + 7, topPos + 21, leftPos + imageWidth - 8, topPos + 22, SEPARATOR);
        graphics.fill(leftPos + 7, topPos + 21, leftPos + 19, topPos + 22, ACCENT);
        graphics.fill(leftPos + 7, topPos + 22, leftPos + 19, topPos + 23, ACCENT_DARK);
        for (Slot slot : menu.slots) {
            graphics.blit(
                    SLOT_TEXTURE,
                    leftPos + slot.x - 1,
                    topPos + slot.y - 1,
                    0,
                    0.0F,
                    0.0F,
                    SLOT_SIZE,
                    SLOT_SIZE,
                    SLOT_SIZE,
                    SLOT_SIZE);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, TITLE_COLOR, false);
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
            int color = !active ? TEXT_DISABLED : isHoveredOrFocused() ? TITLE_COLOR : ACCENT;
            int x = getX();
            int y = getY() + 2;
            graphics.fill(x, y, x + 10, y + 10, color);
            graphics.fill(x + 1, y + 1, x + 9, y + 9, BACKGROUND);
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
