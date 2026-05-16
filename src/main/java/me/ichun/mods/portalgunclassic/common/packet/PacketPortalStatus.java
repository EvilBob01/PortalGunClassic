package me.ichun.mods.portalgunclassic.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.portalgunclassic.client.ClientState;
import me.ichun.mods.portalgunclassic.client.portal.PortalStatus;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketPortalStatus(boolean blue, boolean orange) implements CustomPacketPayload
{
    public static final Type<PacketPortalStatus> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("portalgunclassic", "portal_status"));

    public static final StreamCodec<ByteBuf, PacketPortalStatus> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, PacketPortalStatus::blue,
        ByteBufCodecs.BOOL, PacketPortalStatus::orange,
        PacketPortalStatus::new);

    @Override
    public Type<PacketPortalStatus> type() { return TYPE; }

    public static void handle(PacketPortalStatus packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> ClientState.status = new PortalStatus(packet.blue(), packet.orange()));
    }
}
