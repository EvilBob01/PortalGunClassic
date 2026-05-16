package me.ichun.mods.portalgunclassic.common.core;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.packet.PacketEntityLocation;
import me.ichun.mods.portalgunclassic.common.packet.PacketPortalStatus;
import me.ichun.mods.portalgunclassic.common.packet.PacketRequestTeleport;
import me.ichun.mods.portalgunclassic.common.packet.PacketSwapType;
import me.ichun.mods.portalgunclassic.common.portal.PortalInfo;
import me.ichun.mods.portalgunclassic.common.world.PortalSavedData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;

@EventBusSubscriber(modid = PortalGunClassic.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class EventHandlerServer
{
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        updatePlayerDimensionStatus(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        updatePlayerDimensionStatus(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        updatePlayerDimensionStatus(event.getEntity());
    }

    private static void updatePlayerDimensionStatus(Player player)
    {
        if (!(player instanceof ServerPlayer sp)) return;
        PortalSavedData data = PortalSavedData.getOrCreate(player.level());
        HashMap<String, PortalInfo> map = data.portalInfo.get(player.level().dimension());
        PacketDistributor.sendToPlayer(sp,
            new PacketPortalStatus(map != null && map.containsKey("blue"),
                                   map != null && map.containsKey("orange")));
    }
}
