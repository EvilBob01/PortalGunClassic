package me.ichun.mods.portalgunclassic.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.portalgunclassic.client.ClientState;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Sent server->client to update a player's local portal status map.
 * statusMap: colorIndex (0-15) → bitmask (bit 0 = slot A active, bit 1 = slot B active)
 */
public record PacketPortalStatus(UUID ownerUUID, Map<Integer, Integer> statusMap) implements CustomPacketPayload
{
    public static final Type<PacketPortalStatus> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("portalgunclassic", "portal_status"));

    public static final StreamCodec<ByteBuf, PacketPortalStatus> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> {
            // Write UUID
            buf.writeLong(pkt.ownerUUID().getMostSignificantBits());
            buf.writeLong(pkt.ownerUUID().getLeastSignificantBits());
            // Write map size + entries
            buf.writeInt(pkt.statusMap().size());
            for (Map.Entry<Integer, Integer> e : pkt.statusMap().entrySet())
            {
                buf.writeByte(e.getKey());
                buf.writeByte(e.getValue());
            }
        },
        buf -> {
            UUID uuid = new UUID(buf.readLong(), buf.readLong());
            int size = buf.readInt();
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 0; i < size; i++)
                map.put((int) buf.readByte(), (int) buf.readByte());
            return new PacketPortalStatus(uuid, map);
        });

    @Override
    public Type<PacketPortalStatus> type() { return TYPE; }

    public static void handle(PacketPortalStatus packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> ClientState.updatePortalStatus(packet.ownerUUID(), packet.statusMap()));
    }
}
