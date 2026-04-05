package com.clussmanproductions.trafficcontrol.network;

import com.clussmanproductions.trafficcontrol.ModTrafficControl;
import com.clussmanproductions.trafficcontrol.tileentity.SignBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public record PacketUpdateSign(
        BlockPos pos,
        UUID signId,
        List<String> textLines
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketUpdateSign> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "update_sign"));

    public static final StreamCodec<ByteBuf, PacketUpdateSign> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketUpdateSign::pos,
            ByteBufCodecs.STRING_UTF8.map(UUID::fromString, UUID::toString), PacketUpdateSign::signId,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), PacketUpdateSign::textLines,
            PacketUpdateSign::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketUpdateSign packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity be = serverPlayer.level().getBlockEntity(packet.pos());
                if (be instanceof SignBlockEntity signBE) {
                    signBE.setSignId(packet.signId());
                    signBE.setTextLines(packet.textLines());
                    signBE.setChanged();
                    signBE.syncToClient();
                }
            }
        });
    }
}
