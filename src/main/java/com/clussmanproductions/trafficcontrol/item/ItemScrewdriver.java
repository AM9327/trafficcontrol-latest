package com.clussmanproductions.trafficcontrol.item;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.ChatFormatting;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import java.util.function.Consumer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ItemScrewdriver extends Item {

    private static final SoundEvent SCREWDRIVER_SOUND =
            SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "screwdriver"));

    public ItemScrewdriver(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        var window = Minecraft.getInstance().getWindow();
        if (InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT) || InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT)) {
            tooltipAdder.accept(Component.translatable("trafficcontrol.tooltip.screwdriver").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipAdder.accept(Component.translatable("trafficcontrol.tooltip.help").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);

        if (player == null) {
            return InteractionResult.PASS;
        }

        // Only affect blocks from this mod
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String namespace = blockId.getNamespace();
        if (!namespace.equals(ModTrafficControl.MODID)) {
            return InteractionResult.PASS;
        }

        // Check if block is rotatable before doing anything
        boolean canRotate = state.hasProperty(BlockStateProperties.ROTATION_16)
                || state.hasProperty(BlockStateProperties.HORIZONTAL_FACING);
        if (!canRotate) {
            return InteractionResult.PASS;
        }

        // Client side: consume the interaction to prevent block GUI from opening
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        boolean rotated = false;

        if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
            int rotation = state.getValue(BlockStateProperties.ROTATION_16);
            rotation += player.isShiftKeyDown() ? -1 : 1;
            if (rotation < 0) rotation = 15;
            if (rotation > 15) rotation = 0;
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.ROTATION_16, rotation));
            rotated = true;
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            var facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            var newFacing = player.isShiftKeyDown() ? facing.getCounterClockWise() : facing.getClockWise();
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing));
            rotated = true;
        }

        if (rotated) {
            level.playSound(null, pos, SCREWDRIVER_SOUND, SoundSource.BLOCKS, 0.25f, 1.0f);
            if (!player.getAbilities().instabuild) {
                context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
