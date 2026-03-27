package com.clussmanproductions.trafficcontrol;

import com.clussmanproductions.trafficcontrol.blocks.BlockCone;
import com.clussmanproductions.trafficcontrol.blocks.BlockChannelizer;
import com.clussmanproductions.trafficcontrol.blocks.BlockDrum;
import com.clussmanproductions.trafficcontrol.blocks.BlockConcreteBarrier;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGateBase;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGatePole;
import com.clussmanproductions.trafficcontrol.blocks.BlockHorizontalPole;
import com.clussmanproductions.trafficcontrol.blocks.BlockSignalArm;
import com.clussmanproductions.trafficcontrol.blocks.BlockTrafficLight;
import com.clussmanproductions.trafficcontrol.blocks.BlockTrafficRail;
import com.clussmanproductions.trafficcontrol.blocks.BlockType3Barrier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

// DeferredRegister is NeoForge's way of registering things (blocks, items, etc.)
// Think of it like a registry dictionary: {"cone": BlockCone, "drum": BlockDrum, ...}
// In 1.12.2, registration was done with @ObjectHolder + event handlers.
// Now it's declarative — you define what to register and NeoForge handles the timing.
public class ModBlocks {

    // Create a registry for blocks, tied to our mod ID
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ModTrafficControl.MODID);

    // Register the cone block
    // registerBlock() auto-sets the block ID on the properties (required in 1.21)
    // DeferredBlock is like a lazy reference — the actual Block object is created later
    // when NeoForge is ready. You access it with .get()
    public static final DeferredBlock<Block> CONE = BLOCKS.registerBlock("cone",
            BlockCone::new,
            BlockBehaviour.Properties.of()
                    .strength(1f)               // hardness (how long to mine)
                    .sound(SoundType.STONE)      // stone breaking/walking sounds
                    .noOcclusion()               // not a full opaque block (replaces isOpaqueCube)
                    .forceSolidOn()              // still solid for placement purposes
    );

    public static final DeferredBlock<Block> DRUM = BLOCKS.registerBlock("drum",
            BlockDrum::new,
            BlockBehaviour.Properties.of()
                    .strength(1f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .forceSolidOn()
    );

    public static final DeferredBlock<Block> CHANNELIZER = BLOCKS.registerBlock("channelizer",
            BlockChannelizer::new,
            BlockBehaviour.Properties.of()
                    .strength(1f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .forceSolidOn()
    );
    public static final DeferredBlock<Block> TYPE_3_BARRIER = BLOCKS.registerBlock("type_3_barrier",
            BlockType3Barrier::new,
            BlockBehaviour.Properties.of()
                    .strength(1f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .forceSolidOn()
    );

    private static final BlockBehaviour.Properties CONCRETE_BARRIER_PROPS = BlockBehaviour.Properties.of()
            .strength(2f)
            .sound(SoundType.STONE)
            .noOcclusion()
            .forceSolidOn();

    public static final DeferredBlock<Block> CONCRETE_BARRIER_WHITE = BLOCKS.registerBlock("concrete_barrier_white", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_ORANGE = BLOCKS.registerBlock("concrete_barrier_orange", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_MAGENTA = BLOCKS.registerBlock("concrete_barrier_magenta", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_LIGHT_BLUE = BLOCKS.registerBlock("concrete_barrier_light_blue", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_YELLOW = BLOCKS.registerBlock("concrete_barrier_yellow", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_LIME = BLOCKS.registerBlock("concrete_barrier_lime", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_PINK = BLOCKS.registerBlock("concrete_barrier_pink", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_GRAY = BLOCKS.registerBlock("concrete_barrier_gray", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_LIGHT_GRAY = BLOCKS.registerBlock("concrete_barrier_light_gray", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_CYAN = BLOCKS.registerBlock("concrete_barrier_cyan", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_PURPLE = BLOCKS.registerBlock("concrete_barrier_purple", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_BLUE = BLOCKS.registerBlock("concrete_barrier_blue", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_BROWN = BLOCKS.registerBlock("concrete_barrier_brown", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_GREEN = BLOCKS.registerBlock("concrete_barrier_green", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_RED = BLOCKS.registerBlock("concrete_barrier_red", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);
    public static final DeferredBlock<Block> CONCRETE_BARRIER_BLACK = BLOCKS.registerBlock("concrete_barrier_black", BlockConcreteBarrier::new, CONCRETE_BARRIER_PROPS);

    public static final DeferredBlock<Block> TRAFFIC_RAIL = BLOCKS.registerBlock("traffic_rail",
            BlockTrafficRail::new,
            BlockBehaviour.Properties.of()
                    .strength(2f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .forceSolidOn()
    );

    private static final BlockBehaviour.Properties TRAFFIC_LIGHT_PROPS = BlockBehaviour.Properties.of()
            .strength(1f)
            .sound(SoundType.METAL)
            .noOcclusion()
            .forceSolidOn();

    public static final DeferredBlock<Block> BLACK_TL_TRIPLE = BLOCKS.registerBlock("traffic_light", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> BLACK_TL_SINGLE = BLOCKS.registerBlock("traffic_light_1", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> BLACK_TL_DOUBLE = BLOCKS.registerBlock("traffic_light_2", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> BLACK_TL_QUAD = BLOCKS.registerBlock("traffic_light_4", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> BLACK_TL_FIVE = BLOCKS.registerBlock("traffic_light_5", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> BLACK_TL_T = BLOCKS.registerBlock("traffic_light_6", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> BLACK_TL_DH = BLOCKS.registerBlock("traffic_light_doghouse", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);

    public static final DeferredBlock<Block> YELLOW_TL_TRIPLE = BLOCKS.registerBlock("yellow_traffic_light", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_SINGLE = BLOCKS.registerBlock("yellow_traffic_light_1", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_DOUBLE = BLOCKS.registerBlock("yellow_traffic_light_2", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_QUAD = BLOCKS.registerBlock("yellow_traffic_light_4", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_FIVE = BLOCKS.registerBlock("yellow_traffic_light_5", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_T = BLOCKS.registerBlock("yellow_traffic_light_6", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_DH = BLOCKS.registerBlock("yellow_traffic_light_doghouse", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);

    public static final DeferredBlock<Block> ORANGE_TL_TRIPLE = BLOCKS.registerBlock("orange_traffic_light", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_SINGLE = BLOCKS.registerBlock("orange_traffic_light_1", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_DOUBLE = BLOCKS.registerBlock("orange_traffic_light_2", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_QUAD = BLOCKS.registerBlock("orange_traffic_light_4", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_FIVE = BLOCKS.registerBlock("orange_traffic_light_5", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_T = BLOCKS.registerBlock("orange_traffic_light_6", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_DH = BLOCKS.registerBlock("orange_traffic_light_doghouse", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);

    private static final BlockBehaviour.Properties POLE_PROPS = BlockBehaviour.Properties.of()
            .strength(2f)
            .sound(SoundType.METAL)
            .noOcclusion()
            .forceSolidOn();

    public static final DeferredBlock<Block> CROSSING_GATE_BASE = BLOCKS.registerBlock("crossing_gate_base", BlockCrossingGateBase::new, POLE_PROPS);
    public static final DeferredBlock<Block> STAND = BLOCKS.registerBlock("stand", BlockCrossingGateBase::new, POLE_PROPS);
    public static final DeferredBlock<Block> CROSSING_GATE_POLE = BLOCKS.registerBlock("crossing_gate_pole", BlockCrossingGatePole::new, POLE_PROPS);
    public static final DeferredBlock<Block> HORIZONTAL_POLE = BLOCKS.registerBlock("horizontal_pole", BlockHorizontalPole::new, POLE_PROPS);
    public static final DeferredBlock<Block> SIGNAL_ARM = BLOCKS.registerBlock("signal_arm", BlockSignalArm::new, POLE_PROPS);
}