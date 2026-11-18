package com.murthinext.wiayn.item.multi_config_card;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MultiConfigCardItem extends Item {

    public MultiConfigCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return interact(stack, context);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return interact(context.getItemInHand(), context);
    }

    private static InteractionResult interact(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        ConfigurationTarget target = ConfigurationTarget.of(level, context.getClickedPos(), context.getClickedFace());
        return player.isShiftKeyDown() ? copy(level, stack, target, player) : paste(level, stack, target, player);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && StoredConfiguration.read(stack) != null) {
            if (!level.isClientSide) {
                stack.removeTagKey(StoredConfiguration.TAG);
                player.sendSystemMessage(Component.translatable("message.wiayn.multi_config_card.cleared")
                        .withStyle(ChatFormatting.YELLOW));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        StoredConfiguration stored = StoredConfiguration.read(stack);
        if (stored == null) {
            tooltip.add(Component.translatable("tooltip.wiayn.multi_config_card.empty")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable(
                            "tooltip.wiayn.multi_config_card.stored",
                            stored.name().copy().withStyle(ChatFormatting.AQUA))
                    .withStyle(ChatFormatting.GRAY));
            if (!ConfigurationHandlerRegistry.isRegistered(stored.handler())) {
                tooltip.add(Component.translatable(
                                "tooltip.wiayn.multi_config_card.missing_handler",
                                stored.handler().toString())
                        .withStyle(ChatFormatting.RED));
            }
        }
        tooltip.add(
                Component.translatable("tooltip.wiayn.multi_config_card.help").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static InteractionResult copy(Level level, ItemStack stack, ConfigurationTarget target, Player player) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ConfigurationSnapshot snapshot = ConfigurationHandlerRegistry.copy(target, player);
        if (snapshot == null) {
            player.sendSystemMessage(Component.translatable("message.wiayn.multi_config_card.unsupported")
                    .withStyle(ChatFormatting.RED));
        } else {
            StoredConfiguration.of(snapshot).write(stack);
            player.sendSystemMessage(
                    Component.translatable("message.wiayn.multi_config_card.copied", styledName(snapshot.name()))
                            .withStyle(ChatFormatting.GREEN));
        }
        return InteractionResult.CONSUME;
    }

    private static InteractionResult paste(Level level, ItemStack stack, ConfigurationTarget target, Player player) {
        StoredConfiguration stored = StoredConfiguration.read(stack);
        if (stored == null) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ConfigurationHandler handler = ConfigurationHandlerRegistry.get(stored.handler());
        if (handler == null) {
            player.sendSystemMessage(Component.translatable(
                            "message.wiayn.multi_config_card.missing_handler",
                            stored.handler().toString())
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.CONSUME;
        }
        if (!handler.canPaste(target, player, stored.data())) {
            player.sendSystemMessage(Component.translatable("message.wiayn.multi_config_card.incompatible")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.CONSUME;
        }
        handler.paste(target, player, stored.data());
        player.sendSystemMessage(
                Component.translatable("message.wiayn.multi_config_card.applied", styledName(stored.name()))
                        .withStyle(ChatFormatting.GREEN));
        return InteractionResult.CONSUME;
    }

    private static Component styledName(Component name) {
        return name.copy().withStyle(ChatFormatting.AQUA);
    }
}
