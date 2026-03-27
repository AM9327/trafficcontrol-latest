package com.clussmanproductions.trafficcontrol;

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

    public static final DeferredItem<BlockItem> CROSSING_GATE_BASE = ITEMS.registerSimpleBlockItem("crossing_gate_base", ModBlocks.CROSSING_GATE_BASE);
    public static final DeferredItem<BlockItem> STAND = ITEMS.registerSimpleBlockItem("stand", ModBlocks.STAND);
    public static final DeferredItem<BlockItem> CROSSING_GATE_POLE = ITEMS.registerSimpleBlockItem("crossing_gate_pole", ModBlocks.CROSSING_GATE_POLE);
    public static final DeferredItem<BlockItem> HORIZONTAL_POLE = ITEMS.registerSimpleBlockItem("horizontal_pole", ModBlocks.HORIZONTAL_POLE);
    public static final DeferredItem<BlockItem> SIGNAL_ARM = ITEMS.registerSimpleBlockItem("signal_arm", ModBlocks.SIGNAL_ARM);
}