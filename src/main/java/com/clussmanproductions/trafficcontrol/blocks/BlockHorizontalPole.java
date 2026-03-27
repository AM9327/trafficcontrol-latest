package com.clussmanproductions.trafficcontrol.blocks;

import com.clussmanproductions.trafficcontrol.tileentity.RotatableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BlockHorizontalPole extends Block implements EntityBlock, IHorizontalPoleConnectable {

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

    // Shapes matching the actual pole model dimensions
    private static final VoxelShape SHAPE_NS = Block.box(5.5, 5.5, 0, 10.5, 10.5, 16);
    private static final VoxelShape SHAPE_EW = Block.box(0, 5.5, 5.5, 16, 10.5, 10.5);

    public BlockHorizontalPole(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(ROTATION,
                RotationSegment.convertToSegment(context.getRotation() + 180.0F));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int rotation = state.getValue(ROTATION);
        int snapped = ((rotation + 2) % 16) / 4;
        return (snapped == 1 || snapped == 3) ? SHAPE_EW : SHAPE_NS;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RotatableBlockEntity(pos, state);
    }

    @Override
    public boolean canConnectHorizontalPole(BlockState state, Direction fromDirection) {
        int rotation = state.getValue(ROTATION);
        int snapped = ((rotation + 2) % 16) / 4;
        boolean isNS = (snapped == 0 || snapped == 2);
        boolean isEW = (snapped == 1 || snapped == 3);

        if (isNS) {
            return fromDirection == Direction.NORTH || fromDirection == Direction.SOUTH;
        }
        if (isEW) {
            return fromDirection == Direction.EAST || fromDirection == Direction.WEST;
        }
        return false;
    }
}
