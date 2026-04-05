package com.clussmanproductions.trafficcontrol;

import com.clussmanproductions.trafficcontrol.network.PacketUpdateSign;
import com.clussmanproductions.trafficcontrol.signs.SignRepository;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(ModTrafficControl.MODID)
public class ModTrafficControl {

    public static final String MODID = "trafficcontrol";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final SignRepository SIGN_REPO = new SignRepository();

    public ModTrafficControl(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Traffic Control mod is loading!");

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTab.CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(ClientSetup::onClientSetup);
        modEventBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToServer(
                PacketUpdateSign.TYPE,
                PacketUpdateSign.STREAM_CODEC,
                PacketUpdateSign::handle
        );
    }
}
