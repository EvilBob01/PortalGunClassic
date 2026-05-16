package me.ichun.mods.portalgunclassic.common.core;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalGun;
import me.ichun.mods.portalgunclassic.common.packet.PacketEntityLocation;
import me.ichun.mods.portalgunclassic.common.packet.PacketPortalStatus;
import me.ichun.mods.portalgunclassic.common.packet.PacketRequestTeleport;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = PortalGunClassic.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EventHandlerCommon
{
    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(PacketPortalStatus.TYPE,    PacketPortalStatus.STREAM_CODEC,    PacketPortalStatus::handle);
        registrar.playToServer(PacketRequestTeleport.TYPE, PacketRequestTeleport.STREAM_CODEC, PacketRequestTeleport::handle);
        registrar.playToClient(PacketEntityLocation.TYPE,  PacketEntityLocation.STREAM_CODEC,  PacketEntityLocation::handle);
    }

    @SubscribeEvent
    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(ModRegistries.ITEM_PORTAL_GUN);
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(ModRegistries.ITEM_PORTAL_CORE);
    }
}
