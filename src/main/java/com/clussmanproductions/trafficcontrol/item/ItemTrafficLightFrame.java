package com.clussmanproductions.trafficcontrol.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;

/**
 * A frame item that places a traffic light block when used.
 * Grants extended block interaction range when held so players can
 * place traffic lights on elevated poles more easily.
 */
public class ItemTrafficLightFrame extends BlockItem {

    private static final double EXTRA_RANGE = 3.5;

    public ItemTrafficLightFrame(Block block, Item.Properties properties) {
        super(block, properties.attributes(
                ItemAttributeModifiers.builder()
                        .add(Attributes.BLOCK_INTERACTION_RANGE,
                                new AttributeModifier(
                                        Identifier.withDefaultNamespace("traffic_light_range"),
                                        EXTRA_RANGE,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build()));
    }
}
