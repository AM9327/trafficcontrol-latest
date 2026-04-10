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

import java.util.ArrayList;
import java.util.List;

public record PacketUpdateStreetSign(
        BlockPos pos,
        List<String> texts,
        List<Integer> colorIndices,
        int textColor
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketUpdateStreetSign> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ModTrafficControl.MODID, "update_street_sign"));

    public static final StreamCodec<ByteBuf, PacketUpdateStreetSign> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketUpdateStreetSign::pos,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), PacketUpdateStreetSign::texts,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), PacketUpdateStreetSign::colorIndices,
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
                    // Update all sign entries
                    int count = Math.min(packet.texts().size(), StreetSignBlockEntity.MAX_SIGNS);
                    // Reset and rebuild
                    for (int i = 0; i < count; i++) {
                        if (i >= streetSignBE.getSignCount()) {
                            int colorIdx = i < packet.colorIndices().size() ? packet.colorIndices().get(i) : 0;
                            streetSignBE.addSign(colorIdx);
                        }
                        streetSignBE.setText(i, packet.texts().get(i));
                        if (i < packet.colorIndices().size()) {
                            streetSignBE.setColorIndex(i, packet.colorIndices().get(i));
                        }
                    }
                    streetSignBE.setTextColor(packet.textColor());
                    streetSignBE.syncToClient();
                }
            }
        });
    }
}
