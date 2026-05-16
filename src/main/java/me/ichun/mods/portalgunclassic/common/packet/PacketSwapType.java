package me.ichun.mods.portalgunclassic.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.portalgunclassic.common.core.ModRegistries;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import me.ichun.mods.portalgunclassic.common.world.PortalSavedData;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSwapType(boolean reset, int portalType) implements CustomPacketPayload
{
    public static final Type<PacketSwapType> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("portalgunclassic", "swap_type"));

    public static final StreamCodec<ByteBuf, PacketSwapType> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, PacketSwapType::reset,
        ByteBufCodecs.INT,  PacketSwapType::portalType,
        PacketSwapType::new);

    @Override
    public Type<PacketSwapType> type() { return TYPE; }

    public static void handle(PacketSwapType packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() ->
        {
            ServerPlayer player = (ServerPlayer) ctx.player();
            if (!packet.reset())
                {
                    // Swap blue <-> orange gun in hands
                    for (InteractionHand hand : InteractionHand.values())
                    {
                        ItemStack is = player.getItemInHand(hand);
                        if (is.is(ModRegistries.ITEM_PORTAL_GUN_BLUE.get()))
                        {
                            player.setItemInHand(hand, new ItemStack(ModRegistries.ITEM_PORTAL_GUN_ORANGE.get()));
                        }
                        else if (is.is(ModRegistries.ITEM_PORTAL_GUN_ORANGE.get()))
                        {
                            player.setItemInHand(hand, new ItemStack(ModRegistries.ITEM_PORTAL_GUN_BLUE.get()));
                        }
                    }
                }
                else
                {
                    PortalSavedData data = PortalSavedData.getOrCreate(player.level());
                    if (packet.portalType() == 0)
                {
                    data.kill(player.level(), false);
                    data.kill(player.level(), true);
                }
                else
                {
                    for (InteractionHand hand : InteractionHand.values())
                    {
                        ItemStack is = player.getItemInHand(hand);
                        if (is.is(ModRegistries.ITEM_PORTAL_GUN_BLUE.get()))
                        {
                            data.kill(player.level(), false);
                        }
                        else if (is.is(ModRegistries.ITEM_PORTAL_GUN_ORANGE.get()))
                        {
                            data.kill(player.level(), true);
                        }
                    }
                }
                player.level().playSound(null, player.getX(), player.getEyeY(), player.getZ(),
                    SoundRegistry.RESET.get(), SoundSource.PLAYERS, 0.3F, 1.0F);
            }
        });
    }
}
