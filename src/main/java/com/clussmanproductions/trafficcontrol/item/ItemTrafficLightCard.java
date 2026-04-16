package com.clussmanproductions.trafficcontrol.item;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ItemTrafficLightCard extends Item {

    private final String tooltipKey;

    public ItemTrafficLightCard(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        var window = Minecraft.getInstance().getWindow();
        if (InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT) || InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT)) {
            tooltipAdder.accept(Component.translatable(this.tooltipKey).withStyle(ChatFormatting.GRAY));
        } else {
            tooltipAdder.accept(Component.translatable("trafficcontrol.tooltip.help").withStyle(ChatFormatting.YELLOW));
        }
    }
}
