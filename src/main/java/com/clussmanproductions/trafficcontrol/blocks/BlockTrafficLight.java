package com.clussmanproductions.trafficcontrol.blocks;

import com.clussmanproductions.trafficcontrol.tileentity.RotatableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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

public class BlockTrafficLight extends Block implements EntityBlock {

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty HAS_HORIZONTAL_BAR = BooleanProperty.create("horizontal_bar");

    // Wider hitbox for easier interaction from any angle
    private static final VoxelShape SHAPE_SOUTH = Block.box(2, 0, 4, 14, 16, 14);
    private static final VoxelShape SHAPE_WEST  = Block.box(2, 0, 2, 12, 16, 14);
    private static final VoxelShape SHAPE_NORTH = Block.box(2, 0, 2, 14, 16, 12);
    private static final VoxelShape SHAPE_EAST  = Block.box(4, 0, 2, 14, 16, 14);

    public BlockTrafficLight(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(HAS_HORIZONTAL_BAR, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, HAS_HORIZONTAL_BAR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(ROTATION,
                RotationSegment.convertToSegment(context.getRotation() + 180.0F));
        return updateHorizontalBar(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        return updateHorizontalBar(state, level, pos);
    }

    private BlockState updateHorizontalBar(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockState neighbor = level.getBlockState(pos.relative(dir));
            Block neighborBlock = neighbor.getBlock();
            if (neighborBlock instanceof BlockCrossingGatePole ||
                neighborBlock instanceof BlockHorizontalPole ||
                neighborBlock instanceof BlockCrossingGateBase ||
                neighborBlock instanceof BlockTrafficLight ||
                neighborBlock instanceof BlockSignalArm) {
                return state.setValue(HAS_HORIZONTAL_BAR, true);
            }
        }
        return state.setValue(HAS_HORIZONTAL_BAR, false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int rotation = state.getValue(ROTATION);
        int steps = Math.round(RotationSegment.convertToDegrees(rotation) / 90.0f) % 4;
        if (steps < 0) steps += 4;

        return switch (steps) {
            case 1 -> SHAPE_WEST;
            case 2 -> SHAPE_NORTH;
            case 3 -> SHAPE_EAST;
            default -> SHAPE_SOUTH;
        };
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
