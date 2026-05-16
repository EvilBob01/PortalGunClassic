package me.ichun.mods.portalgunclassic.common.packet;

import me.ichun.mods.portalgunclassic.common.portal.PortalInfo;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import me.ichun.mods.portalgunclassic.common.world.PortalSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Collections;

public record PacketRequestTeleport(BlockPos pos) implements CustomPacketPayload
{
    public static final Type<PacketRequestTeleport> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("portalgunclassic", "request_teleport"));

    public static final StreamCodec<FriendlyByteBuf, PacketRequestTeleport> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> buf.writeBlockPos(pkt.pos()),
        buf -> new PacketRequestTeleport(buf.readBlockPos()));

    @Override
    public Type<PacketRequestTeleport> type() { return TYPE; }

    public static void handle(PacketRequestTeleport packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() ->
        {
            ServerPlayer player = (ServerPlayer) ctx.player();
            BlockEntity te = player.level().getBlockEntity(packet.pos());
            if (!(te instanceof TileEntityPortal current) || current.ownerUUID == null) return;

            PortalSavedData data = PortalSavedData.getOrCreate(player.level());
            PortalInfo pairInfo = data.getPair(current.ownerUUID, current.colorIndex, current.slot);
            if (pairInfo == null) return;

            BlockEntity destTe = player.level().getBlockEntity(pairInfo.pos);
            if (!(destTe instanceof TileEntityPortal dest)) return;

            current.teleport(player.level(), player, dest);
            player.connection.teleport(player.getX(), player.getY(), player.getZ(),
                player.getYRot(), player.getXRot(), Collections.emptySet());
        });
    }
}
