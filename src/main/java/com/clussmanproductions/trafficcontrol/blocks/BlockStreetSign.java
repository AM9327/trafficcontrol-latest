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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BlockStreetSign extends Block implements EntityBlock {

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;

    // Normal sign: Y 6-10
    private static final VoxelShape SHAPE_NS = Block.box(0, 6, 5, 16, 10, 11);
    private static final VoxelShape SHAPE_EW = Block.box(5, 6, 0, 11, 10, 16);
    private static final VoxelShape SHAPE_DIAGONAL = Block.box(0, 6, 0, 16, 10, 16);

    // Hanging sign: Y 15-19 (shifted up by 9)
    private static final VoxelShape HANGING_SHAPE_NS = Block.box(0, 15, 5, 16, 19, 11);
    private static final VoxelShape HANGING_SHAPE_EW = Block.box(5, 15, 0, 11, 19, 16);
    private static final VoxelShape HANGING_SHAPE_DIAGONAL = Block.box(0, 15, 0, 16, 19, 16);

    public BlockStreetSign(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(HANGING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, HANGING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean hanging = context.getClickedFace() == Direction.DOWN;
        return this.defaultBlockState()
                .setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation()))
                .setValue(HANGING, hanging);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean hanging = state.getValue(HANGING);
        int rotation = state.getValue(ROTATION);
        boolean isCardinal = (rotation % 4) == 0;

        int steps = Math.round(RotationSegment.convertToDegrees(rotation) / 90.0f) % 4;
        if (steps < 0) steps += 4;

        if (hanging) {
            if (!isCardinal) return HANGING_SHAPE_DIAGONAL;
            return (steps == 1 || steps == 3) ? HANGING_SHAPE_EW : HANGING_SHAPE_NS;
        } else {
            if (!isCardinal) return SHAPE_DIAGONAL;
            return (steps == 1 || steps == 3) ? SHAPE_EW : SHAPE_NS;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RotatableBlockEntity(pos, state);
    }
}
