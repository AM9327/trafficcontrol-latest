package com.clussmanproductions.trafficcontrol.blocks;

import com.clussmanproductions.trafficcontrol.tileentity.SignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BlockSign extends Block implements IHorizontalPoleConnectable, EntityBlock {

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty HAS_HORIZONTAL_BAR = BooleanProperty.create("horizontal_bar");
    public static final BooleanProperty PAIRED = BooleanProperty.create("paired");

    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 5, 16, 16, 11);
    private static final VoxelShape SHAPE_WEST  = Block.box(5, 0, 0, 11, 16, 16);
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 5, 16, 16, 11);
    private static final VoxelShape SHAPE_EAST  = Block.box(5, 0, 0, 11, 16, 16);
    // Diagonal rotations: sign face spans the block diagonal, use full block
    private static final VoxelShape SHAPE_DIAGONAL = Block.box(0, 0, 0, 16, 16, 16);

    public BlockSign(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(HAS_HORIZONTAL_BAR, false)
                .setValue(PAIRED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, HAS_HORIZONTAL_BAR, PAIRED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(ROTATION,
                RotationSegment.convertToSegment(context.getRotation()));
        return updateConnections(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        return updateConnections(state, level, pos);
    }

    private BlockState updateConnections(BlockState state, LevelReader level, BlockPos pos) {
        boolean hasBar = false;
        boolean paired = false;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (neighbor instanceof BlockCrossingGatePole || neighbor instanceof BlockCrossingGateBase
                    || neighbor instanceof BlockHorizontalPole || neighbor instanceof BlockSign
                    || neighbor instanceof BlockTrafficLight || neighbor instanceof BlockStreetSign) {
                hasBar = true;
            }
        }
        // Direct back-to-back + across-pole pairing
        if (state.hasProperty(ROTATION)) {
            int rotation = state.getValue(ROTATION);
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState neighbor = level.getBlockState(pos.relative(dir));
                if (neighbor.getBlock() instanceof BlockSign && neighbor.hasProperty(ROTATION)) {
                    if (Math.abs(neighbor.getValue(ROTATION) - rotation) == 8) {
                        paired = true;
                        break;
                    }
                }
                // Across-pole check
                Block nb = level.getBlockState(pos.relative(dir)).getBlock();
                if (nb instanceof BlockCrossingGatePole || nb instanceof BlockHorizontalPole
                        || nb instanceof BlockCrossingGateBase) {
                    BlockState beyond = level.getBlockState(pos.relative(dir, 2));
                    if (beyond.getBlock() instanceof BlockSign && beyond.hasProperty(ROTATION)) {
                        if (Math.abs(beyond.getValue(ROTATION) - rotation) == 8) {
                            paired = true;
                            break;
                        }
                    }
                }
            }
        }
        return state.setValue(HAS_HORIZONTAL_BAR, hasBar).setValue(PAIRED, paired);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int rotation = state.getValue(ROTATION);
        boolean isCardinal = (rotation % 4) == 0; // 0, 4, 8, 12 are cardinal

        // Check for adjacent pole — HP first, then CG pole/base
        Direction shiftDir = null;
        boolean isChainedMount = false;
        boolean isHPMount = false;
        // HP mount (priority)
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (neighbor instanceof BlockHorizontalPole) {
                shiftDir = dir;
                isHPMount = true;
                break;
            }
        }
        // CG pole/base mount
        if (shiftDir == null) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockCrossingGatePole || neighbor instanceof BlockCrossingGateBase) {
                    shiftDir = dir;
                    break;
                }
            }
        }
        // Chained: adjacent sign with CG pole/base behind it — shift 1px less
        if (shiftDir == null) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockSign) {
                    Block beyond = level.getBlockState(pos.relative(dir, 2)).getBlock();
                    if (beyond instanceof BlockCrossingGatePole || beyond instanceof BlockCrossingGateBase) {
                        shiftDir = dir;
                        isChainedMount = true;
                        break;
                    }
                }
            }
        }
        // Detect chained mount by checking if shift target is a sign (not a pole)
        if (!isChainedMount && shiftDir != null && !isHPMount) {
            Block shiftTarget = level.getBlockState(pos.relative(shiftDir)).getBlock();
            if (shiftTarget instanceof BlockSign) {
                isChainedMount = true;
            }
        }
        // Back-to-back: only SECOND sign shifts (tiebreaker matches renderer)
        // Skip when pole adjacent (pole shift takes priority)
        boolean isB2BShift = false;
        if (shiftDir == null) {
            boolean hasPole = false;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block n = level.getBlockState(pos.relative(dir)).getBlock();
                if (n instanceof BlockHorizontalPole || n instanceof BlockCrossingGatePole || n instanceof BlockCrossingGateBase) {
                    hasPole = true;
                    break;
                }
            }
            if (!hasPole) {
                // Check ALL directions for opposite-facing sign (matches renderer detection)
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockState neighborState = level.getBlockState(pos.relative(dir));
                    if (neighborState.getBlock() instanceof BlockSign
                            && neighborState.hasProperty(BlockStateProperties.ROTATION_16)) {
                        int neighborRot = neighborState.getValue(BlockStateProperties.ROTATION_16);
                        if (Math.abs(neighborRot - rotation) == 8) {
                            // Tiebreaker: same as renderer (dir toward partner)
                            boolean isSecond = dir.getStepX() + dir.getStepZ() > 0;
                            if (isSecond) {
                                shiftDir = dir;
                                isB2BShift = true;
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (!isCardinal) {
            if (shiftDir != null) {
                double shiftAmount = isB2BShift ? 8.0 : isHPMount ? 7.0 : isChainedMount ? 8.0 : 9.0;
                double offsetX = shiftDir.getStepX() * shiftAmount;
                double offsetZ = shiftDir.getStepZ() * shiftAmount;
                return Block.box(offsetX, 0, offsetZ, 16 + offsetX, 16, 16 + offsetZ);
            }
            return SHAPE_DIAGONAL;
        }

        int steps = Math.round(RotationSegment.convertToDegrees(rotation) / 90.0f) % 4;
        if (steps < 0) steps += 4;

        if (shiftDir != null) {
            double minX, minZ, maxX, maxZ;
            if (steps == 1 || steps == 3) {
                minX = 5; minZ = 0; maxX = 11; maxZ = 16;
            } else {
                minX = 0; minZ = 5; maxX = 16; maxZ = 11;
            }
            double shiftAmt = isB2BShift ? 8.0 : isHPMount ? 7.0 : isChainedMount ? 8.0 : 9.0;
            double offsetX = shiftDir.getStepX() * shiftAmt;
            double offsetZ = shiftDir.getStepZ() * shiftAmt;
            return Block.box(minX + offsetX, 0, minZ + offsetZ, maxX + offsetX, 16, maxZ + offsetZ);
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
    public boolean canConnectHorizontalPole(BlockState state, Direction fromDirection) {
        return true;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SignBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        // Skip GUI when holding screwdriver (it rotates the block instead)
        if (player.getMainHandItem().getItem() instanceof com.clussmanproductions.trafficcontrol.item.ItemScrewdriver
                || player.getOffhandItem().getItem() instanceof com.clussmanproductions.trafficcontrol.item.ItemScrewdriver) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SignBlockEntity signBE) {
                com.clussmanproductions.trafficcontrol.gui.SignGui.open(signBE);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            notifyNeighborBlockEntities(level, pos);
        }
    }

    private void notifyNeighborBlockEntities(Level level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be != null) {
                BlockState neighborState = level.getBlockState(neighborPos);
                level.sendBlockUpdated(neighborPos, neighborState, neighborState, 3);
            }
        }
    }
}
