package com.clussmanproductions.trafficcontrol.blocks;

import com.clussmanproductions.trafficcontrol.gui.StreetSignGui;
import com.clussmanproductions.trafficcontrol.item.ItemScrewdriver;
import com.clussmanproductions.trafficcontrol.blocks.BlockHorizontalPole;
import com.clussmanproductions.trafficcontrol.tileentity.StreetSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BlockStreetSign extends Block implements IHorizontalPoleConnectable, EntityBlock {

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;

    // Normal sign: Y 6-10
    private static final VoxelShape SHAPE_NS = Block.box(0, 6, 5, 16, 10, 11);
    private static final VoxelShape SHAPE_EW = Block.box(5, 6, 0, 11, 10, 16);
    private static final VoxelShape SHAPE_DIAGONAL = Block.box(0, 6, 0, 16, 10, 16);

    // Hanging sign: Y 15-19 (shifted up by 9)
    private static final VoxelShape HANGING_SHAPE_NS = Block.box(0, 15, 5, 16, 19, 11);
    private static final VoxelShape HANGING_SHAPE_EW = Block.box(5, 15, 0, 11, 15, 16);
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
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof StreetSignBlockEntity streetSignBE) {
            // Add the first sign plate on placement with correct rotation
            if (streetSignBE.getSignCount() == 0) {
                int idx = streetSignBE.addSign(0); // default green
                if (idx >= 0) {
                    int rot = state.getValue(ROTATION);
                    streetSignBE.setRotation(idx, rot);
                }
            }
            if (level.isClientSide()) {
                Minecraft.getInstance().setScreen(new StreetSignGui(streetSignBE));
            }
        }
    }

    @Override
    public boolean canConnectHorizontalPole(BlockState state, Direction fromDirection) {
        // Don't connect horizontal pole to hanging signs
        return !state.getValue(HANGING);
    }

    /** Check if a neighbor is a sign-type block (BlockSign or BlockStreetSign, non-hanging). */
    private static boolean isSignLike(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof BlockSign) return true;
        if (block instanceof BlockStreetSign) {
            return !state.hasProperty(HANGING) || !state.getValue(HANGING);
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean hanging = state.getValue(HANGING);
        int rotation = state.getValue(ROTATION);
        boolean isCardinal = (rotation % 4) == 0;

        // Hanging signs: plates at Y 0 + 9px offset = Y 9 to 9+plateCount*4
        if (hanging) {
            int hPlateCount = 1;
            BlockEntity hbe = level.getBlockEntity(pos);
            if (hbe instanceof StreetSignBlockEntity ssbe) {
                hPlateCount = Math.max(1, ssbe.getSignCount());
            }
            double hYMin = 15;
            double hYMax = 15 + hPlateCount * 4.0;
            if (!isCardinal) return Block.box(0, hYMin, 0, 16, hYMax, 16);
            int steps = Math.round(RotationSegment.convertToDegrees(rotation) / 90.0f) % 4;
            if (steps < 0) steps += 4;
            return (steps == 1 || steps == 3)
                    ? Block.box(5, hYMin, 0, 11, hYMax, 16)
                    : Block.box(0, hYMin, 5, 16, hYMax, 11);
        }

        // Check for HP mount (affects Y position)
        boolean isHPMount = false;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(dir)).getBlock() instanceof BlockHorizontalPole) {
                isHPMount = true;
                break;
            }
        }

        // Check for adjacent CG pole/base — sign shifts toward it
        Direction shiftDir = null;
        double shiftPixels = 7.0;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (neighbor instanceof BlockCrossingGatePole || neighbor instanceof BlockCrossingGateBase) {
                shiftDir = dir;
                break;
            }
        }
        // Chained: adjacent sign-like block with CG pole behind it
        if (shiftDir == null) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (isSignLike(level, pos.relative(dir))) {
                    Block beyond = level.getBlockState(pos.relative(dir, 2)).getBlock();
                    if (beyond instanceof BlockCrossingGatePole) {
                        shiftDir = dir;
                        break;
                    }
                }
            }
        }
        // Back-to-back: adjacent sign-like block facing opposite direction
        if (shiftDir == null) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState neighborState = level.getBlockState(pos.relative(dir));
                if ((neighborState.getBlock() instanceof BlockSign || neighborState.getBlock() instanceof BlockStreetSign)
                        && neighborState.hasProperty(BlockStateProperties.ROTATION_16)) {
                    int neighborRot = neighborState.getValue(BlockStateProperties.ROTATION_16);
                    if (Math.abs(neighborRot - rotation) == 8) {
                        shiftDir = dir;
                        break;
                    }
                }
            }
        }

        // Y bounds: plates stacked from bottom (4px each)
        int plateCount = 1;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof StreetSignBlockEntity ssbe) {
            plateCount = Math.max(1, ssbe.getSignCount());
        }
        double yMin, yMax;
        if (isHPMount) {
            yMin = 8.0 - plateCount * 2.0;
            yMax = 8.0 + plateCount * 2.0;
        } else {
            yMin = 0;
            yMax = plateCount * 4.0;
        }

        if (!isCardinal) {
            if (shiftDir != null) {
                double offsetX = shiftDir.getStepX() * shiftPixels;
                double offsetZ = shiftDir.getStepZ() * shiftPixels;
                return Block.box(offsetX, yMin, offsetZ, 16 + offsetX, yMax, 16 + offsetZ);
            }
            return Block.box(0, yMin, 0, 16, yMax, 16);
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
            double offsetX = shiftDir.getStepX() * shiftPixels;
            double offsetZ = shiftDir.getStepZ() * shiftPixels;
            return Block.box(minX + offsetX, yMin, minZ + offsetZ, maxX + offsetX, yMax, maxZ + offsetZ);
        }

        return switch (steps) {
            case 1, 3 -> Block.box(5, yMin, 0, 11, yMax, 16);
            default -> Block.box(0, yMin, 5, 16, yMax, 11);
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
        return new StreetSignBlockEntity(pos, state);
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

    @Override
    protected InteractionResult useItemOn(
            net.minecraft.world.item.ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof StreetSignBlockEntity streetSignBE)) {
            return InteractionResult.PASS;
        }

        // Stack another sign plate — only when NOT sneaking (sneak to place adjacent)
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                && blockItem.getBlock() instanceof BlockStreetSign
                && !player.isSecondaryUseActive()) {
            if (streetSignBE.getSignCount() < StreetSignBlockEntity.MAX_SIGNS) {
                if (!level.isClientSide()) {
                    int newIdx = streetSignBE.addSign(0);
                    if (newIdx >= 0) {
                        streetSignBE.setRotation(newIdx, state.getValue(ROTATION));
                    }
                    streetSignBE.syncToClient();
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                if (level.isClientSide()) {
                    Minecraft.getInstance().setScreen(new StreetSignGui(streetSignBE));
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME; // full, don't place new block
        }

        // Dye interaction: change text color
        if (stack.getItem() instanceof net.minecraft.world.item.DyeItem dyeItem) {
            if (!level.isClientSide()) {
                streetSignBE.setTextColor(dyeItem.getDyeColor().getTextColor());
                streetSignBE.syncToClient();
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.DYE_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                stack.consume(1, player);
            }
            return InteractionResult.SUCCESS;
        }

        // Glow ink sac: make text glow
        if (stack.is(net.minecraft.world.item.Items.GLOW_INK_SAC)) {
            if (!level.isClientSide() && !streetSignBE.hasGlowingText()) {
                streetSignBE.setGlowingText(true);
                streetSignBE.syncToClient();
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GLOW_INK_SAC_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                stack.consume(1, player);
            }
            return InteractionResult.SUCCESS;
        }

        // Ink sac: remove glow
        if (stack.is(net.minecraft.world.item.Items.INK_SAC)) {
            if (!level.isClientSide() && streetSignBE.hasGlowingText()) {
                streetSignBE.setGlowingText(false);
                streetSignBE.syncToClient();
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.INK_SAC_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                stack.consume(1, player);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // Screwdriver bypasses GUI — let screwdriver rotation handle it
        if (player.getMainHandItem().getItem() instanceof ItemScrewdriver
                || player.getOffhandItem().getItem() instanceof ItemScrewdriver) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof StreetSignBlockEntity streetSignBE) {
                Minecraft.getInstance().setScreen(new StreetSignGui(streetSignBE));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
