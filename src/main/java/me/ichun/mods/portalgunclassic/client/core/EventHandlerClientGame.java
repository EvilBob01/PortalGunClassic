package me.ichun.mods.portalgunclassic.client.core;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.portalgunclassic.client.ClientState;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.core.ModRegistries;
import me.ichun.mods.portalgunclassic.common.packet.PacketSwapType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = PortalGunClassic.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class EventHandlerClientGame
{
    public static final ResourceLocation TX_L_EMPTY  = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/overlay/lempty.png");
    public static final ResourceLocation TX_L_FULL   = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/overlay/lfull.png");
    public static final ResourceLocation TX_R_EMPTY  = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/overlay/rempty.png");
    public static final ResourceLocation TX_R_FULL   = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/overlay/rfull.png");

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean holdingGun = mc.player.getMainHandItem().is(ModRegistries.ITEM_PORTAL_GUN_BLUE.get())
            || mc.player.getMainHandItem().is(ModRegistries.ITEM_PORTAL_GUN_ORANGE.get())
            || mc.player.getOffhandItem().is(ModRegistries.ITEM_PORTAL_GUN_BLUE.get())
            || mc.player.getOffhandItem().is(ModRegistries.ITEM_PORTAL_GUN_ORANGE.get());

        if (holdingGun)
        {
            if (!ClientState.keySwitchDown && ClientState.KEY_SWITCH.isDown())
            {
                PacketDistributor.sendToServer(new PacketSwapType(false, 0));
            }
            if (!ClientState.keyResetDown && ClientState.KEY_RESET.isDown())
            {
                PacketDistributor.sendToServer(new PacketSwapType(true, Screen.hasShiftDown() ? 1 : 0));
            }
            ClientState.keySwitchDown = ClientState.KEY_SWITCH.isDown();
            ClientState.keyResetDown  = ClientState.KEY_RESET.isDown();
        }

        if (ClientState.teleportCooldown > 0 && !mc.isPaused())
        {
            ClientState.teleportCooldown--;
        }

        if (ClientState.justTeleported && mc.player != null)
        {
            if (mc.player.getDeltaMovement().lengthSqr() == 0D)
            {
                ClientState.justTeleported = false;
                mc.player.setDeltaMovement(ClientState.mX, ClientState.mY, ClientState.mZ);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) return;

        boolean holdingGun = mc.player.getMainHandItem().is(ModRegistries.ITEM_PORTAL_GUN_BLUE.get())
            || mc.player.getMainHandItem().is(ModRegistries.ITEM_PORTAL_GUN_ORANGE.get())
            || mc.player.getOffhandItem().is(ModRegistries.ITEM_PORTAL_GUN_BLUE.get())
            || mc.player.getOffhandItem().is(ModRegistries.ITEM_PORTAL_GUN_ORANGE.get());

        if (!holdingGun) return;

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        int size = 40;
        int x1 = w / 2 - size + 1;
        int y1 = h / 2 - size + 1;

        net.minecraft.client.gui.GuiGraphics gui = event.getGuiGraphics();

        boolean blueActive   = ClientState.status != null && ClientState.status.blue;
        boolean orangeActive = ClientState.status != null && ClientState.status.orange;

        RenderSystem.enableBlend();

        RenderSystem.setShaderColor(5 / 255f, 130 / 255f, 255 / 255f, 1f);
        gui.blit(blueActive ? TX_L_FULL : TX_L_EMPTY, x1, y1, 0, 0, size * 2, size * 2, size * 2, size * 2);

        RenderSystem.setShaderColor(255 / 255f, 176 / 255f, 6 / 255f, 1f);
        gui.blit(orangeActive ? TX_R_FULL : TX_R_EMPTY, x1, y1, 0, 0, size * 2, size * 2, size * 2, size * 2);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void onClientConnected(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn event)
    {
        ClientState.status = null;
        ClientState.justTeleported = false;
    }
}
