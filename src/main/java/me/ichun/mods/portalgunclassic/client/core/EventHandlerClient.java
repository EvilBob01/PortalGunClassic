package me.ichun.mods.portalgunclassic.client.core;

import me.ichun.mods.portalgunclassic.client.ClientState;
import me.ichun.mods.portalgunclassic.client.render.RenderPortalProjectile;
import me.ichun.mods.portalgunclassic.client.render.TileRendererPortal;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.core.ModRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = PortalGunClassic.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EventHandlerClient
{
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.register(ClientState.KEY_SWITCH);
        event.register(ClientState.KEY_RESET);
    }

    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(ModRegistries.ENTITY_PORTAL_PROJECTILE.get(), RenderPortalProjectile::new);
        event.registerBlockEntityRenderer(ModRegistries.TILE_PORTAL.get(), TileRendererPortal::new);
    }
}
