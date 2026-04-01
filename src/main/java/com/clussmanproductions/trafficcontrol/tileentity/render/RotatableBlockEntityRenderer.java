package com.clussmanproductions.trafficcontrol.tileentity.render;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGateBase;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGatePole;
import com.clussmanproductions.trafficcontrol.blocks.BlockHorizontalPole;
import com.clussmanproductions.trafficcontrol.blocks.BlockSign;
import com.clussmanproductions.trafficcontrol.blocks.BlockSignalArm;
import com.clussmanproductions.trafficcontrol.blocks.BlockTrafficLight;
import com.clussmanproductions.trafficcontrol.tileentity.RotatableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
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
        renderState.cgPoleArmDirs.clear();

        Level level = blockEntity.getLevel();
        if (level == null) return;

        BlockPos pos = blockEntity.getBlockPos();
        int rotation = state.hasProperty(BlockStateProperties.ROTATION_16)
                ? state.getValue(BlockStateProperties.ROTATION_16) : 0;

        // Sign: find adjacent connectable blocks to render arms toward
        if (state.getBlock() instanceof BlockSign) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockHorizontalPole
                        || neighbor instanceof BlockCrossingGatePole
                        || neighbor instanceof BlockCrossingGateBase
                        || neighbor instanceof BlockTrafficLight) {
                    renderState.signalArmTrafficLightDirs.add(dir);
                }
            }
        }

        // Crossing gate pole: read connection state for arm rendering
        if (state.getBlock() instanceof BlockCrossingGatePole) {
            if (state.getValue(BlockCrossingGatePole.NORTH)) renderState.cgPoleArmDirs.add(Direction.NORTH);
            if (state.getValue(BlockCrossingGatePole.SOUTH)) renderState.cgPoleArmDirs.add(Direction.SOUTH);
            if (state.getValue(BlockCrossingGatePole.EAST)) renderState.cgPoleArmDirs.add(Direction.EAST);
            if (state.getValue(BlockCrossingGatePole.WEST)) renderState.cgPoleArmDirs.add(Direction.WEST);
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
                    }
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
                        || neighbor instanceof BlockSign) {
                    renderState.onCrossingGateBase = true;
                    // Render horizontal bar toward crossing gate pole/sign to bridge the gap
                    if (!dir.equals(poleDir)) {
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
                    renderState.horizontalPoleDirs.add(dir);
                    if (renderState.horizontalBarDirection == null) {
                        renderState.horizontalBarDirection = dir;
                    }
                    BlockState neighborState = level.getBlockState(pos.relative(dir));
                    int neighborRot = neighborState.hasProperty(BlockStateProperties.ROTATION_16)
                            ? neighborState.getValue(BlockStateProperties.ROTATION_16) : 0;
                    if (neighborRot != rotation) {
                        renderState.adjacentTrafficLightConnection = true;
                    }
                }
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
        boolean isSign = state.getBlock() instanceof BlockSign;
        RenderType renderType = isSign
                ? RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS)
                : RenderTypes.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS);

        boolean isTrafficLight = state.getBlock() instanceof BlockTrafficLight;
        boolean sideBySide = isTrafficLight && renderState.hasSideBySideNeighbor;

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


        // --- Pole-mounted shift ---
        // Shift toward pole only when there's no adjacent traffic light (side-by-side row)
        // and no other pole connections (e.g. CG pole on the opposite side).
        // When TLs are adjacent or between two poles, keep centered.
        boolean shiftToPole = isTrafficLight && renderState.mountedOnPole
                && renderState.horizontalBarDirection != null
                && !renderState.hasAdjacentTrafficLight
                && renderState.horizontalPoleDirs.isEmpty();
        float poleShiftAmount = 9.0f / 16.0f; // 9 pixels toward pole

        // --- Render body (rotated) ---
        {
            poseStack.pushPose();

            // World-space shift toward pole (applied after rotation in transform order)
            if (shiftToPole) {
                Direction poleDir = renderState.horizontalBarDirection;
                poseStack.translate(poleDir.getStepX() * poleShiftAmount, 0,
                        poleDir.getStepZ() * poleShiftAmount);
            }

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

        // --- Bridge: render connect model at the pole block position to fill the gap ---
        if (sideBySide && renderState.sideBySidePoleDirection != null && connectModel != null) {
            Direction poleDir = renderState.sideBySidePoleDirection;
            poseStack.pushPose();
            // Translate to the pole block in world space
            poseStack.translate(poleDir.getStepX(), 0, poleDir.getStepZ());
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

        // Horizontal bar — for non-TLs, use horizontalBarDirection
        if (renderState.horizontalBarDirection != null && !sideBySide && !isTrafficLight) {
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

        // Traffic light to pole: when mounted on a horizontal pole, no bar needed
        // from the TL side — the HP already renders a traffic_light_pole_arm toward
        // the TL, and the TL body is shifted 9/16 toward the pole (9 + 7 = 16).
        // When mounted on a crossing gate pole, render a short arm from the TL side
        // to bridge the gap between the shifted TL body and the CG pole's ext arm.
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
        if ((state.getBlock() instanceof BlockSignalArm || state.getBlock() instanceof BlockHorizontalPole || state.getBlock() instanceof BlockSign)
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
                    boolean isSignBlock = state.getBlock() instanceof BlockSign;
                    modelKey = isHorizPole ? HORIZONTAL_POLE_MODEL_KEY
                            : isSignBlock ? TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY
                            : SIGNAL_ARM_BAR_MODEL_KEY;
                }
                BlockStateModel poleModel = modelManager.getStandaloneModel(modelKey);
                if (poleModel != null) {
                    float barYRot = DIR_ROTATIONS[dir.get2DDataValue()];
                    poseStack.pushPose();
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

    }
}
