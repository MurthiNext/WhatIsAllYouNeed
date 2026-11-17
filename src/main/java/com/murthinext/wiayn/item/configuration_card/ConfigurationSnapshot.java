package com.murthinext.wiayn.item.configuration_card;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 存放在配置卡上的方块配置快照。
 *
 * @param handler 产出该数据的处理器 id
 * @param name 被复制方块的显示名称
 * @param data 处理器自定义的数据
 */
public record ConfigurationSnapshot(ResourceLocation handler, Component name, CompoundTag data) {}
