package com.clussmanproductions.trafficcontrol.tileentity;

import com.clussmanproductions.trafficcontrol.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// A minimal block entity that exists solely to enable a custom renderer.
// The rotation is stored in the BlockState (ROTATION property), not in the block entity.
// Think of this like an empty class that just says "hey, I need a custom renderer."
public class RotatableBlockEntity extends BlockEntity {

    public RotatableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROTATABLE.get(), pos, state);
    }

    protected RotatableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
