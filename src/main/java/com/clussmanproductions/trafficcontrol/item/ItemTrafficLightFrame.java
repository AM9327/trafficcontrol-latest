package com.clussmanproductions.trafficcontrol.item;

import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGateBase;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGatePole;
import com.clussmanproductions.trafficcontrol.blocks.BlockHorizontalPole;
import com.clussmanproductions.trafficcontrol.blocks.BlockTrafficLight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;

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

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Block clickedBlock = level.getBlockState(clickedPos).getBlock();

        // When clicking on a crossing gate pole or base, and the adjacent position
        // in the clicked direction is a horizontal pole, skip over it and place
        // the TL on the far side of the HP
        if (clickedBlock instanceof BlockCrossingGatePole
                || clickedBlock instanceof BlockCrossingGateBase) {
            BlockPos adjacentPos = clickedPos.relative(clickedFace);
            Block adjacentBlock = level.getBlockState(adjacentPos).getBlock();
            if (adjacentBlock instanceof BlockHorizontalPole) {
                BlockPos targetPos = adjacentPos.relative(clickedFace);
                if (level.getBlockState(targetPos).canBeReplaced()) {
                    return placeTLAt(context, level, targetPos);
                }
            }
        }

        // When clicking on a horizontal pole, place the TL on the clicked face.
        // If that position is occupied, search other faces for an empty spot.
        if (clickedBlock instanceof BlockHorizontalPole) {
            // Try the clicked face first
            BlockPos targetPos = clickedPos.relative(clickedFace);
            if (level.getBlockState(targetPos).canBeReplaced()) {
                return placeTLAt(context, level, targetPos);
            }
            // Clicked face occupied — search all horizontal faces for an empty position
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                targetPos = clickedPos.relative(dir);
                if (level.getBlockState(targetPos).canBeReplaced()) {
                    return placeTLAt(context, level, targetPos);
                }
            }
        }

        return super.useOn(context);
    }

    private InteractionResult placeTLAt(UseOnContext context, Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var player = context.getPlayer();
        if (player == null) return InteractionResult.FAIL;

        int rotation = RotationSegment.convertToSegment(player.getYRot() + 180.0F);
        BlockState state = this.getBlock().defaultBlockState()
                .setValue(BlockStateProperties.ROTATION_16, rotation)
                .setValue(BlockTrafficLight.HAS_HORIZONTAL_BAR, true)
                .setValue(BlockTrafficLight.PAIRED_ACROSS_POLE, false);

        level.setBlock(pos, state, 3);

        if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
