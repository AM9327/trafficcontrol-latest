package com.clussmanproductions.trafficcontrol.blocks;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockPedestrianButton extends HorizontalDirectionalBlock {

    public static final BooleanProperty ABOVE = BooleanProperty.create("above");
    public static final BooleanProperty PAIRED = BooleanProperty.create("paired");
    public static final BooleanProperty MOUNTED = BooleanProperty.create("mounted");
    public static final BooleanProperty HORIZONTAL_BAR = BooleanProperty.create("horizontal_bar");

    public static final MapCodec<BlockPedestrianButton> CODEC = simpleCodec(BlockPedestrianButton::new);

    // Pole is centered — same for all facings
    private static final VoxelShape SHAPE_POLE = Block.box(7, 0, 7, 9, 10, 9);
    private static final VoxelShape SHAPE_POLE_FULL = Block.box(7, 0, 7, 9, 16, 9);

    // Default orientation faces SOUTH (housing + sign on +Z side)
    private static final VoxelShape HOUSING_S = Block.box(7.2, 0.5, 9, 8.7, 2, 9.7);
    private static final VoxelShape SIGN_S    = Block.box(4.5, 3, 9, 11.5, 9, 9.5);
    // 180° rotation → NORTH
    private static final VoxelShape HOUSING_N = Block.box(7.3, 0.5, 6.3, 8.8, 2, 7);
    private static final VoxelShape SIGN_N    = Block.box(4.5, 3, 6.5, 11.5, 9, 7);
    // 90° CW rotation → WEST
    private static final VoxelShape HOUSING_W = Block.box(6.3, 0.5, 7.2, 7, 2, 8.7);
    private static final VoxelShape SIGN_W    = Block.box(6.5, 3, 4.5, 7, 9, 11.5);
    // 270° CW rotation → EAST
    private static final VoxelShape HOUSING_E = Block.box(9, 0.5, 7.3, 9.7, 2, 8.8);
    private static final VoxelShape SIGN_E    = Block.box(9, 3, 4.5, 9.5, 9, 11.5);

    // MOUNTED shapes: entire assembly (pole + housing + sign) shifted 8px toward the rear pole
    // (matches the b2b winner shift pattern); 6 px stub closes the remaining gap to the CG pole south face.
    // SOUTH facing: shift -Z (rear = NORTH)
    private static final VoxelShape POLE_SHIFTED_S      = Block.box(7.0,  0.0, -1.0, 9.0,  10.0, 1.0);
    private static final VoxelShape POLE_FULL_SHIFTED_S = Block.box(7.0,  0.0, -1.0, 9.0,  16.0, 1.0);
    private static final VoxelShape HOUSING_S_MOUNTED   = Block.box(7.2, 0.5, 1.0, 8.7, 2.0, 1.7);
    private static final VoxelShape SIGN_S_MOUNTED      = Block.box(4.5, 3.0, 1.0, 11.5, 9.0, 1.5);
    // NORTH facing: shift +Z (rear = SOUTH)
    private static final VoxelShape POLE_SHIFTED_N      = Block.box(7.0,  0.0, 15.0, 9.0,  10.0, 17.0);
    private static final VoxelShape POLE_FULL_SHIFTED_N = Block.box(7.0,  0.0, 15.0, 9.0,  16.0, 17.0);
    private static final VoxelShape HOUSING_N_MOUNTED   = Block.box(7.3, 0.5, 14.3, 8.8, 2.0, 15.0);
    private static final VoxelShape SIGN_N_MOUNTED      = Block.box(4.5, 3.0, 14.5, 11.5, 9.0, 15.0);
    // EAST facing: shift -X (rear = WEST)
    private static final VoxelShape POLE_SHIFTED_E      = Block.box(-1.0, 0.0, 7.0, 1.0,  10.0, 9.0);
    private static final VoxelShape POLE_FULL_SHIFTED_E = Block.box(-1.0, 0.0, 7.0, 1.0,  16.0, 9.0);
    private static final VoxelShape HOUSING_E_MOUNTED   = Block.box(1.0, 0.5, 7.3, 1.7, 2.0, 8.8);
    private static final VoxelShape SIGN_E_MOUNTED      = Block.box(1.0, 3.0, 4.5, 1.5, 9.0, 11.5);
    // WEST facing: shift +X (rear = EAST)
    private static final VoxelShape POLE_SHIFTED_W      = Block.box(15.0, 0.0, 7.0, 17.0, 10.0, 9.0);
    private static final VoxelShape POLE_FULL_SHIFTED_W = Block.box(15.0, 0.0, 7.0, 17.0, 16.0, 9.0);
    private static final VoxelShape HOUSING_W_MOUNTED   = Block.box(14.3, 0.5, 7.2, 15.0, 2.0, 8.7);
    private static final VoxelShape SIGN_W_MOUNTED      = Block.box(14.5, 3.0, 4.5, 15.0, 9.0, 11.5);

    // Stubs for MOUNTED (on actual pole): 6 px horizontal bar from shifted pole back face to CG/HP pole south face
    private static final VoxelShape STUB_S = Block.box(7.0, 4.0, -7.0, 9.0, 6.0, -1.0);
    private static final VoxelShape STUB_N = Block.box(7.0, 4.0, 17.0, 9.0, 6.0, 23.0);
    private static final VoxelShape STUB_E = Block.box(-7.0, 4.0, 7.0, -1.0, 6.0, 9.0);
    private static final VoxelShape STUB_W = Block.box(17.0, 4.0, 7.0, 23.0, 6.0, 9.0);

    // PAIRED b2b: connector reaches from the block boundary to the local pole.
    private static final VoxelShape STUB_IN_S = Block.box(7.0, 4.0, 0.0, 9.0, 6.0, 7.0);
    private static final VoxelShape STUB_IN_N = Block.box(7.0, 4.0, 9.0, 9.0, 6.0, 16.0);
    private static final VoxelShape STUB_IN_E = Block.box(0.0, 4.0, 7.0, 7.0, 6.0, 9.0);
    private static final VoxelShape STUB_IN_W = Block.box(9.0, 4.0, 7.0, 16.0, 6.0, 9.0);

    private static final VoxelShape BASE_S = Shapes.or(SHAPE_POLE, HOUSING_S, SIGN_S);
    private static final VoxelShape BASE_N = Shapes.or(SHAPE_POLE, HOUSING_N, SIGN_N);
    private static final VoxelShape BASE_E = Shapes.or(SHAPE_POLE, HOUSING_E, SIGN_E);
    private static final VoxelShape BASE_W = Shapes.or(SHAPE_POLE, HOUSING_W, SIGN_W);
    private static final VoxelShape ABOVE_S = Shapes.or(SHAPE_POLE_FULL, HOUSING_S, SIGN_S);
    private static final VoxelShape ABOVE_N = Shapes.or(SHAPE_POLE_FULL, HOUSING_N, SIGN_N);
    private static final VoxelShape ABOVE_E = Shapes.or(SHAPE_POLE_FULL, HOUSING_E, SIGN_E);
    private static final VoxelShape ABOVE_W = Shapes.or(SHAPE_POLE_FULL, HOUSING_W, SIGN_W);
    private static final VoxelShape MOUNTED_S       = Shapes.or(POLE_SHIFTED_S,      HOUSING_S_MOUNTED, SIGN_S_MOUNTED, STUB_S);
    private static final VoxelShape MOUNTED_N       = Shapes.or(POLE_SHIFTED_N,      HOUSING_N_MOUNTED, SIGN_N_MOUNTED, STUB_N);
    private static final VoxelShape MOUNTED_E       = Shapes.or(POLE_SHIFTED_E,      HOUSING_E_MOUNTED, SIGN_E_MOUNTED, STUB_E);
    private static final VoxelShape MOUNTED_W       = Shapes.or(POLE_SHIFTED_W,      HOUSING_W_MOUNTED, SIGN_W_MOUNTED, STUB_W);
    private static final VoxelShape MOUNTED_ABOVE_S = Shapes.or(POLE_FULL_SHIFTED_S, HOUSING_S_MOUNTED, SIGN_S_MOUNTED, STUB_S);
    private static final VoxelShape MOUNTED_ABOVE_N = Shapes.or(POLE_FULL_SHIFTED_N, HOUSING_N_MOUNTED, SIGN_N_MOUNTED, STUB_N);
    private static final VoxelShape MOUNTED_ABOVE_E = Shapes.or(POLE_FULL_SHIFTED_E, HOUSING_E_MOUNTED, SIGN_E_MOUNTED, STUB_E);
    private static final VoxelShape MOUNTED_ABOVE_W = Shapes.or(POLE_FULL_SHIFTED_W, HOUSING_W_MOUNTED, SIGN_W_MOUNTED, STUB_W);

    // PAIRED non-winner shapes: keep the local pole enabled, while the winner side shifts as one assembly.
    private static final VoxelShape PAIRED_S       = Shapes.or(SHAPE_POLE,      HOUSING_S, SIGN_S, STUB_IN_S);
    private static final VoxelShape PAIRED_N       = Shapes.or(SHAPE_POLE,      HOUSING_N, SIGN_N, STUB_IN_N);
    private static final VoxelShape PAIRED_E       = Shapes.or(SHAPE_POLE,      HOUSING_E, SIGN_E, STUB_IN_E);
    private static final VoxelShape PAIRED_W       = Shapes.or(SHAPE_POLE,      HOUSING_W, SIGN_W, STUB_IN_W);
    private static final VoxelShape PAIRED_ABOVE_S = Shapes.or(SHAPE_POLE_FULL, HOUSING_S, SIGN_S, STUB_IN_S);
    private static final VoxelShape PAIRED_ABOVE_N = Shapes.or(SHAPE_POLE_FULL, HOUSING_N, SIGN_N, STUB_IN_N);
    private static final VoxelShape PAIRED_ABOVE_E = Shapes.or(SHAPE_POLE_FULL, HOUSING_E, SIGN_E, STUB_IN_E);
    private static final VoxelShape PAIRED_ABOVE_W = Shapes.or(SHAPE_POLE_FULL, HOUSING_W, SIGN_W, STUB_IN_W);

    // B2B winner side: shift the whole assembly with the pole, matching TC frame and road sign B2B.
    private static final VoxelShape WINNER_N       = Shapes.or(POLE_SHIFTED_N, HOUSING_N_MOUNTED, SIGN_N_MOUNTED, STUB_N);
    private static final VoxelShape WINNER_W       = Shapes.or(POLE_SHIFTED_W, HOUSING_W_MOUNTED, SIGN_W_MOUNTED, STUB_W);
    private static final VoxelShape WINNER_ABOVE_N = Shapes.or(POLE_FULL_SHIFTED_N, HOUSING_N_MOUNTED, SIGN_N_MOUNTED, STUB_N);
    private static final VoxelShape WINNER_ABOVE_W = Shapes.or(POLE_FULL_SHIFTED_W, HOUSING_W_MOUNTED, SIGN_W_MOUNTED, STUB_W);

    private static final SoundEvent PED_BUTTON_SOUND =
            SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "ped_button"));

    public BlockPedestrianButton(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PAIRED, false)
                .setValue(ABOVE, false)
                .setValue(MOUNTED, false)
                .setValue(HORIZONTAL_BAR, false)
                .setValue(BlockStateProperties.POWERED, false));
    }

    @Override
    protected MapCodec<BlockPedestrianButton> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PAIRED, ABOVE, MOUNTED, HORIZONTAL_BAR, BlockStateProperties.POWERED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean above = state.getValue(ABOVE);
        if (state.getValue(MOUNTED)) {
            return switch (state.getValue(FACING)) {
                case NORTH -> above ? MOUNTED_ABOVE_N : MOUNTED_N;
                case EAST  -> above ? MOUNTED_ABOVE_E : MOUNTED_E;
                case WEST  -> above ? MOUNTED_ABOVE_W : MOUNTED_W;
                default    -> above ? MOUNTED_ABOVE_S : MOUNTED_S;
            };
        }
        if (state.getValue(PAIRED)) {
            return switch (state.getValue(FACING)) {
                case NORTH -> above ? WINNER_ABOVE_N : WINNER_N;
                case EAST  -> above ? PAIRED_ABOVE_E : PAIRED_E;
                case WEST  -> above ? WINNER_ABOVE_W : WINNER_W;
                default    -> above ? PAIRED_ABOVE_S : PAIRED_S;
            };
        }
        return switch (state.getValue(FACING)) {
            case NORTH -> above ? ABOVE_N : BASE_N;
            case EAST  -> above ? ABOVE_E : BASE_E;
            case WEST  -> above ? ABOVE_W : BASE_W;
            default    -> above ? ABOVE_S : BASE_S;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        // Auto-orient: if adjacent to a CG/HP pole, face away from it so MOUNTED detects correctly.
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (isRealPole(context.getLevel().getBlockState(context.getClickedPos().relative(dir)))) {
                facing = dir.getOpposite();
                break;
            }
        }
        return updateConnections(this.defaultBlockState().setValue(FACING, facing),
                context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        return updateConnections(state, level, pos);
    }

    private static BlockState updateConnections(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockState behindState = level.getBlockState(pos.relative(facing.getOpposite()));
        BlockState aboveState = level.getBlockState(pos.above());
        boolean mounted = isRealPole(behindState);
        boolean paired = isPairedPartner(behindState, facing);
        boolean above = isPoleLikeBlock(aboveState);
        boolean horizontalBar = mounted || paired;
        return state
                .setValue(ABOVE, above)
                .setValue(MOUNTED, mounted)
                .setValue(PAIRED, paired)
                .setValue(HORIZONTAL_BAR, horizontalBar);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    private static boolean isPairedPartner(BlockState state, Direction myFacing) {
        return state.getBlock() instanceof BlockPedestrianButton
                && state.getValue(FACING) == myFacing.getOpposite();
    }

    // Actual pole blocks only — a paired ped button is NOT a pole.
    private static boolean isRealPole(BlockState state) {
        Block block = state.getBlock();
        return block instanceof BlockCrossingGatePole
                || block instanceof BlockHorizontalPole
                || block instanceof BlockCrossingGateBase;
    }

    // Pole-like for the "pole above" check — ped button above extends own pole full-height.
    private static boolean isPoleLikeBlock(BlockState state) {
        Block block = state.getBlock();
        return isRealPole(state)
                || block instanceof BlockPedestrianButton;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!state.getValue(BlockStateProperties.POWERED)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, true), 3);
            level.scheduleTick(pos, this, 20);
            level.playSound(null, pos, PED_BUTTON_SOUND, SoundSource.BLOCKS, 0.1F, 1.0F);
            level.updateNeighborsAt(pos, this);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(BlockStateProperties.POWERED)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, false), 3);
            level.updateNeighborsAt(pos, this);
        }
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }
}
