package com.clussmanproductions.trafficcontrol.event;

import com.clussmanproductions.trafficcontrol.ModBlockEntities;
import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.clussmanproductions.trafficcontrol.tileentity.render.RotatableBlockEntityRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;

@EventBusSubscriber(modid = ModTrafficControl.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ROTATABLE.get(), RotatableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SIGN_ENTITY.get(), RotatableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.STREET_SIGN_ENTITY.get(), RotatableBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerStandaloneModels(ModelEvent.RegisterStandalone event) {
        event.register(
                RotatableBlockEntityRenderer.BACK_POLE_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/back_pole")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.HORIZONTAL_BAR_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/traffic_light_horizontal_bar")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.HORIZONTAL_POLE_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/horizontal_pole")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.HORIZONTAL_BAR_CONNECT_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/traffic_light_horizontal_bar_connect")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.TRAFFIC_LIGHT_CONNECT_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/traffic_light_connect")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.SIGNAL_ARM_BAR_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/signal_arm_bar")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/traffic_light_pole_arm")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.CG_POLE_ARM_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/cg_pole_arm")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.TRAFFIC_LIGHT_PAIRED_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/traffic_light_paired")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.BACK_POLE_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/traffic_light_back_pole")
                )
        );
        event.register(
                RotatableBlockEntityRenderer.HANGING_BRACKET_MODEL_KEY,
                SimpleUnbakedStandaloneModel.blockStateModel(
                        Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "block/hanging_bracket")
                )
        );
    }
}
