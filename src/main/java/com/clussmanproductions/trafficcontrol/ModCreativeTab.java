package com.clussmanproductions.trafficcontrol;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// Creative tabs are the tabs in creative mode inventory (like "Building Blocks", "Redstone", etc.)
// The old mod used CreativeTab class directly; NeoForge 1.21 uses the registry system.
public class ModCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModTrafficControl.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TRAFFIC_CONTROL_TAB =
            CREATIVE_TABS.register("trafficcontrol", () -> CreativeModeTab.builder()
                    // The tab icon — shows a cone in the tab
                    .icon(() -> new ItemStack(ModItems.CONE.get()))
                    // The tab title — pulled from the lang file
                    .title(Component.translatable("itemGroup.trafficcontrol"))
                    // Add all our items to this tab
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CONE.get());
                        output.accept(ModItems.DRUM.get());
                        output.accept(ModItems.CHANNELIZER.get());
                        output.accept(ModItems.TYPE_3_BARRIER.get());
                        output.accept(ModItems.TRAFFIC_RAIL.get());
                        output.accept(ModItems.CONCRETE_BARRIER_WHITE.get());
                        output.accept(ModItems.CONCRETE_BARRIER_ORANGE.get());
                        output.accept(ModItems.CONCRETE_BARRIER_MAGENTA.get());
                        output.accept(ModItems.CONCRETE_BARRIER_LIGHT_BLUE.get());
                        output.accept(ModItems.CONCRETE_BARRIER_YELLOW.get());
                        output.accept(ModItems.CONCRETE_BARRIER_LIME.get());
                        output.accept(ModItems.CONCRETE_BARRIER_PINK.get());
                        output.accept(ModItems.CONCRETE_BARRIER_GRAY.get());
                        output.accept(ModItems.CONCRETE_BARRIER_LIGHT_GRAY.get());
                        output.accept(ModItems.CONCRETE_BARRIER_CYAN.get());
                        output.accept(ModItems.CONCRETE_BARRIER_PURPLE.get());
                        output.accept(ModItems.CONCRETE_BARRIER_BLUE.get());
                        output.accept(ModItems.CONCRETE_BARRIER_BROWN.get());
                        output.accept(ModItems.CONCRETE_BARRIER_GREEN.get());
                        output.accept(ModItems.CONCRETE_BARRIER_RED.get());
                        output.accept(ModItems.CONCRETE_BARRIER_BLACK.get());
                        // Black Traffic Light Frames (vertical + horizontal)
                        output.accept(ModItems.BLACK_TL_SINGLE_FRAME.get());
                        output.accept(ModItems.BLACK_TL_DOUBLE_FRAME.get());
                        output.accept(ModItems.BLACK_TL_TRIPLE_FRAME.get());
                        output.accept(ModItems.BLACK_TL_QUAD_FRAME.get());
                        output.accept(ModItems.BLACK_TL_FIVE_FRAME.get());
                        output.accept(ModItems.BLACK_TL_T_FRAME.get());
                        output.accept(ModItems.BLACK_TL_DH_FRAME.get());
                        output.accept(ModItems.BLACK_TL_DOUBLE_HORIZ_FRAME.get());
                        output.accept(ModItems.BLACK_TL_TRIPLE_HORIZ_FRAME.get());
                        output.accept(ModItems.BLACK_TL_QUAD_HORIZ_FRAME.get());
                        output.accept(ModItems.BLACK_TL_FIVE_HORIZ_FRAME.get());
                        // Yellow Traffic Light Frames (vertical + horizontal)
                        output.accept(ModItems.YELLOW_TL_SINGLE_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_DOUBLE_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_TRIPLE_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_QUAD_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_FIVE_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_T_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_DH_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_DOUBLE_HORIZ_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_TRIPLE_HORIZ_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_QUAD_HORIZ_FRAME.get());
                        output.accept(ModItems.YELLOW_TL_FIVE_HORIZ_FRAME.get());
                        // Orange Traffic Light Frames (vertical + horizontal)
                        output.accept(ModItems.ORANGE_TL_SINGLE_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_DOUBLE_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_TRIPLE_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_QUAD_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_FIVE_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_T_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_DH_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_DOUBLE_HORIZ_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_TRIPLE_HORIZ_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_QUAD_HORIZ_FRAME.get());
                        output.accept(ModItems.ORANGE_TL_FIVE_HORIZ_FRAME.get());
                        // Tools
                        output.accept(ModItems.SCREWDRIVER.get());
                        output.accept(ModItems.CROSSING_GATE_BASE.get());
                        output.accept(ModItems.STAND.get());
                        output.accept(ModItems.CROSSING_GATE_POLE.get());
                        output.accept(ModItems.HORIZONTAL_POLE.get());
                        // After poles
                        output.accept(ModItems.PEDESTRIAN_BUTTON.get());
                        output.accept(ModItems.CROSSING_GATE_GATE.get());
                        output.accept(ModItems.CROSSING_GATE_LAMPS.get());
                        output.accept(ModItems.OVERHEAD_POLE.get());
                        output.accept(ModItems.OVERHEAD.get());
                        output.accept(ModItems.OVERHEAD_LAMPS.get());
                        output.accept(ModItems.OVERHEAD_CROSSBUCK.get());
                        output.accept(ModItems.SIGN.get());
                        output.accept(ModItems.WIG_WAG.get());
                        output.accept(ModItems.SAFETRAN_TYPE_3.get());
                        output.accept(ModItems.SAFETRAN_MECHANICAL.get());
                        output.accept(ModItems.WCH_MECHANICAL_BELL.get());
                        output.accept(ModItems.WCH_BELL.get());
                        output.accept(ModItems.VERTICAL_WIG_WAG.get());
                        // Traffic Light Bulbs
                        output.accept(ModItems.BULB_RED.get());
                        output.accept(ModItems.BULB_YELLOW.get());
                        output.accept(ModItems.BULB_GREEN.get());
                        output.accept(ModItems.BULB_RED_ARROW_LEFT.get());
                        output.accept(ModItems.BULB_YELLOW_ARROW_LEFT.get());
                        output.accept(ModItems.BULB_GREEN_ARROW_LEFT.get());
                        output.accept(ModItems.BULB_RED_ARROW_RIGHT.get());
                        output.accept(ModItems.BULB_YELLOW_ARROW_RIGHT.get());
                        output.accept(ModItems.BULB_GREEN_ARROW_RIGHT.get());
                        output.accept(ModItems.BULB_STRAIGHT_RED.get());
                        output.accept(ModItems.BULB_STRAIGHT_YELLOW.get());
                        output.accept(ModItems.BULB_STRAIGHT_GREEN.get());
                        output.accept(ModItems.BULB_CROSS.get());
                        output.accept(ModItems.BULB_DONT_CROSS.get());
                        output.accept(ModItems.BULB_NO_RIGHT_TURN.get());
                        output.accept(ModItems.BULB_NO_LEFT_TURN.get());
                        output.accept(ModItems.BULB_TUNNEL_GREEN.get());
                        output.accept(ModItems.BULB_TUNNEL_RED.get());
                        // Street sign after bulbs
                        output.accept(ModItems.STREET_SIGN.get());
                        output.accept(ModItems.ILLUMINATED_STREET_SIGN.get());
                        // Traffic sensors after street sign
                        output.accept(ModItems.TRAFFIC_SENSOR_LEFT.get());
                        output.accept(ModItems.TRAFFIC_SENSOR_STRAIGHT.get());
                        output.accept(ModItems.TRAFFIC_SENSOR_RIGHT.get());
                    })
                    .build());
}