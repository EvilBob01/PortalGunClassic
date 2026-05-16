package me.ichun.mods.portalgunclassic.common.core;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalGun;
import me.ichun.mods.portalgunclassic.common.world.PortalSavedData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = PortalGunClassic.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class EventHandlerServer
{
    /** Left-click on air/block with portal gun = fire slot A */
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event)
    {
        triggerSlotA(event.getEntity(), event.getItemStack());
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        triggerSlotA(event.getEntity(), event.getItemStack());
    }

    private static void triggerSlotA(Player player, ItemStack stack)
    {
        if (stack.getItem() instanceof ItemPortalGun gun && !player.level().isClientSide)
            gun.fireSlotA(player.level(), player, stack);
    }

    /** On login/respawn/dimension change: send fresh portal status to the player */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        syncStatus(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        syncStatus(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        syncStatus(event.getEntity());
    }

    private static void syncStatus(Player player)
    {
        if (!(player instanceof ServerPlayer sp)) return;
        PortalSavedData data = PortalSavedData.getOrCreate(player.level());
        data.sendStatusToPlayer(player.level(), player.getUUID());
    }
}
