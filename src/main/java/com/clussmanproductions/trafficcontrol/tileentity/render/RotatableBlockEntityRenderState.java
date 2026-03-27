package com.clussmanproductions.trafficcontrol.tileentity.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class RotatableBlockEntityRenderState extends BlockEntityRenderState {
    public float rotationDegrees = 0;
    public @Nullable Direction horizontalBarDirection = null;
    public boolean connectsToTrafficLight = false;
    // Direction to an adjacent signal_arm (traffic light renders the bar toward it)
    public @Nullable Direction signalArmBarDirection = null;
    // True when the traffic light is mounted on a crossing_gate_base column
    public boolean onCrossingGateBase = false;

    // Side-by-side flush connection
    public boolean hasSideBySideNeighbor = false;
    // Direction to the pole between two side-by-side lights (null if directly adjacent)
    public @Nullable Direction sideBySidePoleDirection = null;
}
