package com.clussmanproductions.trafficcontrol.event;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGatePole;
import com.clussmanproductions.trafficcontrol.blocks.BlockHorizontalPole;
import com.clussmanproductions.trafficcontrol.blocks.BlockTrafficLight;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jspecify.annotations.Nullable;

/**
 * Renders a translucent "ghost" preview of a traffic light at the placement
 * position whenever the player is holding a traffic light item.
 */
@EventBusSubscriber(modid = ModTrafficControl.MODID, value = Dist.CLIENT)
public class GhostBlockRenderer {

    private static final float GHOST_ALPHA = 0.4F;
    private static final double MAX_GHOST_DISTANCE = 12.0;
    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    @SubscribeEvent
    public static void onRenderAfterTranslucent(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Check if the player is holding a traffic light in either hand
        BlockItem blockItem = getHeldTrafficLight(mc.player.getMainHandItem());
        if (blockItem == null) blockItem = getHeldTrafficLight(mc.player.getOffhandItem());
        if (blockItem == null) return;

        // Need a block hit result (even a miss gives us a position)
        if (!(mc.hitResult instanceof BlockHitResult blockHit)) return;

        // Determine placement position
        BlockPos placePos;
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            placePos = blockHit.getBlockPos().relative(blockHit.getDirection());
        } else {
            // Miss (looking at air) — use the raycast endpoint
            placePos = BlockPos.containing(blockHit.getLocation());
        }

        if (!mc.level.getBlockState(placePos).canBeReplaced()) return;

        // Limit ghost render distance
        if (mc.player.getEyePosition().distanceTo(Vec3.atCenterOf(placePos)) > MAX_GHOST_DISTANCE) return;

        // Calculate rotation based on player facing (same logic as BlockTrafficLight.getStateForPlacement)
        int rotation = RotationSegment.convertToSegment(mc.player.getYRot() + 180.0F);
        float rotationDegrees = RotationSegment.convertToDegrees(rotation);

        // Build the ghost block state — show horizontal bar if a pole is actually adjacent
        boolean hasBar = hasAdjacentPole(mc.level, placePos);
        BlockState ghostState = blockItem.getBlock().defaultBlockState()
                .setValue(BlockTrafficLight.ROTATION, rotation)
                .setValue(BlockTrafficLight.HAS_HORIZONTAL_BAR, hasBar);

        // Get rendering resources
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        BlockStateModel model = dispatcher.getBlockModel(ghostState);

        // Set up pose stack — translate from camera space to world position
        PoseStack poseStack = event.getPoseStack();
        Vec3 cam = event.getLevelRenderState().cameraRenderState.pos;

        poseStack.pushPose();
        poseStack.translate(
                placePos.getX() - cam.x,
                placePos.getY() - cam.y,
                placePos.getZ() - cam.z
        );

        // Apply rotation around block center (same as RotatableBlockEntityRenderer)
        poseStack.translate(0.5f, 0.0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotationDegrees));
        poseStack.translate(-0.5f, 0.0f, -0.5f);

        // Render the model with transparency
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(
                RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));

        renderGhostModel(poseStack.last(), consumer, model, GHOST_ALPHA);

        bufferSource.endLastBatch();
        poseStack.popPose();
    }

    /**
     * Renders a BlockStateModel with a custom alpha value for ghost/preview rendering.
     * This mirrors ModelBlockRenderer.renderModel but allows controlling transparency.
     */
    private static void renderGhostModel(PoseStack.Pose pose, VertexConsumer consumer,
                                          BlockStateModel model, float alpha) {
        for (BlockModelPart part : model.collectParts(
                EmptyBlockAndTintGetter.INSTANCE, BlockPos.ZERO,
                Blocks.AIR.defaultBlockState(), RandomSource.create(42L))) {
            for (Direction direction : ALL_DIRECTIONS) {
                renderQuadList(pose, consumer, part.getQuads(direction), alpha);
            }
            renderQuadList(pose, consumer, part.getQuads(null), alpha);
        }
    }

    private static void renderQuadList(PoseStack.Pose pose, VertexConsumer consumer,
                                        java.util.List<BakedQuad> quads, float alpha) {
        for (BakedQuad quad : quads) {
            consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, alpha,
                    15728880, OverlayTexture.NO_OVERLAY);
        }
    }

    private static @Nullable BlockItem getHeldTrafficLight(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof BlockTrafficLight) {
            return blockItem;
        }
        return null;
    }

    private static boolean isPole(BlockState state) {
        Block block = state.getBlock();
        return block instanceof BlockHorizontalPole || block instanceof BlockCrossingGatePole;
    }

    private static boolean hasAdjacentPole(Level level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (isPole(level.getBlockState(pos.relative(dir)))) return true;
        }
        return false;
    }
}