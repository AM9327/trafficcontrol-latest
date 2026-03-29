package com.clussmanproductions.trafficcontrol.blocks;

import com.clussmanproductions.trafficcontrol.item.ItemTrafficLightFrame;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCrossingGatePole extends Block {

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    private static final VoxelShape POLE = Block.box(7, 0, 7, 9, 16, 9);
    private static final VoxelShape NORTH_ARM = Block.box(7, 7, 0, 9, 9, 7);
    private static final VoxelShape SOUTH_ARM = Block.box(7, 7, 9, 9, 9, 16);
    private static final VoxelShape EAST_ARM = Block.box(9, 7, 7, 16, 9, 9);
    private static final VoxelShape WEST_ARM = Block.box(0, 7, 7, 7, 9, 9);

    public BlockCrossingGatePole(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateConnections(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        return updateConnections(state, level, pos);
    }

    private BlockState updateConnections(BlockState state, LevelReader level, BlockPos pos) {
        return state
                .setValue(NORTH, shouldConnect(level, pos, Direction.NORTH))
                .setValue(SOUTH, shouldConnect(level, pos, Direction.SOUTH))
                .setValue(EAST, shouldConnect(level, pos, Direction.EAST))
                .setValue(WEST, shouldConnect(level, pos, Direction.WEST));
    }

    private boolean shouldConnect(LevelReader level, BlockPos pos, Direction direction) {
        Block neighbor = level.getBlockState(pos.relative(direction)).getBlock();
        return neighbor instanceof BlockHorizontalPole
                || neighbor instanceof BlockTrafficLight
                || neighbor instanceof BlockCrossingGatePole
                || neighbor instanceof BlockSign;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // When player holds a TL frame and there's a horizontal pole adjacent,
        // become transparent so clicks reach the HP behind
        if (context instanceof EntityCollisionContext entityContext) {
            var entity = entityContext.getEntity();
            if (entity instanceof Player player
                    && player.getMainHandItem().getItem() instanceof ItemTrafficLightFrame) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    if (level.getBlockState(pos.relative(dir)).getBlock() instanceof BlockHorizontalPole) {
                        return Shapes.empty();
                    }
                }
                return Shapes.block();
            }
        }

        VoxelShape shape = POLE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_ARM);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_ARM);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_ARM);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_ARM);
        return shape;
    }
}
