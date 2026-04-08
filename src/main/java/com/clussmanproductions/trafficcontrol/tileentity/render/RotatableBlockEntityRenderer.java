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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

        // Sign: detect mounting pole and find adjacent connectable blocks
        if (state.getBlock() instanceof BlockSign) {
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
            // Chained mount: adjacent sign with CG pole behind it
            if (!renderState.mountedOnPole) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                    if (neighbor instanceof BlockSign) {
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
            // Back-to-back: adjacent sign facing opposite direction (rotation diff of 8)
            if (!renderState.mountedOnPole && state.hasProperty(BlockStateProperties.ROTATION_16)) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockState neighborState = level.getBlockState(pos.relative(dir));
                    if (neighborState.getBlock() instanceof BlockSign
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
            // Regular signs (BlockSign) do NOT extend a center pole — they mount
            // on CG poles to the side, which render their own pole visuals.
            // Collect connectable neighbors for arm rendering
            // Includes adjacent signs for sign-to-sign chaining on CG poles
            // Skip back-to-back signs (rotation diff of 8) — no arm needed
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
                if (neighbor instanceof BlockCrossingGatePole
                        || neighbor instanceof BlockCrossingGateBase
                        || neighbor instanceof BlockTrafficLight) {
                    renderState.signalArmTrafficLightDirs.add(dir);
                } else if (neighbor instanceof BlockSign) {
                    BlockState neighborState = level.getBlockState(pos.relative(dir));
                    boolean isBackToBack = neighborState.hasProperty(BlockStateProperties.ROTATION_16)
                            && Math.abs(neighborState.getValue(BlockStateProperties.ROTATION_16) - rotation) == 8;
                    if (!isBackToBack) {
                        renderState.signalArmTrafficLightDirs.add(dir);
                        renderState.signToSignDirs.add(dir);
                    }
                }
            }
            // Add arm toward mounting HP only (not all adjacent HPs)
            if (renderState.mountedOnHorizontalPole && renderState.horizontalBarDirection != null) {
                renderState.signalArmTrafficLightDirs.add(renderState.horizontalBarDirection);
            }
        }

        // Street sign: hanging state and CG pole connection
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
                }
            }

            // 4. Check for pole/TL/sign above and below — extend vertical pole
            Block tlBlockAbove = level.getBlockState(pos.above()).getBlock();
            Block tlBlockBelow = level.getBlockState(pos.below()).getBlock();
            if (tlBlockAbove instanceof BlockCrossingGatePole || tlBlockAbove instanceof BlockCrossingGateBase
                    || tlBlockAbove instanceof BlockTrafficLight || tlBlockAbove instanceof BlockSign
                    || tlBlockAbove instanceof BlockStreetSign) {
                renderState.extendPoleUp = true;
            }
            if (tlBlockBelow instanceof BlockCrossingGatePole || tlBlockBelow instanceof BlockCrossingGateBase
                    || tlBlockBelow instanceof BlockTrafficLight || tlBlockBelow instanceof BlockSign
                    || tlBlockBelow instanceof BlockStreetSign) {
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
        boolean shiftToPole = (isTrafficLight && renderState.mountedOnPole
                && renderState.horizontalBarDirection != null
                && (!renderState.hasAdjacentTrafficLight || renderState.backToBackTLDir != null)
                && renderState.horizontalPoleDirs.isEmpty())
                || (isSign && renderState.mountedOnPole
                && !renderState.mountedOnHorizontalPole
                && renderState.horizontalBarDirection != null);
        float poleShiftAmount = 9.0f / 16.0f; // 9 pixels toward pole

        // --- Render body (rotated) ---
        // Skip body model for regular signs (BlockSign) when mounted on a pole —
        // the model only contains a center pole element; the sign face renders as a quad separately
        boolean isHangingStreetSign = state.getBlock() instanceof BlockStreetSign && renderState.hanging;
        boolean skipBodyModel = state.getBlock() instanceof BlockSign && shiftToPole;
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

        // --- Vertical pole extension for TLs/signs above/below CG poles ---
        // When shifted to a side pole, only render if bridging blocks both above AND below;
        // otherwise the center pole appears offset from the body with nothing to connect to
        boolean shouldRenderPole = (renderState.extendPoleUp || renderState.extendPoleDown);
        if (shiftToPole && isTrafficLight) {
            shouldRenderPole = renderState.extendPoleUp && renderState.extendPoleDown;
        }
        if ((isTrafficLight || isSign) && !isHangingStreetSign && shouldRenderPole) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BlockStateModel poleModel = modelManager.getStandaloneModel(BACK_POLE_MODEL_KEY);
            if (poleModel != null) {
                poseStack.pushPose();
                if (state.getBlock() instanceof BlockStreetSign) {
                    // Street sign: scale pole to only cover Y 0 to 6 (below the sign plate)
                    poseStack.scale(1.0f, 6.0f / 16.0f, 1.0f);
                }
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

    }

    private static void ensureTextureLoaded(Identifier location) {
        var texManager = Minecraft.getInstance().getTextureManager();
        if (texManager.getTexture(location) == null) {
            texManager.register(location, new SimpleTexture(location));
        }
    }
}
