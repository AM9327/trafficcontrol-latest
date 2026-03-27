package com.clussmanproductions.trafficcontrol;

// In Python you'd write: import logging
// Java uses a logger library called SLF4J - same concept as Python's logging module
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

// These are NeoForge imports - like importing from a framework (e.g., from flask import Flask)
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// @Mod is a "decorator" (Java calls them "annotations")
// In Python terms: @app.route("/") marks a function as a route handler
// @Mod marks this class as the mod entry point
@Mod(ModTrafficControl.MODID)
public class ModTrafficControl {

    // "public static final String" = a constant
    // Python equivalent: MODID = "trafficcontrol"  (but truly immutable)
    public static final String MODID = "trafficcontrol";

    // Logger - same as: logger = logging.getLogger(__name__)
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor - like Python's __init__(self)
    // NeoForge automatically passes modEventBus and modContainer (dependency injection)
    // Python equivalent: def __init__(self, event_bus, container):
    public ModTrafficControl(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Traffic Control mod is loading!");

        // Register our blocks and items with NeoForge
        // This is like telling the framework "here are my things, add them to the game"
        // In Python terms: app.register_blueprint(blocks_blueprint)
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTab.CREATIVE_TABS.register(modEventBus);
    }
}
