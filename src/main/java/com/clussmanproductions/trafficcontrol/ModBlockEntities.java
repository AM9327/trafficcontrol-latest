package com.clussmanproductions.trafficcontrol;

import com.clussmanproductions.trafficcontrol.tileentity.RotatableBlockEntity;
import com.clussmanproductions.trafficcontrol.tileentity.SignBlockEntity;
import com.clussmanproductions.trafficcontrol.tileentity.StreetSignBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// Registry for block entities (called "tile entities" in 1.12.2).
// A block entity is extra data/logic attached to a block — here we use it
// to get a custom renderer that can rotate the model to any angle.
public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModTrafficControl.MODID);

    // One block entity type shared by all blocks that need free rotation
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RotatableBlockEntity>> ROTATABLE =
            BLOCK_ENTITIES.register("rotatable",
                    () -> new BlockEntityType<>(
                            RotatableBlockEntity::new,
                            ModBlocks.CONE.get(),
                            ModBlocks.DRUM.get(),
                            ModBlocks.CHANNELIZER.get(),
                            ModBlocks.TYPE_3_BARRIER.get(),
                            ModBlocks.CONCRETE_BARRIER_WHITE.get(),
                            ModBlocks.CONCRETE_BARRIER_ORANGE.get(),
                            ModBlocks.CONCRETE_BARRIER_MAGENTA.get(),
                            ModBlocks.CONCRETE_BARRIER_LIGHT_BLUE.get(),
                            ModBlocks.CONCRETE_BARRIER_YELLOW.get(),
                            ModBlocks.CONCRETE_BARRIER_LIME.get(),
                            ModBlocks.CONCRETE_BARRIER_PINK.get(),
                            ModBlocks.CONCRETE_BARRIER_GRAY.get(),
                            ModBlocks.CONCRETE_BARRIER_LIGHT_GRAY.get(),
                            ModBlocks.CONCRETE_BARRIER_CYAN.get(),
                            ModBlocks.CONCRETE_BARRIER_PURPLE.get(),
                            ModBlocks.CONCRETE_BARRIER_BLUE.get(),
                            ModBlocks.CONCRETE_BARRIER_BROWN.get(),
                            ModBlocks.CONCRETE_BARRIER_GREEN.get(),
                            ModBlocks.CONCRETE_BARRIER_RED.get(),
                            ModBlocks.CONCRETE_BARRIER_BLACK.get(),
                            ModBlocks.TRAFFIC_RAIL.get(),
                            ModBlocks.BLACK_TL_TRIPLE.get(),
                            ModBlocks.BLACK_TL_SINGLE.get(),
                            ModBlocks.BLACK_TL_DOUBLE.get(),
                            ModBlocks.BLACK_TL_QUAD.get(),
                            ModBlocks.BLACK_TL_FIVE.get(),
                            ModBlocks.BLACK_TL_T.get(),
                            ModBlocks.BLACK_TL_DH.get(),
                            ModBlocks.YELLOW_TL_TRIPLE.get(),
                            ModBlocks.YELLOW_TL_SINGLE.get(),
                            ModBlocks.YELLOW_TL_DOUBLE.get(),
                            ModBlocks.YELLOW_TL_QUAD.get(),
                            ModBlocks.YELLOW_TL_FIVE.get(),
                            ModBlocks.YELLOW_TL_T.get(),
                            ModBlocks.YELLOW_TL_DH.get(),
                            ModBlocks.ORANGE_TL_TRIPLE.get(),
                            ModBlocks.ORANGE_TL_SINGLE.get(),
                            ModBlocks.ORANGE_TL_DOUBLE.get(),
                            ModBlocks.ORANGE_TL_QUAD.get(),
                            ModBlocks.ORANGE_TL_FIVE.get(),
                            ModBlocks.ORANGE_TL_T.get(),
                            ModBlocks.ORANGE_TL_DH.get(),
                            // Horizontal traffic light variants
                            ModBlocks.BLACK_TL_TRIPLE_HORIZ.get(),
                            ModBlocks.BLACK_TL_QUAD_HORIZ.get(),
                            ModBlocks.BLACK_TL_FIVE_HORIZ.get(),
                            ModBlocks.YELLOW_TL_TRIPLE_HORIZ.get(),
                            ModBlocks.YELLOW_TL_QUAD_HORIZ.get(),
                            ModBlocks.YELLOW_TL_FIVE_HORIZ.get(),
                            ModBlocks.ORANGE_TL_TRIPLE_HORIZ.get(),
                            ModBlocks.ORANGE_TL_QUAD_HORIZ.get(),
                            ModBlocks.ORANGE_TL_FIVE_HORIZ.get(),
                            ModBlocks.BLACK_TL_DOUBLE_HORIZ.get(),
                            ModBlocks.YELLOW_TL_DOUBLE_HORIZ.get(),
                            ModBlocks.ORANGE_TL_DOUBLE_HORIZ.get(),
                            ModBlocks.CROSSING_GATE_BASE.get(),
                            ModBlocks.STAND.get(),
                            ModBlocks.HORIZONTAL_POLE.get(),
                            ModBlocks.SIGNAL_ARM.get(),
                            ModBlocks.CROSSING_GATE_POLE.get()
                    ));

    // Dedicated block entity for signs — stores sign selection and text
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SignBlockEntity>> SIGN_ENTITY =
            BLOCK_ENTITIES.register("sign",
                    () -> new BlockEntityType<>(
                            SignBlockEntity::new,
                            ModBlocks.SIGN.get()
                    ));

    // Dedicated block entity for street signs — stores text and color
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StreetSignBlockEntity>> STREET_SIGN_ENTITY =
            BLOCK_ENTITIES.register("street_sign",
                    () -> new BlockEntityType<>(
                            StreetSignBlockEntity::new,
                            ModBlocks.STREET_SIGN.get(),
                            ModBlocks.ILLUMINATED_STREET_SIGN.get()
                    ));
}
