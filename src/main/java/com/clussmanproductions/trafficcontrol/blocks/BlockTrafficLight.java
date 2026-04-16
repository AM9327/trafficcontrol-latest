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

    // 4-bulb (quad): y=0 to 32 (extends 1 block above)
    private static final VoxelShape SHAPE_QUAD_SOUTH = Block.box(2, 0, 4, 14, 32, 14);
    private static final VoxelShape SHAPE_QUAD_WEST  = Block.box(2, 0, 2, 12, 32, 14);
    private static final VoxelShape SHAPE_QUAD_NORTH = Block.box(2, 0, 2, 14, 32, 12);
    private static final VoxelShape SHAPE_QUAD_EAST  = Block.box(4, 0, 2, 14, 32, 14);

    // 5-bulb: y=-5 to 32 (extends below and above)
    private static final VoxelShape SHAPE_FIVE_SOUTH = Block.box(2, -5, 4, 14, 32, 14);
    private static final VoxelShape SHAPE_FIVE_WEST  = Block.box(2, -5, 2, 12, 32, 14);
    private static final VoxelShape SHAPE_FIVE_NORTH = Block.box(2, -5, 2, 14, 32, 12);
    private static final VoxelShape SHAPE_FIVE_EAST  = Block.box(4, -5, 2, 14, 32, 14);

    // Arm hitboxes extending into adjacent blocks (same as HP arms)
    private static final VoxelShape TL_ARM_NORTH = Block.box(5.5, 5.5, -16, 10.5, 10.5, 7);
    private static final VoxelShape TL_ARM_SOUTH = Block.box(5.5, 5.5, 9, 10.5, 10.5, 32);
    private static final VoxelShape TL_ARM_EAST  = Block.box(9, 5.5, 5.5, 32, 10.5, 10.5);
    private static final VoxelShape TL_ARM_WEST  = Block.box(-16, 5.5, 5.5, 7, 10.5, 10.5);

    // Horizontal TL frame hitboxes — per variant width
    // 3-bulb: backing plate only (X=-3..18.5). Back-pole arm (X=25..27) renders conditionally, so no hitbox for it.
    private static final VoxelShape SHAPE_HORIZ_3_NS = Block.box(-3, 3, 4, 18.5, 13, 14);
    private static final VoxelShape SHAPE_HORIZ_3_EW = Block.box(2, 3, -3, 12, 13, 18.5);
    // 4-bulb: x=1 to 29.5 (28.5px)
    private static final VoxelShape SHAPE_HORIZ_4_NS = Block.box(1, 3, 4, 29.5, 13, 14);
    private static final VoxelShape SHAPE_HORIZ_4_EW = Block.box(2, 3, 1, 12, 13, 29.5);
    // 5-bulb: x=-5 to 29.5 (34.5px)
    private static final VoxelShape SHAPE_HORIZ_5_NS = Block.box(-5, 3, 4, 29.5, 13, 14);
    private static final VoxelShape SHAPE_HORIZ_5_EW = Block.box(2, 3, -5, 12, 13, 29.5);

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

        // Check for across-pole pairing (check all pole directions, not just first)
        if (state.hasProperty(ROTATION)) {
            int rotation = state.getValue(ROTATION);
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockCrossingGatePole
                        || neighbor instanceof BlockHorizontalPole
                        || neighbor instanceof BlockCrossingGateBase) {
                    BlockPos beyondPole = pos.relative(dir, 2);
                    BlockState beyondState = level.getBlockState(beyondPole);
                    if (beyondState.getBlock() instanceof BlockTrafficLight
                            && beyondState.hasProperty(ROTATION)) {
                        int beyondRot = beyondState.getValue(ROTATION);
                        if (Math.abs(beyondRot - rotation) == 8) {
                            paired = true;
                            break;
                        }
                    }
                }
            }
        }

        // Check for direct back-to-back pairing (adjacent TL facing opposite direction)
        if (!paired && state.hasProperty(ROTATION)) {
            int rotation = state.getValue(ROTATION);
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState neighbor = level.getBlockState(pos.relative(dir));
                if (neighbor.getBlock() instanceof BlockTrafficLight
                        && neighbor.hasProperty(ROTATION)) {
                    int neighborRot = neighbor.getValue(ROTATION);
                    if (Math.abs(neighborRot - rotation) == 8) {
                        paired = true;
                        break;
                    }
                }
            }
        }

        return state.setValue(HAS_HORIZONTAL_BAR, hasBar).setValue(PAIRED_ACROSS_POLE, paired);
    }

    private VoxelShape getBaseShape(int steps) {
        String id = this.getDescriptionId();
        boolean isQuad = id.contains("_4");
        boolean isFive = id.contains("_5");
        if (isFive) {
            return switch (steps) {
                case 1 -> SHAPE_FIVE_WEST;
                case 2 -> SHAPE_FIVE_NORTH;
                case 3 -> SHAPE_FIVE_EAST;
                default -> SHAPE_FIVE_SOUTH;
            };
        } else if (isQuad) {
            return switch (steps) {
                case 1 -> SHAPE_QUAD_WEST;
                case 2 -> SHAPE_QUAD_NORTH;
                case 3 -> SHAPE_QUAD_EAST;
                default -> SHAPE_QUAD_SOUTH;
            };
        }
        return switch (steps) {
            case 1 -> SHAPE_WEST;
            case 2 -> SHAPE_NORTH;
            case 3 -> SHAPE_EAST;
            default -> SHAPE_SOUTH;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int rotation = state.getValue(ROTATION);
        int steps = Math.round(RotationSegment.convertToDegrees(rotation) / 90.0f) % 4;
        if (steps < 0) steps += 4;

        // Horizontal TL frames: wider hitbox per variant, no pole shift
        boolean isHorizTL = state.getBlock().getDescriptionId().contains("horiz");
        if (isHorizTL) {
            String hId = state.getBlock().getDescriptionId();
            boolean is5 = hId.contains("_5");
            boolean is4 = hId.contains("_4");
            if (is5) return (steps == 1 || steps == 3) ? SHAPE_HORIZ_5_EW : SHAPE_HORIZ_5_NS;
            if (is4) return (steps == 1 || steps == 3) ? SHAPE_HORIZ_4_EW : SHAPE_HORIZ_4_NS;
            return (steps == 1 || steps == 3) ? SHAPE_HORIZ_3_EW : SHAPE_HORIZ_3_NS;
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
                    if (neighbor instanceof BlockCrossingGatePole
                            || neighbor instanceof BlockCrossingGateBase) {
                        shiftDir = dir;
                        break;
                    }
                }
            }
            if (shiftDir != null) {
                // Shift full TL frame shape toward pole (9/16 for CG pole, 7/16 for HP)
                VoxelShape baseShape = getBaseShape(steps);
                boolean isHP = level.getBlockState(pos.relative(shiftDir)).getBlock() instanceof BlockHorizontalPole;
                double shiftAmt = isHP ? 7.0 / 16.0 : 9.0 / 16.0;
                return baseShape.move(shiftDir.getStepX() * shiftAmt, 0, shiftDir.getStepZ() * shiftAmt);
            }
        }

        // Back-to-back: second TL shifts 8px toward partner (matches renderer tiebreaker)
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockState neighborState = level.getBlockState(pos.relative(dir));
            if (neighborState.getBlock() instanceof BlockTrafficLight
                    && neighborState.hasProperty(ROTATION)) {
                int neighborRot = neighborState.getValue(ROTATION);
                if (Math.abs(neighborRot - rotation) == 8) {
                    boolean isSecond = dir.getStepX() + dir.getStepZ() > 0;
                    if (isSecond) {
                        VoxelShape baseShape = getBaseShape(steps);
                        return baseShape.move(dir.getStepX() * 0.5, 0, dir.getStepZ() * 0.5);
                    }
                    break;
                }
            }
        }

        // Add arm hitboxes toward connected neighbors (HP, CG poles, adjacent TLs, signs)
        VoxelShape result = getBaseShape(steps);
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (neighbor instanceof BlockHorizontalPole || neighbor instanceof BlockCrossingGatePole
                    || neighbor instanceof BlockCrossingGateBase || neighbor instanceof BlockTrafficLight
                    || neighbor instanceof BlockSign || neighbor instanceof BlockStreetSign) {
                VoxelShape arm = switch (dir) {
                    case NORTH -> TL_ARM_NORTH;
                    case SOUTH -> TL_ARM_SOUTH;
                    case EAST -> TL_ARM_EAST;
                    case WEST -> TL_ARM_WEST;
                    default -> null;
                };
                if (arm != null) result = Shapes.or(result, arm);
            }
        }
        return result;
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
