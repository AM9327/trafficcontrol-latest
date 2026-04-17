package com.clussmanproductions.trafficcontrol.blocks;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockBell extends Block {

    private final SoundEvent ringSound;

    public BlockBell(BlockBehaviour.Properties properties, String soundName) {
        super(properties);
        this.ringSound = SoundEvent.createVariableRangeEvent(
                Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, soundName));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        level.playSound(null, pos, ringSound, SoundSource.BLOCKS, 2.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }
}
