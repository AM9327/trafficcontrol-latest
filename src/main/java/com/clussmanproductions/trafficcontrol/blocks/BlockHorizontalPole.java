package com.clussmanproductions.trafficcontrol.blocks;

import com.clussmanproductions.trafficcontrol.item.ItemTrafficLightFrame;
import com.clussmanproductions.trafficcontrol.tileentity.RotatableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BlockHorizontalPole extends Block implements EntityBlock, IHorizontalPoleConnectable {

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    // Base shapes matching actual pole model dimensions (single block)
    private static final VoxelShape SHAPE_NS = Block.box(5.5, 5.5, 0, 10.5, 10.5, 16);
    private static final VoxelShape SHAPE_EW = Block.box(0, 5.5, 5.5, 16, 10.5, 10.5);

    // Ext arm hitboxes — extend into adjacent TL block so they're clickable through TL hitboxes
    private static final VoxelShape ARM_NORTH = Block.box(5.5, 5.5, -16, 10.5, 10.5, 7);
    private static final VoxelShape ARM_SOUTH = Block.box(5.5, 5.5, 9, 10.5, 10.5, 32);
    private static final VoxelShape ARM_EAST  = Block.box(9, 5.5, 5.5, 32, 10.5, 10.5);
    private static final VoxelShape ARM_WEST  = Block.box(-16, 5.5, 5.5, 7, 10.5, 10.5);

    public BlockHorizontalPole(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(ROTATION,
                RotationSegment.convertToSegment(context.getRotation() + 180.0F));
        return updateConnections(state, context.getLevel(), context.getClickedPos());
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
        return neighbor instanceof BlockTrafficLight
                || neighbor instanceof BlockCrossingGatePole
                || neighbor instanceof BlockCrossingGateBase
                || neighbor instanceof BlockHorizontalPole
                || neighbor instanceof BlockSign
                || neighbor instanceof BlockStreetSign;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // When player holds a TL frame, use full block hitbox for easy clicking
        if (context instanceof EntityCollisionContext entityContext) {
            var entity = entityContext.getEntity();
            if (entity instanceof Player player
                    && player.getMainHandItem().getItem() instanceof ItemTrafficLightFrame) {
                return Shapes.block();
            }
        }

        int rotation = state.getValue(ROTATION);
        int snapped = ((rotation + 2) % 16) / 4;
        boolean isEW = (snapped == 1 || snapped == 3);

        // Base pole shape — single block only, no axis extension
        VoxelShape shape = isEW ? SHAPE_EW : SHAPE_NS;

        // Add ext arm hitboxes where connectable blocks are adjacent
        // Arms extend into neighbor blocks so the pole arm is clickable
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (neighbor instanceof BlockTrafficLight || neighbor instanceof BlockCrossingGatePole
                    || neighbor instanceof BlockCrossingGateBase || neighbor instanceof BlockSign
                    || neighbor instanceof BlockStreetSign || neighbor instanceof BlockHorizontalPole) {
                VoxelShape arm = switch (dir) {
                    case NORTH -> ARM_NORTH;
                    case SOUTH -> ARM_SOUTH;
                    case EAST -> ARM_EAST;
                    case WEST -> ARM_WEST;
                    default -> null;
                };
                if (arm != null) shape = Shapes.or(shape, arm);
            }
        }

        return shape;
    }

    private int countConnectedPoles(BlockGetter level, BlockPos pos, Direction dir) {
        int count = 0;
        BlockPos check = pos;
        for (int i = 0; i < 32; i++) {
            check = check.relative(dir);
            if (level.getBlockState(check).getBlock() instanceof BlockHorizontalPole) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int rotation = state.getValue(ROTATION);
        int snapped = ((rotation + 2) % 16) / 4;
        return (snapped == 1 || snapped == 3) ? SHAPE_EW : SHAPE_NS;
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
