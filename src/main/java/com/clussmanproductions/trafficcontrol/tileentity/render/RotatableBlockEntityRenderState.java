package com.clussmanproductions.trafficcontrol.tileentity.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RotatableBlockEntityRenderState extends BlockEntityRenderState {
    public float rotationDegrees = 0;

    // Sign texture rendering
    public @Nullable Identifier signFrontTexture = null;
    public @Nullable Identifier signBackTexture = null;
    public @Nullable Direction horizontalBarDirection = null;

    // --- Separate, non-overlapping state flags ---

    // True when a pole block is directly adjacent (any pole, any config)
    public boolean mountedOnPole = false;

    // True when the mounting pole is a horizontal pole (vs crossing gate pole)
    public boolean mountedOnHorizontalPole = false;

    // True ONLY when there is a pole adjacent AND an opposite-facing traffic light beyond it
    public boolean pairedAcrossPole = false;

    // True when an adjacent traffic light has a different rotation (no pole between)
    public boolean adjacentTrafficLightConnection = false;

    // True when there is any adjacent traffic light (side-by-side row)
    public boolean hasAdjacentTrafficLight = false;

    // Direction to an adjacent signal_arm
    public @Nullable Direction signalArmBarDirection = null;
    // True when a traffic light is on the other side of the signal_arm
    public boolean signalArmConnectsToLight = false;

    // True when the traffic light is mounted on a crossing_gate_base column
    public boolean onCrossingGateBase = false;

    // Side-by-side flush connection
    public boolean hasSideBySideNeighbor = false;
    // Direction to the pole between two side-by-side lights (null if directly adjacent)
    public @Nullable Direction sideBySidePoleDirection = null;

    // Directions toward adjacent horizontal poles (renders bars toward them)
    public List<Direction> horizontalPoleDirs = new ArrayList<>();

    // Signal arm: directions toward adjacent traffic lights (renders bars toward them)
    public List<Direction> signalArmTrafficLightDirs = new ArrayList<>();

    // Horizontal pole: directions toward adjacent non-cardinal TLs (use short arm model)
    public List<Direction> nonCardinalTLDirs = new ArrayList<>();

    // Horizontal pole: directions toward adjacent cardinal TLs (use short arm model)
    public List<Direction> cardinalTLDirs = new ArrayList<>();

    // Horizontal pole: directions toward adjacent signs (use short arm to avoid poking through)
    public List<Direction> signDirs = new ArrayList<>();

    // Sign: directions toward adjacent signs (use short arm for sign-to-sign)
    public List<Direction> signToSignDirs = new ArrayList<>();

    // Sign: direction toward back-to-back paired sign (render bridge bar)
    public @Nullable Direction backToBackSignDir = null;

    // Traffic light: direction toward back-to-back paired TL (render bridge bar)
    public @Nullable Direction backToBackTLDir = null;

    // Traffic light: render vertical pole extension up/down toward CG pole
    public boolean extendPoleUp = false;
    public boolean extendPoleDown = false;

    // Crossing gate pole: directions with connection arms
    public List<Direction> cgPoleArmDirs = new ArrayList<>();

    // Street sign: hanging from block above
    public boolean hanging = false;

    // Horizontal pole: directions toward adjacent horizontal TL frames (no shift, full bar)
    public List<Direction> horizTLDirs = new ArrayList<>();

    // Horizontal pole: directions toward adjacent non-hanging street signs (extend bar into)
    public List<Direction> streetSignDirs = new ArrayList<>();

    // Street sign: up to 4 stacked plates, each with text and fill color
    public int streetSignCount = 0;
    public String[] streetSignTexts = new String[4];
    public String[] streetSignTexts2 = new String[4];
    public int[] streetSignFillColors = new int[4];
    public float[] streetSignRotations = new float[4]; // degrees per plate
    public int streetSignTextColor = 0xFFFFFFFF;
    public boolean streetSignGlowing = false;
}
