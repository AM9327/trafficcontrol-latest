package com.clussmanproductions.trafficcontrol.network;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.clussmanproductions.trafficcontrol.tileentity.StreetSignBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketUpdateStreetSign(
        BlockPos pos,
        String text1,
        String text2,
        int colorIndex,
        int textColor
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketUpdateStreetSign> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "update_street_sign"));

    public static final StreamCodec<ByteBuf, PacketUpdateStreetSign> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketUpdateStreetSign::pos,
            ByteBufCodecs.STRING_UTF8, PacketUpdateStreetSign::text1,
            ByteBufCodecs.STRING_UTF8, PacketUpdateStreetSign::text2,
            ByteBufCodecs.INT, PacketUpdateStreetSign::colorIndex,
            ByteBufCodecs.INT, PacketUpdateStreetSign::textColor,
            PacketUpdateStreetSign::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketUpdateStreetSign packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity be = serverPlayer.level().getBlockEntity(packet.pos());
                if (be instanceof StreetSignBlockEntity streetSignBE) {
                    streetSignBE.setText1(packet.text1());
                    streetSignBE.setText2(packet.text2());
                    streetSignBE.setColorIndex(packet.colorIndex());
                    streetSignBE.setTextColor(packet.textColor());
                    streetSignBE.syncToClient();
                }
            }
        });
    }
}
