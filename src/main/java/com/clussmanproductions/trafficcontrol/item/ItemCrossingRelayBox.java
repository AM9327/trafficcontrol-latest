package com.clussmanproductions.trafficcontrol.item;

import com.clussmanproductions.trafficcontrol.ModBlocks;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class ItemCrossingRelayBox extends Item {

    public ItemCrossingRelayBox(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        var window = Minecraft.getInstance().getWindow();
        if (InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT) || InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT)) {
            tooltipAdder.accept(Component.translatable("trafficcontrol.tooltip.crossingrelay").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipAdder.accept(Component.translatable("trafficcontrol.tooltip.help").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos start = context.getClickedPos().relative(context.getClickedFace());

        BlockPos[] positions = new BlockPos[] {
                start,
                start.offset(1, 0, 0),
                start.offset(0, 0, 1),
                start.offset(1, 0, 1),
                start.offset(0, 1, 0),
                start.offset(1, 1, 0),
                start.offset(0, 1, 1),
                start.offset(1, 1, 1)
        };

        for (BlockPos pos : positions) {
            if (!level.getBlockState(pos).canBeReplaced()) {
                return InteractionResult.FAIL;
            }
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockState[] states = new BlockState[] {
                ModBlocks.CROSSING_RELAY_NW.get().defaultBlockState(),
                ModBlocks.CROSSING_RELAY_NE.get().defaultBlockState(),
                ModBlocks.CROSSING_RELAY_SW.get().defaultBlockState(),
                ModBlocks.CROSSING_RELAY_SE.get().defaultBlockState(),
                ModBlocks.CROSSING_RELAY_TOP_NW.get().defaultBlockState(),
                ModBlocks.CROSSING_RELAY_TOP_NE.get().defaultBlockState(),
                ModBlocks.CROSSING_RELAY_TOP_SW.get().defaultBlockState(),
                ModBlocks.CROSSING_RELAY_TOP_SE.get().defaultBlockState()
        };

        for (int i = 0; i < 8; i++) {
            level.setBlock(positions[i], states[i], 3);
        }

        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
