package com.clussmanproductions.trafficcontrol.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IHorizontalPoleConnectable {

    boolean canConnectHorizontalPole(BlockState state, Direction fromDirection);
}
