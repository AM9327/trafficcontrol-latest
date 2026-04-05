package com.clussmanproductions.trafficcontrol.blocks;

import com.clussmanproductions.trafficcontrol.tileentity.SignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BlockSign extends Block implements IHorizontalPoleConnectable, EntityBlock {

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 5, 16, 16, 11);
    private static final VoxelShape SHAPE_WEST  = Block.box(5, 0, 0, 11, 16, 16);
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 5, 16, 16, 11);
    private static final VoxelShape SHAPE_EAST  = Block.box(5, 0, 0, 11, 16, 16);
    // Diagonal rotations: sign face spans the block diagonal, use full block
    private static final VoxelShape SHAPE_DIAGONAL = Block.box(0, 0, 0, 16, 16, 16);

    public BlockSign(BlockBehaviour.Properties properties) {
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
                RotationSegment.convertToSegment(context.getRotation()));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int rotation = state.getValue(ROTATION);
        boolean isCardinal = (rotation % 4) == 0; // 0, 4, 8, 12 are cardinal

        // Check for adjacent CG pole/base — sign shifts toward it (not toward HP)
        Direction shiftDir = null;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (neighbor instanceof BlockCrossingGatePole || neighbor instanceof BlockCrossingGateBase) {
                shiftDir = dir;
                break;
            }
        }
        // Chained: adjacent sign with CG pole behind it
        if (shiftDir == null) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockSign) {
                    Block beyond = level.getBlockState(pos.relative(dir, 2)).getBlock();
                    if (beyond instanceof BlockCrossingGatePole) {
                        shiftDir = dir;
                        break;
                    }
                }
            }
        }
        // Back-to-back: adjacent sign facing opposite direction
        if (shiftDir == null) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState neighborState = level.getBlockState(pos.relative(dir));
                if (neighborState.getBlock() instanceof BlockSign
                        && neighborState.hasProperty(BlockStateProperties.ROTATION_16)) {
                    int neighborRot = neighborState.getValue(BlockStateProperties.ROTATION_16);
                    if (Math.abs(neighborRot - rotation) == 8) {
                        shiftDir = dir;
                        break;
                    }
                }
            }
        }

        if (!isCardinal) {
            // Diagonal rotations: sign face spans beyond the block, use full block
            if (shiftDir != null) {
                double offsetX = shiftDir.getStepX() * 9.0;
                double offsetZ = shiftDir.getStepZ() * 9.0;
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
            double offsetX = shiftDir.getStepX() * 9.0;
            double offsetZ = shiftDir.getStepZ() * 9.0;
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
