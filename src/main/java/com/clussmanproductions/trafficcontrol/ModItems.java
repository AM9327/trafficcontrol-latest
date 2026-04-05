package com.clussmanproductions.trafficcontrol;

import com.clussmanproductions.trafficcontrol.item.ItemScrewdriver;
import com.clussmanproductions.trafficcontrol.item.ItemTrafficLightFrame;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// Same pattern as ModBlocks but for items.
// Every placeable block needs a corresponding BlockItem — that's the item
// you hold in your inventory that places the block when you right-click.
public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModTrafficControl.MODID);

    // BlockItem for the cone — links the item to the block
    // registerSimpleBlockItem auto-creates a BlockItem tied to the block
    public static final DeferredItem<BlockItem> CONE = ITEMS.registerSimpleBlockItem("cone", ModBlocks.CONE);
    public static final DeferredItem<BlockItem> DRUM = ITEMS.registerSimpleBlockItem("drum", ModBlocks.DRUM);
    public static final DeferredItem<BlockItem> CHANNELIZER = ITEMS.registerSimpleBlockItem("channelizer", ModBlocks.CHANNELIZER);
    public static final DeferredItem<BlockItem> TYPE_3_BARRIER = ITEMS.registerSimpleBlockItem("type_3_barrier", ModBlocks.TYPE_3_BARRIER);

    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_WHITE = ITEMS.registerSimpleBlockItem("concrete_barrier_white", ModBlocks.CONCRETE_BARRIER_WHITE);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_ORANGE = ITEMS.registerSimpleBlockItem("concrete_barrier_orange", ModBlocks.CONCRETE_BARRIER_ORANGE);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_MAGENTA = ITEMS.registerSimpleBlockItem("concrete_barrier_magenta", ModBlocks.CONCRETE_BARRIER_MAGENTA);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_LIGHT_BLUE = ITEMS.registerSimpleBlockItem("concrete_barrier_light_blue", ModBlocks.CONCRETE_BARRIER_LIGHT_BLUE);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_YELLOW = ITEMS.registerSimpleBlockItem("concrete_barrier_yellow", ModBlocks.CONCRETE_BARRIER_YELLOW);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_LIME = ITEMS.registerSimpleBlockItem("concrete_barrier_lime", ModBlocks.CONCRETE_BARRIER_LIME);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_PINK = ITEMS.registerSimpleBlockItem("concrete_barrier_pink", ModBlocks.CONCRETE_BARRIER_PINK);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_GRAY = ITEMS.registerSimpleBlockItem("concrete_barrier_gray", ModBlocks.CONCRETE_BARRIER_GRAY);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_LIGHT_GRAY = ITEMS.registerSimpleBlockItem("concrete_barrier_light_gray", ModBlocks.CONCRETE_BARRIER_LIGHT_GRAY);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_CYAN = ITEMS.registerSimpleBlockItem("concrete_barrier_cyan", ModBlocks.CONCRETE_BARRIER_CYAN);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_PURPLE = ITEMS.registerSimpleBlockItem("concrete_barrier_purple", ModBlocks.CONCRETE_BARRIER_PURPLE);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_BLUE = ITEMS.registerSimpleBlockItem("concrete_barrier_blue", ModBlocks.CONCRETE_BARRIER_BLUE);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_BROWN = ITEMS.registerSimpleBlockItem("concrete_barrier_brown", ModBlocks.CONCRETE_BARRIER_BROWN);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_GREEN = ITEMS.registerSimpleBlockItem("concrete_barrier_green", ModBlocks.CONCRETE_BARRIER_GREEN);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_RED = ITEMS.registerSimpleBlockItem("concrete_barrier_red", ModBlocks.CONCRETE_BARRIER_RED);
    public static final DeferredItem<BlockItem> CONCRETE_BARRIER_BLACK = ITEMS.registerSimpleBlockItem("concrete_barrier_black", ModBlocks.CONCRETE_BARRIER_BLACK);

    public static final DeferredItem<BlockItem> TRAFFIC_RAIL = ITEMS.registerSimpleBlockItem("traffic_rail", ModBlocks.TRAFFIC_RAIL);

    public static final DeferredItem<BlockItem> BLACK_TL_TRIPLE_FRAME = ITEMS.registerItem("traffic_light_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_TRIPLE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> BLACK_TL_SINGLE_FRAME = ITEMS.registerItem("traffic_light_1_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_SINGLE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> BLACK_TL_DOUBLE_FRAME = ITEMS.registerItem("traffic_light_2_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_DOUBLE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> BLACK_TL_QUAD_FRAME = ITEMS.registerItem("traffic_light_4_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_QUAD.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> BLACK_TL_FIVE_FRAME = ITEMS.registerItem("traffic_light_5_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_FIVE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> BLACK_TL_T_FRAME = ITEMS.registerItem("traffic_light_6_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_T.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> BLACK_TL_DH_FRAME = ITEMS.registerItem("traffic_light_doghouse_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_DH.get(), p.stacksTo(1)));

    public static final DeferredItem<BlockItem> YELLOW_TL_TRIPLE_FRAME = ITEMS.registerItem("yellow_traffic_light_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_TRIPLE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> YELLOW_TL_SINGLE_FRAME = ITEMS.registerItem("yellow_traffic_light_1_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_SINGLE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> YELLOW_TL_DOUBLE_FRAME = ITEMS.registerItem("yellow_traffic_light_2_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_DOUBLE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> YELLOW_TL_QUAD_FRAME = ITEMS.registerItem("yellow_traffic_light_4_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_QUAD.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> YELLOW_TL_FIVE_FRAME = ITEMS.registerItem("yellow_traffic_light_5_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_FIVE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> YELLOW_TL_T_FRAME = ITEMS.registerItem("yellow_traffic_light_6_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_T.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> YELLOW_TL_DH_FRAME = ITEMS.registerItem("yellow_traffic_light_doghouse_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_DH.get(), p.stacksTo(1)));

    public static final DeferredItem<BlockItem> ORANGE_TL_TRIPLE_FRAME = ITEMS.registerItem("orange_traffic_light_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_TRIPLE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> ORANGE_TL_SINGLE_FRAME = ITEMS.registerItem("orange_traffic_light_1_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_SINGLE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> ORANGE_TL_DOUBLE_FRAME = ITEMS.registerItem("orange_traffic_light_2_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_DOUBLE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> ORANGE_TL_QUAD_FRAME = ITEMS.registerItem("orange_traffic_light_4_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_QUAD.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> ORANGE_TL_FIVE_FRAME = ITEMS.registerItem("orange_traffic_light_5_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_FIVE.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> ORANGE_TL_T_FRAME = ITEMS.registerItem("orange_traffic_light_6_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_T.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> ORANGE_TL_DH_FRAME = ITEMS.registerItem("orange_traffic_light_doghouse_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_DH.get(), p.stacksTo(1)));

    // Horizontal Traffic Light Frames
    public static final DeferredItem<BlockItem> BLACK_TL_TRIPLE_HORIZ_FRAME = ITEMS.registerItem("traffic_light_horizontal_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_TRIPLE_HORIZ.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> BLACK_TL_QUAD_HORIZ_FRAME = ITEMS.registerItem("traffic_light_4_horizontal_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_QUAD_HORIZ.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> BLACK_TL_FIVE_HORIZ_FRAME = ITEMS.registerItem("traffic_light_5_horizontal_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.BLACK_TL_FIVE_HORIZ.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> YELLOW_TL_TRIPLE_HORIZ_FRAME = ITEMS.registerItem("yellow_traffic_light_horizontal_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_TRIPLE_HORIZ.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> YELLOW_TL_QUAD_HORIZ_FRAME = ITEMS.registerItem("yellow_traffic_light_4_horizontal_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_QUAD_HORIZ.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> YELLOW_TL_FIVE_HORIZ_FRAME = ITEMS.registerItem("yellow_traffic_light_5_horizontal_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.YELLOW_TL_FIVE_HORIZ.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> ORANGE_TL_TRIPLE_HORIZ_FRAME = ITEMS.registerItem("orange_traffic_light_horizontal_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_TRIPLE_HORIZ.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> ORANGE_TL_QUAD_HORIZ_FRAME = ITEMS.registerItem("orange_traffic_light_4_horizontal_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_QUAD_HORIZ.get(), p.stacksTo(1)));
    public static final DeferredItem<BlockItem> ORANGE_TL_FIVE_HORIZ_FRAME = ITEMS.registerItem("orange_traffic_light_5_horizontal_frame",
            p -> new ItemTrafficLightFrame(ModBlocks.ORANGE_TL_FIVE_HORIZ.get(), p.stacksTo(1)));

    public static final DeferredItem<BlockItem> CROSSING_GATE_BASE = ITEMS.registerSimpleBlockItem("crossing_gate_base", ModBlocks.CROSSING_GATE_BASE);
    public static final DeferredItem<BlockItem> STAND = ITEMS.registerSimpleBlockItem("stand", ModBlocks.STAND);
    public static final DeferredItem<BlockItem> CROSSING_GATE_POLE = ITEMS.registerSimpleBlockItem("crossing_gate_pole", ModBlocks.CROSSING_GATE_POLE);
    public static final DeferredItem<BlockItem> HORIZONTAL_POLE = ITEMS.registerSimpleBlockItem("horizontal_pole", ModBlocks.HORIZONTAL_POLE);

    // Crossing gate components
    public static final DeferredItem<BlockItem> PEDESTRIAN_BUTTON = ITEMS.registerSimpleBlockItem("pedestrian_button", ModBlocks.PEDESTRIAN_BUTTON);
    public static final DeferredItem<BlockItem> CROSSING_GATE_GATE = ITEMS.registerSimpleBlockItem("crossing_gate_gate", ModBlocks.CROSSING_GATE_GATE);
    public static final DeferredItem<BlockItem> CROSSING_GATE_LAMPS = ITEMS.registerSimpleBlockItem("crossing_gate_lamps", ModBlocks.CROSSING_GATE_LAMPS);
    public static final DeferredItem<BlockItem> CROSSING_GATE_CROSSBUCK = ITEMS.registerSimpleBlockItem("crossing_gate_crossbuck", ModBlocks.CROSSING_GATE_CROSSBUCK);

    // Overhead components
    public static final DeferredItem<BlockItem> OVERHEAD_POLE = ITEMS.registerSimpleBlockItem("overhead_pole", ModBlocks.OVERHEAD_POLE);
    public static final DeferredItem<BlockItem> OVERHEAD = ITEMS.registerSimpleBlockItem("overhead", ModBlocks.OVERHEAD);
    public static final DeferredItem<BlockItem> OVERHEAD_LAMPS = ITEMS.registerSimpleBlockItem("overhead_lamps", ModBlocks.OVERHEAD_LAMPS);
    public static final DeferredItem<BlockItem> OVERHEAD_CROSSBUCK = ITEMS.registerSimpleBlockItem("overhead_crossbuck", ModBlocks.OVERHEAD_CROSSBUCK);

    // Signs and signals
    public static final DeferredItem<BlockItem> SIGN = ITEMS.registerSimpleBlockItem("sign", ModBlocks.SIGN);
    public static final DeferredItem<BlockItem> WIG_WAG = ITEMS.registerSimpleBlockItem("wig_wag", ModBlocks.WIG_WAG);
    public static final DeferredItem<BlockItem> SAFETRAN_TYPE_3 = ITEMS.registerSimpleBlockItem("safetran_type_3", ModBlocks.SAFETRAN_TYPE_3);
    public static final DeferredItem<BlockItem> SAFETRAN_MECHANICAL = ITEMS.registerSimpleBlockItem("safetran_mechanical", ModBlocks.SAFETRAN_MECHANICAL);
    public static final DeferredItem<BlockItem> WCH_MECHANICAL_BELL = ITEMS.registerSimpleBlockItem("wch_mechanical_bell", ModBlocks.WCH_MECHANICAL_BELL);
    public static final DeferredItem<BlockItem> WCH_BELL = ITEMS.registerSimpleBlockItem("wch_bell", ModBlocks.WCH_BELL);
    public static final DeferredItem<BlockItem> VERTICAL_WIG_WAG = ITEMS.registerSimpleBlockItem("vertical_wig_wag", ModBlocks.VERTICAL_WIG_WAG);
    public static final DeferredItem<BlockItem> STREET_SIGN = ITEMS.registerSimpleBlockItem("street_sign", ModBlocks.STREET_SIGN);

    // Traffic sensors
    public static final DeferredItem<BlockItem> TRAFFIC_SENSOR_LEFT = ITEMS.registerSimpleBlockItem("traffic_sensor_left", ModBlocks.TRAFFIC_SENSOR_LEFT);
    public static final DeferredItem<BlockItem> TRAFFIC_SENSOR_RIGHT = ITEMS.registerSimpleBlockItem("traffic_sensor_right", ModBlocks.TRAFFIC_SENSOR_RIGHT);
    public static final DeferredItem<BlockItem> TRAFFIC_SENSOR_STRAIGHT = ITEMS.registerSimpleBlockItem("traffic_sensor_straight", ModBlocks.TRAFFIC_SENSOR_STRAIGHT);

    // Traffic Light Bulbs — basic colors
    public static final DeferredItem<Item> BULB_RED = ITEMS.registerSimpleItem("traffic_light_bulb_red", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_YELLOW = ITEMS.registerSimpleItem("traffic_light_bulb_yellow", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_GREEN = ITEMS.registerSimpleItem("traffic_light_bulb_green", new Item.Properties().stacksTo(16));

    // Traffic Light Bulbs — left arrows
    public static final DeferredItem<Item> BULB_RED_ARROW_LEFT = ITEMS.registerSimpleItem("traffic_light_bulb_red_arrow_left", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_YELLOW_ARROW_LEFT = ITEMS.registerSimpleItem("traffic_light_bulb_yellow_arrow_left", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_GREEN_ARROW_LEFT = ITEMS.registerSimpleItem("traffic_light_bulb_green_arrow_left", new Item.Properties().stacksTo(16));

    // Traffic Light Bulbs — right arrows
    public static final DeferredItem<Item> BULB_RED_ARROW_RIGHT = ITEMS.registerSimpleItem("traffic_light_bulb_red_arrow_right", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_YELLOW_ARROW_RIGHT = ITEMS.registerSimpleItem("traffic_light_bulb_yellow_arrow_right", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_GREEN_ARROW_RIGHT = ITEMS.registerSimpleItem("traffic_light_bulb_green_arrow_right", new Item.Properties().stacksTo(16));

    // Traffic Light Bulbs — straight arrows
    public static final DeferredItem<Item> BULB_STRAIGHT_RED = ITEMS.registerSimpleItem("traffic_light_bulb_straight_red", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_STRAIGHT_YELLOW = ITEMS.registerSimpleItem("traffic_light_bulb_straight_yellow", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_STRAIGHT_GREEN = ITEMS.registerSimpleItem("traffic_light_bulb_straight_green", new Item.Properties().stacksTo(16));

    // Traffic Light Bulbs — pedestrian signals
    public static final DeferredItem<Item> BULB_CROSS = ITEMS.registerSimpleItem("traffic_light_bulb_cross", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_DONT_CROSS = ITEMS.registerSimpleItem("traffic_light_bulb_dont_cross", new Item.Properties().stacksTo(16));

    // Traffic Light Bulbs — no turn
    public static final DeferredItem<Item> BULB_NO_RIGHT_TURN = ITEMS.registerSimpleItem("traffic_light_bulb_no_right_turn", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_NO_LEFT_TURN = ITEMS.registerSimpleItem("traffic_light_bulb_no_left_turn", new Item.Properties().stacksTo(16));

    // Traffic Light Bulbs — tunnel
    public static final DeferredItem<Item> BULB_TUNNEL_GREEN = ITEMS.registerSimpleItem("traffic_light_bulb_tunnelgreen", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> BULB_TUNNEL_RED = ITEMS.registerSimpleItem("traffic_light_bulb_tunnelred", new Item.Properties().stacksTo(16));

    // Tools
    public static final DeferredItem<Item> SCREWDRIVER = ITEMS.registerItem("screwdriver",
            props -> new ItemScrewdriver(props.stacksTo(1).durability(128)));
}