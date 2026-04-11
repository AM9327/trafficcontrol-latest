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

public class BlockTrafficLight extends Block implements EntityBlock {

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty HAS_HORIZONTAL_BAR = BooleanProperty.create("horizontal_bar");
    public static final BooleanProperty PAIRED_ACROSS_POLE = BooleanProperty.create("paired");

    // Wider hitbox for easier interaction from any angle
    private static final VoxelShape SHAPE_SOUTH = Block.box(2, 0, 4, 14, 16, 14);
    private static final VoxelShape SHAPE_WEST  = Block.box(2, 0, 2, 12, 16, 14);
    private static final VoxelShape SHAPE_NORTH = Block.box(2, 0, 2, 14, 16, 12);
    private static final VoxelShape SHAPE_EAST  = Block.box(4, 0, 2, 14, 16, 14);

    // Horizontal TL frame hitboxes — wider to match the sideways model
    private static final VoxelShape SHAPE_HORIZ_NS = Block.box(-5, 3, 4, 30, 13, 14);
    private static final VoxelShape SHAPE_HORIZ_EW = Block.box(2, 3, -5, 12, 13, 30);

    // Pole-mounted hitboxes — split into top/bottom halves leaving y=5.5-10.5 clear for pole arm clicks
    private static final VoxelShape SHAPE_PAIRED_POLE_EAST  = Shapes.or(
            Block.box(13, 0, 5, 16, 5.5, 11), Block.box(13, 10.5, 5, 16, 16, 11));
    private static final VoxelShape SHAPE_PAIRED_POLE_WEST  = Shapes.or(
            Block.box(0, 0, 5, 3, 5.5, 11), Block.box(0, 10.5, 5, 3, 16, 11));
    private static final VoxelShape SHAPE_PAIRED_POLE_SOUTH = Shapes.or(
            Block.box(5, 0, 13, 11, 5.5, 16), Block.box(5, 10.5, 13, 11, 16, 16));
    private static final VoxelShape SHAPE_PAIRED_POLE_NORTH = Shapes.or(
            Block.box(5, 0, 0, 11, 5.5, 3), Block.box(5, 10.5, 0, 11, 16, 3));

    public BlockTrafficLight(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(HAS_HORIZONTAL_BAR, false)
                .setValue(PAIRED_ACROSS_POLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, HAS_HORIZONTAL_BAR, PAIRED_ACROSS_POLE);
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
        boolean hasBar = false;
        boolean paired = false;
        Direction poleDir = null;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockState neighbor = level.getBlockState(pos.relative(dir));
            Block neighborBlock = neighbor.getBlock();
            if (neighborBlock instanceof BlockCrossingGatePole ||
                neighborBlock instanceof BlockHorizontalPole ||
                neighborBlock instanceof BlockCrossingGateBase ||
                neighborBlock instanceof BlockTrafficLight ||
                neighborBlock instanceof BlockSignalArm ||
                neighborBlock instanceof BlockSign ||
                neighborBlock instanceof BlockStreetSign) {
                hasBar = true;
            }
            if (poleDir == null && (neighborBlock instanceof BlockCrossingGatePole
                    || neighborBlock instanceof BlockHorizontalPole
                    || neighborBlock instanceof BlockCrossingGateBase)) {
                poleDir = dir;
            }
        }

        // Check for across-pole pairing
        if (poleDir != null && state.hasProperty(ROTATION)) {
            int rotation = state.getValue(ROTATION);
            BlockPos beyondPole = pos.relative(poleDir, 2);
            BlockState beyondState = level.getBlockState(beyondPole);
            if (beyondState.getBlock() instanceof BlockTrafficLight
                    && beyondState.hasProperty(ROTATION)) {
                int beyondRot = beyondState.getValue(ROTATION);
                if (Math.abs(beyondRot - rotation) == 8) {
                    paired = true;
                }
            }
        }

        return state.setValue(HAS_HORIZONTAL_BAR, hasBar).setValue(PAIRED_ACROSS_POLE, paired);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int rotation = state.getValue(ROTATION);
        int steps = Math.round(RotationSegment.convertToDegrees(rotation) / 90.0f) % 4;
        if (steps < 0) steps += 4;

        // Horizontal TL frames: wider hitbox, no pole shift
        boolean isHorizTL = state.getBlock().getDescriptionId().contains("horiz");
        if (isHorizTL) {
            return (steps == 1 || steps == 3) ? SHAPE_HORIZ_EW : SHAPE_HORIZ_NS;
        }

        // Check for adjacent traffic lights (side-by-side row)
        boolean hasAdjacentTL = false;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(dir)).getBlock() instanceof BlockTrafficLight) {
                hasAdjacentTL = true;
                break;
            }
        }

        // Use pole-shifted hitbox only when pole-mounted and NOT in a side-by-side row
        if (!hasAdjacentTL) {
            Direction shiftDir = null;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockHorizontalPole) {
                    shiftDir = dir;
                    break;
                }
            }
            if (shiftDir == null) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                    if (neighbor instanceof BlockCrossingGatePole) {
                        shiftDir = dir;
                        break;
                    }
                }
            }
            if (shiftDir != null) {
                return switch (shiftDir) {
                    case EAST -> SHAPE_PAIRED_POLE_EAST;
                    case WEST -> SHAPE_PAIRED_POLE_WEST;
                    case SOUTH -> SHAPE_PAIRED_POLE_SOUTH;
                    case NORTH -> SHAPE_PAIRED_POLE_NORTH;
                    default -> SHAPE_SOUTH;
                };
            }
        }

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
