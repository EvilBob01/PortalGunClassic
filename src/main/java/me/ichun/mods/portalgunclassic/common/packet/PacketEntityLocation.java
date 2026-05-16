package me.ichun.mods.portalgunclassic.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.portalgunclassic.client.ClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketEntityLocation(
    int id,
    double posX, double posY, double posZ,
    double mX,   double mY,   double mZ,
    float  yaw,  float  pitch
) implements CustomPacketPayload
{
    public static final Type<PacketEntityLocation> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("portalgunclassic", "entity_location"));

    // StreamCodec.composite() max is 6 fields; split into two stages via map()
    private record PartA(int id, double posX, double posY, double posZ, double mX, double mY) {}
    private record PartB(double mZ, float yaw, float pitch) {}

    private static final StreamCodec<ByteBuf, PartA> CODEC_A = StreamCodec.composite(
        ByteBufCodecs.INT,    PartA::id,
        ByteBufCodecs.DOUBLE, PartA::posX,
        ByteBufCodecs.DOUBLE, PartA::posY,
        ByteBufCodecs.DOUBLE, PartA::posZ,
        ByteBufCodecs.DOUBLE, PartA::mX,
        ByteBufCodecs.DOUBLE, PartA::mY,
        PartA::new);

    private static final StreamCodec<ByteBuf, PartB> CODEC_B = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, PartB::mZ,
        ByteBufCodecs.FLOAT,  PartB::yaw,
        ByteBufCodecs.FLOAT,  PartB::pitch,
        PartB::new);

    public static final StreamCodec<ByteBuf, PacketEntityLocation> STREAM_CODEC =
        StreamCodec.of(
            (buf, pkt) -> {
                CODEC_A.encode(buf, new PartA(pkt.id(), pkt.posX(), pkt.posY(), pkt.posZ(), pkt.mX(), pkt.mY()));
                CODEC_B.encode(buf, new PartB(pkt.mZ(), pkt.yaw(), pkt.pitch()));
            },
            buf -> {
                PartA a = CODEC_A.decode(buf);
                PartB b = CODEC_B.decode(buf);
                return new PacketEntityLocation(a.id(), a.posX(), a.posY(), a.posZ(), a.mX(), a.mY(), b.mZ(), b.yaw(), b.pitch());
            }
        );

    public PacketEntityLocation(Entity ent)
    {
        this(ent.getId(),
            ent.getX(), ent.getY(), ent.getZ(),
            ent.getDeltaMovement().x, ent.getDeltaMovement().y, ent.getDeltaMovement().z,
            ent.getYRot(), ent.getXRot());
    }

    @Override
    public Type<PacketEntityLocation> getType() { return TYPE; }

    public static void handle(PacketEntityLocation packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> handleClient(packet));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PacketEntityLocation packet)
    {
        if (Minecraft.getInstance().level == null) return;
        Entity ent = Minecraft.getInstance().level.getEntity(packet.id());
        if (ent == null) return;

        ent.setPos(packet.posX(), packet.posY(), packet.posZ());
        ent.setDeltaMovement(packet.mX(), packet.mY(), packet.mZ());
        ent.setYRot(packet.yaw());
        ent.setXRot(packet.pitch());
        ent.xo = packet.posX();
        ent.yo = packet.posY();
        ent.zo = packet.posZ();

        if (ent == Minecraft.getInstance().player)
        {
            ClientState.justTeleported = true;
            ClientState.mX = packet.mX();
            ClientState.mY = packet.mY();
            ClientState.mZ = packet.mZ();
        }
    }
}
