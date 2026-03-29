package com.clussmanproductions.trafficcontrol;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SIGN.get(), ChunkSectionLayer.CUTOUT);
        });
    }
}
