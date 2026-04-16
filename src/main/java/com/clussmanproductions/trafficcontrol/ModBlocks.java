package com.clussmanproductions.trafficcontrol;

import com.clussmanproductions.trafficcontrol.blocks.BlockCone;
import com.clussmanproductions.trafficcontrol.blocks.BlockChannelizer;
import com.clussmanproductions.trafficcontrol.blocks.BlockDrum;
import com.clussmanproductions.trafficcontrol.blocks.BlockConcreteBarrier;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGateBase;
import com.clussmanproductions.trafficcontrol.blocks.BlockCrossingGatePole;
import com.clussmanproductions.trafficcontrol.blocks.BlockHorizontalPole;
import com.clussmanproductions.trafficcontrol.blocks.BlockSign;
import com.clussmanproductions.trafficcontrol.blocks.BlockSignalArm;
import com.clussmanproductions.trafficcontrol.blocks.BlockTrafficLight;
import com.clussmanproductions.trafficcontrol.blocks.BlockTrafficLightControlBox;
import com.clussmanproductions.trafficcontrol.blocks.BlockStreetSign;
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

    // Horizontal traffic light variants
    public static final DeferredBlock<Block> BLACK_TL_TRIPLE_HORIZ = BLOCKS.registerBlock("traffic_light_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> BLACK_TL_QUAD_HORIZ = BLOCKS.registerBlock("traffic_light_4_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> BLACK_TL_FIVE_HORIZ = BLOCKS.registerBlock("traffic_light_5_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_TRIPLE_HORIZ = BLOCKS.registerBlock("yellow_traffic_light_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_QUAD_HORIZ = BLOCKS.registerBlock("yellow_traffic_light_4_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_FIVE_HORIZ = BLOCKS.registerBlock("yellow_traffic_light_5_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_TRIPLE_HORIZ = BLOCKS.registerBlock("orange_traffic_light_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_QUAD_HORIZ = BLOCKS.registerBlock("orange_traffic_light_4_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_FIVE_HORIZ = BLOCKS.registerBlock("orange_traffic_light_5_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> BLACK_TL_DOUBLE_HORIZ = BLOCKS.registerBlock("traffic_light_2_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> YELLOW_TL_DOUBLE_HORIZ = BLOCKS.registerBlock("yellow_traffic_light_2_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);
    public static final DeferredBlock<Block> ORANGE_TL_DOUBLE_HORIZ = BLOCKS.registerBlock("orange_traffic_light_2_horizontal", BlockTrafficLight::new, TRAFFIC_LIGHT_PROPS);

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

    // Placeholder blocks — these use simple Block for now until dedicated classes are ported
    private static final BlockBehaviour.Properties PLACEHOLDER_PROPS = BlockBehaviour.Properties.of()
            .strength(1f)
            .sound(SoundType.METAL)
            .noOcclusion()
            .forceSolidOn();

    public static final DeferredBlock<Block> PEDESTRIAN_BUTTON = BLOCKS.registerBlock("pedestrian_button", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_GATE_GATE = BLOCKS.registerBlock("crossing_gate_gate", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_GATE_LAMPS = BLOCKS.registerBlock("crossing_gate_lamps", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_GATE_CROSSBUCK = BLOCKS.registerBlock("crossing_gate_crossbuck", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> OVERHEAD_POLE = BLOCKS.registerBlock("overhead_pole", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> OVERHEAD = BLOCKS.registerBlock("overhead", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> OVERHEAD_LAMPS = BLOCKS.registerBlock("overhead_lamps", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> OVERHEAD_CROSSBUCK = BLOCKS.registerBlock("overhead_crossbuck", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> SIGN = BLOCKS.registerBlock("road_sign", BlockSign::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> WIG_WAG = BLOCKS.registerBlock("wig_wag", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> SAFETRAN_TYPE_3 = BLOCKS.registerBlock("safetran_type_3", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> SAFETRAN_MECHANICAL = BLOCKS.registerBlock("safetran_mechanical", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> WCH_MECHANICAL_BELL = BLOCKS.registerBlock("wch_mechanical_bell", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> WCH_BELL = BLOCKS.registerBlock("wch_bell", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> VERTICAL_WIG_WAG = BLOCKS.registerBlock("vertical_wig_wag", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> STREET_SIGN = BLOCKS.registerBlock("street_sign", BlockStreetSign::new,
            BlockBehaviour.Properties.of().strength(2.0f).noOcclusion());
    public static final DeferredBlock<Block> ILLUMINATED_STREET_SIGN = BLOCKS.registerBlock("illuminated_street_sign", BlockStreetSign::new,
            BlockBehaviour.Properties.of().strength(2.0f).noOcclusion().lightLevel(state -> 15));
    public static final DeferredBlock<Block> TRAFFIC_SENSOR_LEFT = BLOCKS.registerBlock("traffic_sensor_left", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> TRAFFIC_SENSOR_RIGHT = BLOCKS.registerBlock("traffic_sensor_right", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> TRAFFIC_SENSOR_STRAIGHT = BLOCKS.registerBlock("traffic_sensor_straight", Block::new, PLACEHOLDER_PROPS);

    // Street lights
    public static final DeferredBlock<Block> STREET_LIGHT_SINGLE = BLOCKS.registerBlock("street_light_single", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> STREET_LIGHT_DOUBLE = BLOCKS.registerBlock("street_light_double", Block::new, PLACEHOLDER_PROPS);

    // Traffic light control box (horizontal-facing, orients toward player on placement)
    public static final DeferredBlock<Block> TRAFFIC_LIGHT_CONTROL_BOX = BLOCKS.registerBlock("traffic_light_control_box", BlockTrafficLightControlBox::new, PLACEHOLDER_PROPS);

    // Crossing relay boxes (corner + top variants)
    public static final DeferredBlock<Block> CROSSING_RELAY_NE = BLOCKS.registerBlock("crossing_relay_ne", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_RELAY_NW = BLOCKS.registerBlock("crossing_relay_nw", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_RELAY_SE = BLOCKS.registerBlock("crossing_relay_se", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_RELAY_SW = BLOCKS.registerBlock("crossing_relay_sw", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_RELAY_TOP_NE = BLOCKS.registerBlock("crossing_relay_top_ne", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_RELAY_TOP_NW = BLOCKS.registerBlock("crossing_relay_top_nw", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_RELAY_TOP_SE = BLOCKS.registerBlock("crossing_relay_top_se", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> CROSSING_RELAY_TOP_SW = BLOCKS.registerBlock("crossing_relay_top_sw", Block::new, PLACEHOLDER_PROPS);

    // Shunts
    public static final DeferredBlock<Block> SHUNT_BORDER = BLOCKS.registerBlock("shunt_border", Block::new, PLACEHOLDER_PROPS);
    public static final DeferredBlock<Block> SHUNT_ISLAND = BLOCKS.registerBlock("shunt_island", Block::new, PLACEHOLDER_PROPS);

    // Type 3 Barrier right variant — same behavior/hitbox as TYPE_3_BARRIER
    public static final DeferredBlock<Block> TYPE_3_BARRIER_RIGHT = BLOCKS.registerBlock("type_3_barrier_right", BlockType3Barrier::new, PLACEHOLDER_PROPS);
}