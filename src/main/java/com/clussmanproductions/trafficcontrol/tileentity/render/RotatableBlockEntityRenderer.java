package com.clussmanproductions.trafficcontrol.tileentity.render;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGateBase;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGatePole;
import com.clussmanproductions.trafficcontrol.blocks.BlockHorizontalPole;
import com.clussmanproductions.trafficcontrol.blocks.BlockSign;
import com.clussmanproductions.trafficcontrol.blocks.BlockSignalArm;
import com.clussmanproductions.trafficcontrol.blocks.BlockStreetSign;
import com.clussmanproductions.trafficcontrol.blocks.BlockTrafficLight;
import com.clussmanproductions.trafficcontrol.signs.Sign;
import com.clussmanproductions.trafficcontrol.tileentity.RotatableBlockEntity;
import com.clussmanproductions.trafficcontrol.tileentity.SignBlockEntity;
import com.clussmanproductions.trafficcontrol.tileentity.StreetSignBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class RotatableBlockEntityRenderer implements BlockEntityRenderer<RotatableBlockEntity, RotatableBlockEntityRenderState> {


    public static final StandaloneModelKey<BlockStateModel> HORIZONTAL_BAR_MODEL_KEY =
            new StandaloneModelKey<>(() -> ModTrafficControl.MODID + ":horizontal_bar");
    public static final StandaloneModelKey<BlockStateModel> BACK_POLE_MODEL_KEY =
            new StandaloneModelKey<>(() -> ModTrafficControl.MODID + ":back_pole");
    public static final StandaloneModelKey<BlockStateModel> HORIZONTAL_POLE_MODEL_KEY =
            new StandaloneModelKey<>(() -> ModTrafficControl.MODID + ":horizontal_pole");
    public static final StandaloneModelKey<BlockStateModel> HORIZONTAL_BAR_CONNECT_MODEL_KEY =
            new StandaloneModelKey<>(() -> ModTrafficControl.MODID + ":horizontal_bar_connect");
    public static final StandaloneModelKey<BlockStateModel> TRAFFIC_LIGHT_CONNECT_MODEL_KEY =
            new StandaloneModelKey<>(() -> ModTrafficControl.MODID + ":traffic_light_connect");
    public static final StandaloneModelKey<BlockStateModel> SIGNAL_ARM_BAR_MODEL_KEY =
            new StandaloneModelKey<>(() -> ModTrafficControl.MODID + ":signal_arm_bar");
    public static final StandaloneModelKey<BlockStateModel> TRAFFIC_LIGHT_PAIRED_MODEL_KEY =
            new StandaloneModelKey<>(() -> ModTrafficControl.MODID + ":traffic_light_paired");
    public static final StandaloneModelKey<BlockStateModel> TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY =
            new StandaloneModelKey<>(() -> ModTrafficControl.MODID + ":traffic_light_pole_arm");
    public static final StandaloneModelKey<BlockStateModel> HANGING_BRACKET_MODEL_KEY =
            new StandaloneModelKey<>(() -> ModTrafficControl.MODID + ":hanging_bracket");

    // PoseStack Y rotations for each cardinal direction
    private static final float[] DIR_ROTATIONS = new float[4];
    static {
        DIR_ROTATIONS[Direction.NORTH.get2DDataValue()] = 0;
        DIR_ROTATIONS[Direction.SOUTH.get2DDataValue()] = 180;
        DIR_ROTATIONS[Direction.WEST.get2DDataValue()] = 90;
        DIR_ROTATIONS[Direction.EAST.get2DDataValue()] = 270;
    }

    private final BlockRenderDispatcher blockRenderer;

    public RotatableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.blockRenderDispatcher();
    }

    @Override
    public RotatableBlockEntityRenderState createRenderState() {
        return new RotatableBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(
            RotatableBlockEntity blockEntity,
            RotatableBlockEntityRenderState renderState,
            float partialTick,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        var state = blockEntity.getBlockState();
        if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
            int rotation = state.getValue(BlockStateProperties.ROTATION_16);
            renderState.rotationDegrees = RotationSegment.convertToDegrees(rotation);
        }

        renderState.horizontalBarDirection = null;
        renderState.mountedOnPole = false;
        renderState.mountedOnHorizontalPole = false;
        renderState.pairedAcrossPole = false;
        renderState.adjacentTrafficLightConnection = false;
        renderState.hasAdjacentTrafficLight = false;
        renderState.signalArmBarDirection = null;
        renderState.signalArmConnectsToLight = false;
        renderState.onCrossingGateBase = false;
        renderState.hasSideBySideNeighbor = false;
        renderState.sideBySidePoleDirection = null;
        renderState.horizontalPoleDirs.clear();
        renderState.signalArmTrafficLightDirs.clear();
        renderState.nonCardinalTLDirs.clear();
        renderState.cardinalTLDirs.clear();
        renderState.signDirs.clear();
        renderState.signToSignDirs.clear();
        renderState.backToBackSignDir = null;
        renderState.backToBackTLDir = null;
        renderState.extendPoleUp = false;
        renderState.extendPoleDown = false;
        renderState.cgPoleArmDirs.clear();
        renderState.streetSignDirs.clear();
        renderState.hanging = false;
        renderState.streetSignCount = 0;
        for (int i = 0; i < 4; i++) {
            renderState.streetSignTexts[i] = "";
            renderState.streetSignTexts2[i] = "";
            renderState.streetSignFillColors[i] = 0xFF006400;
            renderState.streetSignRotations[i] = 0;
        }
        renderState.streetSignTextColor = 0xFFFFFFFF;
        renderState.streetSignGlowing = false;
        renderState.signFrontTexture = null;
        renderState.signBackTexture = null;

        // Lazily initialize sign repository on first render if needed
        if (!ModTrafficControl.SIGN_REPO.isInitialized()) {
            var resourceManager = Minecraft.getInstance().getResourceManager();
            ModTrafficControl.SIGN_REPO.init(resourceManager);
        }

        // Extract sign textures from SignBlockEntity
        if (blockEntity instanceof SignBlockEntity signBE && signBE.getSignId() != null) {
            Sign sign = ModTrafficControl.SIGN_REPO.getSignByID(signBE.getSignId());
            if (sign != null) {
                renderState.signFrontTexture = sign.getFrontTexture();
                renderState.signBackTexture = sign.getBackTexture();
                ensureTextureLoaded(renderState.signFrontTexture);
                if (renderState.signBackTexture != null) {
                    ensureTextureLoaded(renderState.signBackTexture);
                }
            }
        }

        Level level = blockEntity.getLevel();
        if (level == null) return;

        BlockPos pos = blockEntity.getBlockPos();
        int rotation = state.hasProperty(BlockStateProperties.ROTATION_16)
                ? state.getValue(BlockStateProperties.ROTATION_16) : 0;

        // Sign / Street sign: detect mounting pole and find adjacent connectable blocks
        // Both BlockSign and non-hanging BlockStreetSign share the same pole mounting logic.
        boolean isSignLike = state.getBlock() instanceof BlockSign
                || (state.getBlock() instanceof BlockStreetSign
                    && (!state.hasProperty(BlockStreetSign.HANGING) || !state.getValue(BlockStreetSign.HANGING)));
        if (isSignLike) {
            // First pass: prefer horizontal pole for mounting
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockHorizontalPole) {
                    renderState.mountedOnPole = true;
                    renderState.mountedOnHorizontalPole = true;
                    renderState.horizontalBarDirection = dir;
                    break;
                }
            }
            // Fallback: crossing gate pole
            if (!renderState.mountedOnPole) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                    if (neighbor instanceof BlockCrossingGatePole) {
                        renderState.mountedOnPole = true;
                        renderState.mountedOnHorizontalPole = false;
                        renderState.horizontalBarDirection = dir;
                        break;
                    }
                }
            }
            // Chained mount: adjacent sign-like block with CG pole behind it
            if (!renderState.mountedOnPole) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                    if (neighbor instanceof BlockSign || neighbor instanceof BlockStreetSign) {
                        Block beyond = level.getBlockState(pos.relative(dir, 2)).getBlock();
                        if (beyond instanceof BlockCrossingGatePole) {
                            renderState.mountedOnPole = true;
                            renderState.mountedOnHorizontalPole = false;
                            renderState.horizontalBarDirection = dir;
                            break;
                        }
                    }
                }
            }
            // Back-to-back: adjacent sign-like block facing opposite direction (rotation diff of 8)
            if (!renderState.mountedOnPole && state.hasProperty(BlockStateProperties.ROTATION_16)) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockState neighborState = level.getBlockState(pos.relative(dir));
                    if ((neighborState.getBlock() instanceof BlockSign || neighborState.getBlock() instanceof BlockStreetSign)
                            && neighborState.hasProperty(BlockStateProperties.ROTATION_16)) {
                        int neighborRot = neighborState.getValue(BlockStateProperties.ROTATION_16);
                        if (Math.abs(neighborRot - rotation) == 8) {
                            renderState.mountedOnPole = true;
                            renderState.mountedOnHorizontalPole = false;
                            renderState.horizontalBarDirection = dir;
                            renderState.backToBackSignDir = dir;
                            break;
                        }
                    }
                }
            }
            // Collect connectable neighbors for arm rendering
            // Includes adjacent signs for sign-to-sign chaining on CG poles
            // Skip back-to-back signs (rotation diff of 8) — no arm needed
            // Street signs don't render arms toward poles/TLs — they shift toward the pole instead
            // Street signs never render arms — all arm models are full-block height
            // and poke through the 4px-tall plate. They shift toward poles instead.
            boolean isCurrentStreetSign = state.getBlock() instanceof BlockStreetSign;
            if (!isCurrentStreetSign) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                    if (neighbor instanceof BlockCrossingGatePole
                            || neighbor instanceof BlockCrossingGateBase
                            || neighbor instanceof BlockTrafficLight) {
                        renderState.signalArmTrafficLightDirs.add(dir);
                    } else if (neighbor instanceof BlockSign || neighbor instanceof BlockStreetSign) {
                        BlockState neighborState = level.getBlockState(pos.relative(dir));
                        boolean isBackToBack = neighborState.hasProperty(BlockStateProperties.ROTATION_16)
                                && Math.abs(neighborState.getValue(BlockStateProperties.ROTATION_16) - rotation) == 8;
                        if (!isBackToBack) {
                            renderState.signalArmTrafficLightDirs.add(dir);
                            renderState.signToSignDirs.add(dir);
                        }
                    }
                }
            }
            // Add arm toward mounting HP only (not all adjacent HPs).
            // Street signs shift toward the HP so they don't need their own arm — the HP handles it.
            if (renderState.mountedOnHorizontalPole && renderState.horizontalBarDirection != null
                    && !(state.getBlock() instanceof BlockStreetSign)) {
                renderState.signalArmTrafficLightDirs.add(renderState.horizontalBarDirection);
            }
        }

        // Street sign: hanging state, CG pole below, and text/color data
        if (state.getBlock() instanceof BlockStreetSign) {
            if (state.hasProperty(BlockStreetSign.HANGING)) {
                renderState.hanging = state.getValue(BlockStreetSign.HANGING);
            }
            if (!renderState.hanging) {
                Block blockBelow = level.getBlockState(pos.below()).getBlock();
                if (blockBelow instanceof BlockCrossingGatePole || blockBelow instanceof BlockCrossingGateBase) {
                    renderState.extendPoleDown = true;
                }
            }
            if (blockEntity instanceof StreetSignBlockEntity streetSignBE) {
                renderState.streetSignCount = streetSignBE.getSignCount();
                for (int i = 0; i < renderState.streetSignCount; i++) {
                    renderState.streetSignTexts[i] = streetSignBE.getText(i);
                    renderState.streetSignTexts2[i] = streetSignBE.getText2(i);
                    renderState.streetSignRotations[i] = net.minecraft.world.level.block.state.properties.RotationSegment.convertToDegrees(streetSignBE.getRotation(i));
                    renderState.streetSignFillColors[i] = switch (streetSignBE.getColorIndex(i)) {
                        case 1 -> 0xFFCC0000; // Red
                        case 2 -> 0xFF0000CC; // Blue
                        case 3 -> 0xFFCCCC00; // Yellow
                        default -> 0xFF006400; // Green
                    };
                }
                renderState.streetSignTextColor = streetSignBE.getTextColor() | 0xFF000000;
                renderState.streetSignGlowing = streetSignBE.hasGlowingText();
            }
        }

        // Crossing gate pole: read connection state for arm rendering
        // Skip arms toward street signs — the arm model is full-block height and pokes through the 4px plate
        if (state.getBlock() instanceof BlockCrossingGatePole) {
            Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
            net.minecraft.world.level.block.state.properties.BooleanProperty[] props = {
                    BlockCrossingGatePole.NORTH, BlockCrossingGatePole.SOUTH,
                    BlockCrossingGatePole.EAST, BlockCrossingGatePole.WEST};
            for (int i = 0; i < 4; i++) {
                if (state.getValue(props[i])) {
                    Block neighbor = level.getBlockState(pos.relative(dirs[i])).getBlock();
                    if (!(neighbor instanceof BlockStreetSign)) {
                        renderState.cgPoleArmDirs.add(dirs[i]);
                    }
                }
            }
        }

        // Signal arm: find adjacent traffic lights to render bars toward
        if (state.getBlock() instanceof BlockSignalArm) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockTrafficLight) {
                    renderState.signalArmTrafficLightDirs.add(dir);
                }
            }
        }

        // Horizontal pole: find adjacent traffic lights and crossing gate poles to render ext arms toward
        if (state.getBlock() instanceof BlockHorizontalPole) {
            // First pass: collect adjacent TLs and their rotations
            java.util.Map<Direction, Integer> adjacentTLRotations = new java.util.EnumMap<>(Direction.class);
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState neighborState = level.getBlockState(pos.relative(dir));
                Block neighbor = neighborState.getBlock();
                if (neighbor instanceof BlockTrafficLight
                        && neighborState.hasProperty(BlockStateProperties.ROTATION_16)) {
                    adjacentTLRotations.put(dir, neighborState.getValue(BlockStateProperties.ROTATION_16));
                } else if (neighbor instanceof BlockCrossingGatePole
                        || neighbor instanceof BlockCrossingGateBase
                        || neighbor instanceof BlockSign) {
                    renderState.signalArmTrafficLightDirs.add(dir);
                    if (neighbor instanceof BlockSign) {
                        renderState.signDirs.add(dir);
                        renderState.streetSignDirs.add(dir);
                    }
                } else if (neighbor instanceof BlockStreetSign
                        && (!neighborState.hasProperty(BlockStreetSign.HANGING)
                            || !neighborState.getValue(BlockStreetSign.HANGING))) {
                    renderState.signalArmTrafficLightDirs.add(dir);
                    renderState.streetSignDirs.add(dir);
                }
            }
            // Second pass: add TLs, but skip back-to-back pairs (opposite dirs, rotation diff of 8)
            for (var entry : adjacentTLRotations.entrySet()) {
                Direction dir = entry.getKey();
                int tlRot = entry.getValue();
                Direction opposite = dir.getOpposite();
                boolean isBackToBack = adjacentTLRotations.containsKey(opposite)
                        && Math.abs(adjacentTLRotations.get(opposite) - tlRot) == 8;
                renderState.signalArmTrafficLightDirs.add(dir);
                if (tlRot % 4 != 0) {
                    renderState.nonCardinalTLDirs.add(dir);
                } else {
                    renderState.cardinalTLDirs.add(dir);
                }
            }
        }

        // --- Traffic light: detect neighbors with clean separated flags ---
        Direction poleDir = null;

        if (state.getBlock() instanceof BlockTrafficLight) {
            // 1. Find adjacent pole — prefer horizontal pole over crossing gate pole for row alignment
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockHorizontalPole) {
                    renderState.mountedOnPole = true;
                    renderState.mountedOnHorizontalPole = true;
                    poleDir = dir;
                    renderState.horizontalBarDirection = dir;
                    break;
                }
            }
            // Fallback: if no horizontal pole, look for crossing gate pole
            if (poleDir == null) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                    if (isVerticalPoleBlock(neighbor)) {
                        renderState.mountedOnPole = true;
                        renderState.mountedOnHorizontalPole = false;
                        poleDir = dir;
                        renderState.horizontalBarDirection = dir;
                        break;
                    }
                }
            }

            // Back-to-back: adjacent TL facing opposite direction (no pole between)
            if (poleDir == null && state.hasProperty(BlockStateProperties.ROTATION_16)) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockState neighborState = level.getBlockState(pos.relative(dir));
                    if (neighborState.getBlock() instanceof BlockTrafficLight
                            && neighborState.hasProperty(BlockStateProperties.ROTATION_16)) {
                        int neighborRot = neighborState.getValue(BlockStateProperties.ROTATION_16);
                        if (Math.abs(neighborRot - rotation) == 8) {
                            renderState.mountedOnPole = true;
                            renderState.mountedOnHorizontalPole = false;
                            poleDir = dir;
                            renderState.horizontalBarDirection = dir;
                            renderState.backToBackTLDir = dir;
                            break;
                        }
                    }
                }
            }

            // 2. Check for true back-to-back pair (pairedAcrossPole)
            if (poleDir != null && state.hasProperty(BlockStateProperties.ROTATION_16)) {
                BlockPos beyondPole = pos.relative(poleDir, 2);
                BlockState beyondState = level.getBlockState(beyondPole);
                if (beyondState.getBlock() instanceof BlockTrafficLight
                        && beyondState.hasProperty(BlockStateProperties.ROTATION_16)) {
                    int beyondRot = beyondState.getValue(BlockStateProperties.ROTATION_16);
                    if (Math.abs(beyondRot - rotation) == 8) {
                        renderState.pairedAcrossPole = true;
                    }
                }
            }

            // 3. Check signal arms, horizontal poles, adjacent traffic lights, crossing_gate info
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockSignalArm) {
                    renderState.signalArmBarDirection = dir;
                    Block beyondArm = level.getBlockState(pos.relative(dir, 2)).getBlock();
                    if (beyondArm instanceof BlockTrafficLight) {
                        renderState.signalArmConnectsToLight = true;
                    }
                }
                if (neighbor instanceof BlockCrossingGatePole
                        || neighbor instanceof BlockCrossingGateBase
                        || neighbor instanceof BlockSign
                        || neighbor instanceof BlockStreetSign) {
                    renderState.onCrossingGateBase = true;
                    // Render horizontal bar toward crossing gate pole/sign to bridge the gap.
                    // Skip for street signs (bar pokes through 4px plate).
                    boolean isStreetSignNeighbor = neighbor instanceof BlockStreetSign;
                    if (!dir.equals(poleDir) && !isStreetSignNeighbor) {
                        renderState.horizontalPoleDirs.add(dir);
                    }
                }
                // Horizontal pole: don't add to horizontalPoleDirs — the HP renders
                // its own bar toward the TL from its side, avoiding poke-through
                if (neighbor instanceof BlockHorizontalPole) {
                    if (renderState.horizontalBarDirection == null) {
                        renderState.horizontalBarDirection = dir;
                    }
                }
                // Adjacent traffic light: also track for bar rendering (TL-to-TL connection)
                if (neighbor instanceof BlockTrafficLight) {
                    renderState.hasAdjacentTrafficLight = true;
                    // Skip bar for back-to-back TLs (bridge rendered separately)
                    if (renderState.backToBackTLDir == null || !dir.equals(renderState.backToBackTLDir)) {
                        renderState.horizontalPoleDirs.add(dir);
                    }
                    if (renderState.horizontalBarDirection == null) {
                        renderState.horizontalBarDirection = dir;
                    }
                    BlockState neighborState = level.getBlockState(pos.relative(dir));
                    int neighborRot = neighborState.hasProperty(BlockStateProperties.ROTATION_16)
                            ? neighborState.getValue(BlockStateProperties.ROTATION_16) : 0;
                    if (neighborRot != rotation) {
                        renderState.adjacentTrafficLightConnection = true;
                    }
                    // Side-by-side: both TLs on HP, same rotation, adjacent perpendicular to HP
                    if (neighborRot == rotation
                            && renderState.mountedOnHorizontalPole
                            && renderState.horizontalBarDirection != null
                            && dir.getAxis() != renderState.horizontalBarDirection.getAxis()) {
                        renderState.hasSideBySideNeighbor = true;
                        renderState.sideBySidePoleDirection = dir;
                    }
                }
            }

            // 4. Check for pole/TL/sign above and below — extend vertical pole
            Block tlBlockAbove = level.getBlockState(pos.above()).getBlock();
            Block tlBlockBelow = level.getBlockState(pos.below()).getBlock();
            if (tlBlockAbove instanceof BlockCrossingGatePole || tlBlockAbove instanceof BlockCrossingGateBase
                    || tlBlockAbove instanceof BlockTrafficLight || tlBlockAbove instanceof BlockSign) {
                renderState.extendPoleUp = true;
            }
            if (tlBlockBelow instanceof BlockCrossingGatePole || tlBlockBelow instanceof BlockCrossingGateBase
                    || tlBlockBelow instanceof BlockTrafficLight || tlBlockBelow instanceof BlockSign) {
                renderState.extendPoleDown = true;
            }
        }
    }

    private static boolean isPoleBlock(Block block) {
        return block instanceof BlockCrossingGatePole
                || block instanceof BlockHorizontalPole
                || block instanceof BlockCrossingGateBase;
    }

    private static boolean isVerticalPoleBlock(Block block) {
        return block instanceof BlockCrossingGatePole
                || block instanceof BlockHorizontalPole;
    }

    @Override
    public void submit(
            RotatableBlockEntityRenderState renderState,
            PoseStack poseStack,
            SubmitNodeCollector nodeCollector,
            CameraRenderState cameraRenderState
    ) {
        BlockState state = renderState.blockState;
        BlockStateModel baseModel = this.blockRenderer.getBlockModel(state);
        boolean isSign = state.getBlock() instanceof BlockSign || state.getBlock() instanceof BlockStreetSign;
        RenderType renderType = isSign
                ? RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS)
                : RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS);

        boolean isTrafficLight = state.getBlock() instanceof BlockTrafficLight;
        boolean isHorizTL = isTrafficLight && state.getBlock().getDescriptionId().contains("horiz");
        // Side-by-side: vertical TLs get connect model, horizontal TLs get spacing shift
        boolean sideBySide = isTrafficLight && !isHorizTL && renderState.hasSideBySideNeighbor;

        // Pick body model: always use the block's own model so all variants render correctly
        BlockStateModel bodyModel = baseModel;
        BlockStateModel connectModel = null;
        if (sideBySide) {
            ModelManager mm = Minecraft.getInstance().getModelManager();
            connectModel = mm.getStandaloneModel(TRAFFIC_LIGHT_CONNECT_MODEL_KEY);
            if (connectModel != null) {
                bodyModel = connectModel;
            }
        }


        boolean isStreetSign = state.getBlock() instanceof BlockStreetSign;
        boolean isHangingStreetSign = isStreetSign && renderState.hanging;

        // --- Pole-mounted shift ---
        // Shift toward pole only when there's no adjacent traffic light (side-by-side row)
        // and no other pole connections (e.g. CG pole on the opposite side).
        // When TLs are adjacent or between two poles, keep centered.
        // Street signs always shift when mounted (both HP and CG pole).
        // Regular signs only shift on CG pole (HP provides flush mount).
        // Back-to-back TLs always shift (bridge bar connects them).
        // Regular TLs shift when no horizontal bars and no side-by-side adjacent TLs.
        boolean shiftToPole = (isTrafficLight && renderState.backToBackTLDir != null
                && renderState.horizontalBarDirection != null)
                || (isTrafficLight && renderState.mountedOnPole
                && renderState.horizontalBarDirection != null
                && (!renderState.hasAdjacentTrafficLight || renderState.backToBackTLDir != null)
                && renderState.horizontalPoleDirs.isEmpty())
                || (state.getBlock() instanceof BlockSign && renderState.mountedOnPole
                && !renderState.mountedOnHorizontalPole
                && renderState.horizontalBarDirection != null)
                || (isStreetSign && !isHangingStreetSign && renderState.mountedOnPole
                && !renderState.mountedOnHorizontalPole
                && renderState.horizontalBarDirection != null);
        // Street signs use a smaller shift (7/16) so the plate edge stops at the
        // CG pole column face (at 7/16 into the pole block) instead of overlapping it.
        float poleShiftAmount = isStreetSign ? 7.0f / 16.0f : 9.0f / 16.0f;

        // --- Render body (rotated) ---
        // Skip body model for regular signs (BlockSign) when mounted on a pole —
        // the model only contains a center pole element; the sign face renders as a quad separately
        boolean skipBodyModel = (state.getBlock() instanceof BlockSign && shiftToPole)
                || isStreetSign; // street sign plates are rendered dynamically
        if (!skipBodyModel) {
            poseStack.pushPose();

            // Hanging street sign: shift body UP so sign sits at Y 15-19
            if (isHangingStreetSign) {
                poseStack.translate(0, 9.0f / 16.0f, 0);
            }

            // World-space shift toward pole (applied after rotation in transform order)
            if (shiftToPole) {
                Direction poleDir = renderState.horizontalBarDirection;
                poseStack.translate(poleDir.getStepX() * poleShiftAmount, 0,
                        poleDir.getStepZ() * poleShiftAmount);
            }

            // Horizontal TLs: no shifting — overlap between adjacent frames is acceptable

            poseStack.translate(0.5f, 0.0f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.rotationDegrees));
            poseStack.translate(-0.5f, 0.0f, -0.5f);


            nodeCollector.submitBlockModel(
                    poseStack, renderType, bodyModel,
                    1.0f, 1.0f, 1.0f,
                    renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
            );

            poseStack.popPose();
        }

        // --- Street sign: render stacked plates (fill + border + text per plate) ---
        // Each plate is 4px tall, stacking from bottom (index 0) to top.
        // Block model is skipped; plates are rendered entirely as custom geometry.
        // Always render at least 1 plate (avoids invisible block when signCount=0)
        if (isStreetSign) {
            int plateCount = Math.max(1, renderState.streetSignCount);
            Identifier signTex = Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "textures/block/street_sign.png");
            ensureTextureLoaded(signTex);

            int light = renderState.lightCoords;
            int overlay = OverlayTexture.NO_OVERLAY;
            Font font = Minecraft.getInstance().font;
            float maxUsableWidth = 0.875f; // 14/16 blocks

            for (int plateIdx = 0; plateIdx < plateCount; plateIdx++) {
                // Y position: stack from bottom up, sitting on whatever is below
                float plateY1 = plateIdx * 4f / 16f;
                float plateY2 = plateY1 + 4f / 16f;
                float plateCenterY = (plateY1 + plateY2) / 2f;

                // Color index → UV row in street_sign.png (4 rows: green, red, blue, yellow)
                int colorIdx = plateIdx < renderState.streetSignCount
                        ? ((renderState.streetSignFillColors[plateIdx] >> 8) & 0xFF) == 0xCC ? 1  // red
                        : (renderState.streetSignFillColors[plateIdx] & 0xFF) == 0xCC ? 2         // blue
                        : ((renderState.streetSignFillColors[plateIdx] >> 8) & 0xFF) == 0xCC00 ? 3 // yellow (won't match)
                        : 0 : 0; // green default
                // Simpler: map from ARGB fill color to row index
                int fc = plateIdx < renderState.streetSignCount ? renderState.streetSignFillColors[plateIdx] : 0xFF006400;
                int row = switch (fc) {
                    case 0xFFCC0000 -> 1; // Red
                    case 0xFF0000CC -> 2; // Blue
                    case 0xFFCCCC00 -> 3; // Yellow
                    default -> 0;         // Green
                };
                float v1 = row * 0.25f;
                float v2 = v1 + 0.25f;

                float x1 = 0, x2 = 1;
                float zSouth = 9f / 16f;
                float zNorth = 7f / 16f;

                // Per-plate rotation
                float plateRotation = plateIdx < renderState.streetSignCount
                        ? renderState.streetSignRotations[plateIdx] : renderState.rotationDegrees;

                // Common transform for this plate
                poseStack.pushPose();
                // Hanging signs: offset plates to Y 15+ so bracket chains connect at Y 19
                if (isHangingStreetSign) poseStack.translate(0, 15.0f / 16.0f, 0);
                if (shiftToPole) {
                    Direction poleDir = renderState.horizontalBarDirection;
                    poseStack.translate(poleDir.getStepX() * poleShiftAmount, 0,
                            poleDir.getStepZ() * poleShiftAmount);
                }
                poseStack.translate(0.5f, 0.0f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(-plateRotation));
                poseStack.translate(-0.5f, 0.0f, -0.5f);

                // All faces in one call — front, back, and 3D edges
                final float fy1 = plateY1, fy2 = plateY2;
                final float uv1 = v1, uv2 = v2;
                // Edge UVs: each edge samples its own border strip from the plate's row
                final float rowV = row * 0.25f;         // row start in V
                final float pxU = 1f / 16f;             // 1 pixel in U
                final float pxV = 1f / 16f;             // 1 pixel in V
                RenderType signRt = RenderTypes.entitySolid(signTex);
                nodeCollector.submitCustomGeometry(poseStack, signRt, (pose, consumer) -> {
                    Matrix4f m = pose.pose();
                    // South face (front)
                    Vector3f nS = pose.transformNormal(0, 0, 1, new Vector3f());
                    consumer.addVertex(m, x1, fy2, zSouth).setColor(255, 255, 255, 255)
                            .setUv(0, uv1).setOverlay(overlay).setLight(light).setNormal(nS.x, nS.y, nS.z);
                    consumer.addVertex(m, x1, fy1, zSouth).setColor(255, 255, 255, 255)
                            .setUv(0, uv2).setOverlay(overlay).setLight(light).setNormal(nS.x, nS.y, nS.z);
                    consumer.addVertex(m, x2, fy1, zSouth).setColor(255, 255, 255, 255)
                            .setUv(1, uv2).setOverlay(overlay).setLight(light).setNormal(nS.x, nS.y, nS.z);
                    consumer.addVertex(m, x2, fy2, zSouth).setColor(255, 255, 255, 255)
                            .setUv(1, uv1).setOverlay(overlay).setLight(light).setNormal(nS.x, nS.y, nS.z);
                    // North face (back)
                    Vector3f nN = pose.transformNormal(0, 0, -1, new Vector3f());
                    consumer.addVertex(m, x2, fy2, zNorth).setColor(255, 255, 255, 255)
                            .setUv(0, uv1).setOverlay(overlay).setLight(light).setNormal(nN.x, nN.y, nN.z);
                    consumer.addVertex(m, x2, fy1, zNorth).setColor(255, 255, 255, 255)
                            .setUv(0, uv2).setOverlay(overlay).setLight(light).setNormal(nN.x, nN.y, nN.z);
                    consumer.addVertex(m, x1, fy1, zNorth).setColor(255, 255, 255, 255)
                            .setUv(1, uv2).setOverlay(overlay).setLight(light).setNormal(nN.x, nN.y, nN.z);
                    consumer.addVertex(m, x1, fy2, zNorth).setColor(255, 255, 255, 255)
                            .setUv(1, uv1).setOverlay(overlay).setLight(light).setNormal(nN.x, nN.y, nN.z);
                    // All edges solid white — sample from center of top border (pure white pixel)
                    float wu = 0.5f, wv = rowV + 0.03f; // center of white border area
                    // Top edge
                    Vector3f nUp = pose.transformNormal(0, 1, 0, new Vector3f());
                    consumer.addVertex(m, x1, fy2, zNorth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nUp.x, nUp.y, nUp.z);
                    consumer.addVertex(m, x1, fy2, zSouth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nUp.x, nUp.y, nUp.z);
                    consumer.addVertex(m, x2, fy2, zSouth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nUp.x, nUp.y, nUp.z);
                    consumer.addVertex(m, x2, fy2, zNorth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nUp.x, nUp.y, nUp.z);
                    // Bottom edge
                    Vector3f nDown = pose.transformNormal(0, -1, 0, new Vector3f());
                    consumer.addVertex(m, x1, fy1, zSouth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nDown.x, nDown.y, nDown.z);
                    consumer.addVertex(m, x1, fy1, zNorth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nDown.x, nDown.y, nDown.z);
                    consumer.addVertex(m, x2, fy1, zNorth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nDown.x, nDown.y, nDown.z);
                    consumer.addVertex(m, x2, fy1, zSouth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nDown.x, nDown.y, nDown.z);
                    // Left edge
                    Vector3f nW = pose.transformNormal(-1, 0, 0, new Vector3f());
                    consumer.addVertex(m, x1, fy2, zNorth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nW.x, nW.y, nW.z);
                    consumer.addVertex(m, x1, fy1, zNorth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nW.x, nW.y, nW.z);
                    consumer.addVertex(m, x1, fy1, zSouth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nW.x, nW.y, nW.z);
                    consumer.addVertex(m, x1, fy2, zSouth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nW.x, nW.y, nW.z);
                    // Right edge
                    Vector3f nE = pose.transformNormal(1, 0, 0, new Vector3f());
                    consumer.addVertex(m, x2, fy2, zSouth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nE.x, nE.y, nE.z);
                    consumer.addVertex(m, x2, fy1, zSouth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nE.x, nE.y, nE.z);
                    consumer.addVertex(m, x2, fy1, zNorth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nE.x, nE.y, nE.z);
                    consumer.addVertex(m, x2, fy2, zNorth).setColor(255, 255, 255, 255)
                            .setUv(wu, wv).setOverlay(overlay).setLight(light).setNormal(nE.x, nE.y, nE.z);
                });

                // 4. Text rendering (2 lines, both faces)
                String plateText1 = plateIdx < renderState.streetSignCount ? renderState.streetSignTexts[plateIdx] : "";
                String plateText2 = plateIdx < renderState.streetSignCount ? renderState.streetSignTexts2[plateIdx] : "";
                boolean hasL1 = plateText1 != null && !plateText1.isEmpty();
                boolean hasL2 = plateText2 != null && !plateText2.isEmpty();
                if (hasL1 || hasL2) {
                    FormattedCharSequence line1 = hasL1 ? FormattedCharSequence.forward(plateText1, Style.EMPTY) : null;
                    FormattedCharSequence line2 = hasL2 ? FormattedCharSequence.forward(plateText2, Style.EMPTY) : null;
                    int w1 = hasL1 ? font.width(line1) : 0;
                    int w2 = hasL2 ? font.width(line2) : 0;
                    int lineCount = (hasL1 ? 1 : 0) + (hasL2 ? 1 : 0);
                    int maxW = Math.max(w1, w2);

                    float textScale = 1.0f / 64.0f;
                    float maxUsableHeight = 0.1875f; // 3/16 blocks
                    if (maxW * textScale > maxUsableWidth) {
                        textScale = maxUsableWidth / maxW;
                    }
                    if (lineCount * font.lineHeight * textScale > maxUsableHeight) {
                        textScale = maxUsableHeight / (lineCount * font.lineHeight);
                    }
                    float finalScale = textScale;
                    float totalH = lineCount * font.lineHeight;

                    for (int face = 0; face < 2; face++) {
                        poseStack.pushPose();
                        poseStack.translate(0.5f, plateCenterY, 0.5f);
                        if (face == 1) {
                            poseStack.mulPose(Axis.YP.rotationDegrees(180));
                        }
                        poseStack.translate(0, 0, 1.5f / 16.0f);
                        poseStack.scale(finalScale, -finalScale, finalScale);

                        int textLight = 0xF000F0;
                        float yStart = -totalH / 2.0f;
                        int li = 0;
                        if (hasL1) {
                            nodeCollector.submitText(poseStack,
                                    -w1 / 2.0f, yStart + li * font.lineHeight,
                                    line1, false, Font.DisplayMode.POLYGON_OFFSET,
                                    textLight, renderState.streetSignTextColor, 0, 0);
                            li++;
                        }
                        if (hasL2) {
                            nodeCollector.submitText(poseStack,
                                    -w2 / 2.0f, yStart + li * font.lineHeight,
                                    line2, false, Font.DisplayMode.POLYGON_OFFSET,
                                    textLight, renderState.streetSignTextColor, 0, 0);
                        }
                        poseStack.popPose();
                    }
                }

                poseStack.popPose(); // end plate transform
            }
        }

        // --- Hanging bracket (chain) for street signs ---
        if (isHangingStreetSign) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BlockStateModel bracketModel = modelManager.getStandaloneModel(HANGING_BRACKET_MODEL_KEY);
            if (bracketModel != null) {
                poseStack.pushPose();
                poseStack.translate(0.5f, 0.0f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.rotationDegrees));
                poseStack.translate(-0.5f, 0.0f, -0.5f);
                nodeCollector.submitBlockModel(
                        poseStack, renderType, bracketModel,
                        1.0f, 1.0f, 1.0f,
                        renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                );
                poseStack.popPose();
            }
        }

        // --- Bridge: render connect model halfway toward adjacent TL to fill the gap ---
        // Skip for horizontal TLs (models already extend beyond block, no bridge needed)
        boolean isHorizontalTL = sideBySide && state.getBlock() instanceof BlockTrafficLight
                && state.getBlock().getDescriptionId().contains("horiz");
        if (sideBySide && !isHorizontalTL && renderState.sideBySidePoleDirection != null && connectModel != null) {
            Direction poleDir = renderState.sideBySidePoleDirection;
            poseStack.pushPose();
            // Translate halfway toward the adjacent TL
            poseStack.translate(poleDir.getStepX() * 0.5f, 0, poleDir.getStepZ() * 0.5f);
            // Apply same rotation as the body
            poseStack.translate(0.5f, 0.0f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.rotationDegrees));
            poseStack.translate(-0.5f, 0.0f, -0.5f);

            nodeCollector.submitBlockModel(
                    poseStack, renderType, connectModel,
                    1.0f, 1.0f, 1.0f,
                    renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
            );
            poseStack.popPose();
        }

        // --- Vertical pole extension for TLs/signs above/below CG poles ---
        // When shifted to a side pole, only render if bridging blocks both above AND below;
        // otherwise the center pole appears offset from the body with nothing to connect to
        boolean shouldRenderPole = (renderState.extendPoleUp || renderState.extendPoleDown);
        if (shiftToPole && isTrafficLight) {
            shouldRenderPole = renderState.extendPoleUp && renderState.extendPoleDown;
        }
        // Street signs never render the center pole — they float standalone
        // or mount on external poles (CG pole/HP provides the support)
        if (isStreetSign) shouldRenderPole = false;
        if ((isTrafficLight || isSign) && !isHangingStreetSign && shouldRenderPole) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BlockStateModel poleModel = modelManager.getStandaloneModel(BACK_POLE_MODEL_KEY);
            if (poleModel != null) {
                poseStack.pushPose();
                nodeCollector.submitBlockModel(
                        poseStack, renderType, poleModel,
                        1.0f, 1.0f, 1.0f,
                        renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                );
                poseStack.popPose();
            }
        }

        // --- Bridge for back-to-back signs/TLs: signal arm bar between the two poles ---
        if ((isSign && renderState.backToBackSignDir != null)
                || (isTrafficLight && renderState.backToBackTLDir != null)) {
            Direction b2bDirBridge = isSign ? renderState.backToBackSignDir : renderState.backToBackTLDir;
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BlockStateModel barModel = modelManager.getStandaloneModel(SIGNAL_ARM_BAR_MODEL_KEY);
            if (barModel != null) {
                poseStack.pushPose();
                // Shift with body toward the neighbor
                poseStack.translate(b2bDirBridge.getStepX() * poleShiftAmount, 0,
                        b2bDirBridge.getStepZ() * poleShiftAmount);
                // Rotate the bar to face the neighbor direction
                float barYRot = DIR_ROTATIONS[b2bDirBridge.get2DDataValue()];
                if (barYRot != 0) {
                    poseStack.translate(0.5f, 0.0f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(barYRot));
                    poseStack.translate(-0.5f, 0.0f, -0.5f);
                }
                nodeCollector.submitBlockModel(
                        poseStack, renderType, barModel,
                        1.0f, 1.0f, 1.0f,
                        renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                );
                poseStack.popPose();
            }
        }

        // Horizontal bar — for non-TLs/non-signs, use horizontalBarDirection
        if (renderState.horizontalBarDirection != null && !sideBySide && !isTrafficLight && !isSign) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            float barYRot = DIR_ROTATIONS[renderState.horizontalBarDirection.get2DDataValue()];
            BlockStateModel barModel = modelManager.getStandaloneModel(HORIZONTAL_POLE_MODEL_KEY);
            if (barModel != null) {
                poseStack.pushPose();
                if (barYRot != 0) {
                    poseStack.translate(0.5f, 0.0f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(barYRot));
                    poseStack.translate(-0.5f, 0.0f, -0.5f);
                }
                nodeCollector.submitBlockModel(
                        poseStack, renderType, barModel,
                        1.0f, 1.0f, 1.0f,
                        renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                );
                poseStack.popPose();
            }
        }

        // Traffic lights: render bar toward each adjacent horizontal pole
        if (isTrafficLight && !renderState.horizontalPoleDirs.isEmpty()) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            for (Direction dir : renderState.horizontalPoleDirs) {
                // If there's a connection on the opposite side (pole or another horizontal pole),
                // use the full-length model so the bar passes through the TL frame.
                // Otherwise use the short arm (TL is at the end of a run).
                Direction opposite = dir.getOpposite();
                boolean hasOppositeConnection = renderState.mountedOnPole
                        || renderState.horizontalPoleDirs.contains(opposite);

                StandaloneModelKey<BlockStateModel> barKey = hasOppositeConnection
                        ? HORIZONTAL_POLE_MODEL_KEY : SIGNAL_ARM_BAR_MODEL_KEY;
                BlockStateModel barModel = modelManager.getStandaloneModel(barKey);
                if (barModel != null) {
                    float barYRot2 = DIR_ROTATIONS[dir.get2DDataValue()];
                    poseStack.pushPose();
                    // Shift bar with the body so it stays connected when pole-mounted
                    if (shiftToPole) {
                        Direction poleDir2 = renderState.horizontalBarDirection;
                        poseStack.translate(poleDir2.getStepX() * poleShiftAmount, 0,
                                poleDir2.getStepZ() * poleShiftAmount);
                    }
                    if (barYRot2 != 0) {
                        poseStack.translate(0.5f, 0.0f, 0.5f);
                        poseStack.mulPose(Axis.YP.rotationDegrees(barYRot2));
                        poseStack.translate(-0.5f, 0.0f, -0.5f);
                    }
                    nodeCollector.submitBlockModel(
                            poseStack, renderType, barModel,
                            1.0f, 1.0f, 1.0f,
                            renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                    );
                    poseStack.popPose();
                }
            }
        }

        // TL/Sign to pole: when mounted on a horizontal pole, no bar needed
        // from the TL/sign side — the HP already renders a traffic_light_pole_arm toward
        // it, and the body is shifted 9/16 toward the pole (9 + 7 = 16).
        // When mounted on a crossing gate pole, render a short arm to bridge the gap.
        boolean isCardinalRotation = ((int) renderState.rotationDegrees % 90) == 0;
        if (isTrafficLight && !sideBySide && renderState.mountedOnPole
                && !renderState.mountedOnHorizontalPole
                && !renderState.pairedAcrossPole
                && renderState.horizontalBarDirection != null
                && renderState.signalArmBarDirection == null
                && isCardinalRotation) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BlockStateModel armModel = modelManager.getStandaloneModel(TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY);
            if (armModel != null) {
                float barYRot = DIR_ROTATIONS[renderState.horizontalBarDirection.get2DDataValue()];
                poseStack.pushPose();
                // Shift arm with the body so it stays connected
                if (shiftToPole) {
                    Direction poleDir = renderState.horizontalBarDirection;
                    poseStack.translate(poleDir.getStepX() * poleShiftAmount, 0,
                            poleDir.getStepZ() * poleShiftAmount);
                }
                if (barYRot != 0) {
                    poseStack.translate(0.5f, 0.0f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(barYRot));
                    poseStack.translate(-0.5f, 0.0f, -0.5f);
                }
                nodeCollector.submitBlockModel(
                        poseStack, renderType, armModel,
                        1.0f, 1.0f, 1.0f,
                        renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                );
                poseStack.popPose();
            }
        }

        // Traffic light paired across pole: no arm needed, frame is shifted to the pole

        // Traffic light to signal_arm: render bar when another traffic light is beyond the arm
        if (isTrafficLight && !sideBySide && renderState.signalArmBarDirection != null
                && renderState.signalArmConnectsToLight) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BlockStateModel barModel = modelManager.getStandaloneModel(SIGNAL_ARM_BAR_MODEL_KEY);
            if (barModel != null) {
                float barYRot = DIR_ROTATIONS[renderState.signalArmBarDirection.get2DDataValue()];
                poseStack.pushPose();
                if (barYRot != 0) {
                    poseStack.translate(0.5f, 0.0f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(barYRot));
                    poseStack.translate(-0.5f, 0.0f, -0.5f);
                }
                nodeCollector.submitBlockModel(
                        poseStack, renderType, barModel,
                        1.0f, 1.0f, 1.0f,
                        renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                );
                poseStack.popPose();
            }
        }

        // Signal arm / horizontal pole / sign: render ext arm toward each adjacent block
        if ((state.getBlock() instanceof BlockSignalArm || state.getBlock() instanceof BlockHorizontalPole
                || state.getBlock() instanceof BlockSign || (state.getBlock() instanceof BlockStreetSign && !renderState.hanging))
                && !renderState.signalArmTrafficLightDirs.isEmpty()) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            boolean isHorizPole = state.getBlock() instanceof BlockHorizontalPole;
            for (Direction dir : renderState.signalArmTrafficLightDirs) {
                boolean isTowardTL = isHorizPole
                        && (renderState.nonCardinalTLDirs.contains(dir)
                            || renderState.cardinalTLDirs.contains(dir));
                StandaloneModelKey<BlockStateModel> modelKey;
                if (isTowardTL) {
                    modelKey = TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY;
                } else if (isHorizPole && renderState.signDirs.contains(dir)) {
                    modelKey = TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY;
                } else {
                    boolean isSignBlock = state.getBlock() instanceof BlockSign || state.getBlock() instanceof BlockStreetSign;
                    if (isSignBlock) {
                        // Short arm for sign-to-sign, full arm for sign-to-pole
                        modelKey = renderState.signToSignDirs.contains(dir)
                                ? HORIZONTAL_BAR_CONNECT_MODEL_KEY
                                : TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY;
                    } else {
                        modelKey = isHorizPole ? HORIZONTAL_POLE_MODEL_KEY
                                : SIGNAL_ARM_BAR_MODEL_KEY;
                    }
                }
                BlockStateModel poleModel = modelManager.getStandaloneModel(modelKey);
                if (poleModel != null) {
                    float barYRot = DIR_ROTATIONS[dir.get2DDataValue()];
                    poseStack.pushPose();
                    // Shift arm with body when sign is shifted toward pole
                    // Skip shift for sign-to-sign arms so they bridge the gap
                    if (isSign && shiftToPole && !renderState.signToSignDirs.contains(dir)) {
                        Direction poleDir = renderState.horizontalBarDirection;
                        poseStack.translate(poleDir.getStepX() * poleShiftAmount, 0,
                                poleDir.getStepZ() * poleShiftAmount);
                    }
                    if (barYRot != 0) {
                        poseStack.translate(0.5f, 0.0f, 0.5f);
                        poseStack.mulPose(Axis.YP.rotationDegrees(barYRot));
                        poseStack.translate(-0.5f, 0.0f, -0.5f);
                    }
                    nodeCollector.submitBlockModel(
                            poseStack, renderType, poleModel,
                            1.0f, 1.0f, 1.0f,
                            renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                    );
                    poseStack.popPose();
                }
            }
        }

        // HP: extend bar into adjacent street sign blocks only
        if (state.getBlock() instanceof BlockHorizontalPole && !renderState.streetSignDirs.isEmpty()) {
            ModelManager mm = Minecraft.getInstance().getModelManager();
            BlockStateModel hpModel = mm.getStandaloneModel(HORIZONTAL_POLE_MODEL_KEY);
            if (hpModel != null) {
                for (Direction dir : renderState.streetSignDirs) {
                    poseStack.pushPose();
                    poseStack.translate(dir.getStepX(), 0, dir.getStepZ());
                    poseStack.translate(0.5f, 0.0f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.rotationDegrees));
                    poseStack.translate(-0.5f, 0.0f, -0.5f);
                    nodeCollector.submitBlockModel(
                            poseStack, renderType, hpModel,
                            1.0f, 1.0f, 1.0f,
                            renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                    );
                    poseStack.popPose();
                }
            }
        }

        // Crossing gate pole: render connection arms
        if (state.getBlock() instanceof BlockCrossingGatePole && !renderState.cgPoleArmDirs.isEmpty()) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BlockStateModel armModel = modelManager.getStandaloneModel(TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY);
            if (armModel != null) {
                for (Direction dir : renderState.cgPoleArmDirs) {
                    float barYRot = DIR_ROTATIONS[dir.get2DDataValue()];
                    poseStack.pushPose();
                    if (barYRot != 0) {
                        poseStack.translate(0.5f, 0.0f, 0.5f);
                        poseStack.mulPose(Axis.YP.rotationDegrees(barYRot));
                        poseStack.translate(-0.5f, 0.0f, -0.5f);
                    }
                    nodeCollector.submitBlockModel(
                            poseStack, renderType, armModel,
                            1.0f, 1.0f, 1.0f,
                            renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0
                    );
                    poseStack.popPose();
                }
            }
        }

        // --- Sign face rendering: draw sign texture as a quad ---
        // Always render for sign blocks (use blank sign texture if none selected)
        // Street signs use their own block model texture, not the sign face system
        if (isSign && !(state.getBlock() instanceof BlockStreetSign)) {
            Identifier frontTex = renderState.signFrontTexture;
            Identifier backTex = renderState.signBackTexture;

            // Default to blank circle sign if none selected
            if (frontTex == null) {
                Sign blankSign = ModTrafficControl.SIGN_REPO.getSignByID(Sign.DEFAULT_BLANK_SIGN);
                if (blankSign != null) {
                    frontTex = blankSign.getFrontTexture();
                    backTex = blankSign.getBackTexture();
                    ensureTextureLoaded(frontTex);
                    if (backTex != null) ensureTextureLoaded(backTex);
                }
            }

            if (frontTex != null) {
                int light = renderState.lightCoords;
                int overlay = OverlayTexture.NO_OVERLAY;

                // Front face: faces -z (north), visible from the front of the sign
                {
                    RenderType signRenderType = RenderTypes.entityCutout(frontTex);
                    poseStack.pushPose();
                    // Shift sign face with body toward pole
                    if (shiftToPole) {
                        Direction poleDir = renderState.horizontalBarDirection;
                        poseStack.translate(poleDir.getStepX() * poleShiftAmount, 0,
                                poleDir.getStepZ() * poleShiftAmount);
                    }
                    poseStack.translate(0.5f, 0.0f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.rotationDegrees));
                    poseStack.translate(-0.5f, 0.0f, -0.5f);

                    nodeCollector.submitCustomGeometry(poseStack, signRenderType, (pose, consumer) -> {
                        Matrix4f matrix = pose.pose();
                        Vector3f normal = pose.transformNormal(0, 0, -1, new Vector3f());
                        float z = 0.431f;
                        // Reversed winding for -z facing normal
                        consumer.addVertex(matrix, 1, 1, z).setColor(255, 255, 255, 255)
                                .setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(normal.x, normal.y, normal.z);
                        consumer.addVertex(matrix, 1, 0, z).setColor(255, 255, 255, 255)
                                .setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(normal.x, normal.y, normal.z);
                        consumer.addVertex(matrix, 0, 0, z).setColor(255, 255, 255, 255)
                                .setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(normal.x, normal.y, normal.z);
                        consumer.addVertex(matrix, 0, 1, z).setColor(255, 255, 255, 255)
                                .setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(normal.x, normal.y, normal.z);
                    });

                    poseStack.popPose();
                }

                // Back face: faces +z (south), visible from behind the sign
                if (backTex != null) {
                    RenderType backRenderType = RenderTypes.entityCutout(backTex);
                    poseStack.pushPose();
                    // Shift back face with body toward pole
                    if (shiftToPole) {
                        Direction poleDir = renderState.horizontalBarDirection;
                        poseStack.translate(poleDir.getStepX() * poleShiftAmount, 0,
                                poleDir.getStepZ() * poleShiftAmount);
                    }
                    poseStack.translate(0.5f, 0.0f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.rotationDegrees));
                    poseStack.translate(-0.5f, 0.0f, -0.5f);

                    nodeCollector.submitCustomGeometry(poseStack, backRenderType, (pose, consumer) -> {
                        Matrix4f matrix = pose.pose();
                        Vector3f normal = pose.transformNormal(0, 0, 1, new Vector3f());
                        float z = 0.441f;
                        consumer.addVertex(matrix, 0, 1, z).setColor(255, 255, 255, 255)
                                .setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(normal.x, normal.y, normal.z);
                        consumer.addVertex(matrix, 0, 0, z).setColor(255, 255, 255, 255)
                                .setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(normal.x, normal.y, normal.z);
                        consumer.addVertex(matrix, 1, 0, z).setColor(255, 255, 255, 255)
                                .setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(normal.x, normal.y, normal.z);
                        consumer.addVertex(matrix, 1, 1, z).setColor(255, 255, 255, 255)
                                .setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(normal.x, normal.y, normal.z);
                    });

                    poseStack.popPose();
                }
            }
        }

        // (Street sign text is now rendered inline with each plate above)

    }

    private static void addBorderQuad(
            VertexConsumer consumer, Matrix4f m, Vector3f n,
            float x1, float y1, float x2, float y2, float z,
            int r, int g, int b, int a, int overlay, int light, boolean flipWinding
    ) {
        if (flipWinding) {
            // North face winding (CCW from -Z side)
            consumer.addVertex(m, x2, y2, z).setColor(r, g, b, a).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x2, y1, z).setColor(r, g, b, a).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x1, y1, z).setColor(r, g, b, a).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x1, y2, z).setColor(r, g, b, a).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
        } else {
            // South face winding (CCW from +Z side)
            consumer.addVertex(m, x1, y2, z).setColor(r, g, b, a).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x1, y1, z).setColor(r, g, b, a).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x2, y1, z).setColor(r, g, b, a).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x2, y2, z).setColor(r, g, b, a).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
        }
    }

    // Renders a face quad at a fixed Z. flipWinding=false for south (+Z), true for north (-Z).
    private static void addQuad(
            VertexConsumer consumer, Matrix4f m, Vector3f n,
            float x1, float y1, float x2, float y2, float z, boolean flipWinding,
            int r, int g, int b, int a, int overlay, int light
    ) {
        if (flipWinding) {
            consumer.addVertex(m, x2, y2, z).setColor(r, g, b, a).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x2, y1, z).setColor(r, g, b, a).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x1, y1, z).setColor(r, g, b, a).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x1, y2, z).setColor(r, g, b, a).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
        } else {
            consumer.addVertex(m, x1, y2, z).setColor(r, g, b, a).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x1, y1, z).setColor(r, g, b, a).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x2, y1, z).setColor(r, g, b, a).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
            consumer.addVertex(m, x2, y2, z).setColor(r, g, b, a).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(n.x, n.y, n.z);
        }
    }

    private static void ensureTextureLoaded(Identifier location) {
        var texManager = Minecraft.getInstance().getTextureManager();
        if (texManager.getTexture(location) == null) {
            texManager.register(location, new SimpleTexture(location));
        }
    }
}
