package me.ichun.mods.portalgunclassic.client.core;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.portalgunclassic.client.ClientState;
import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.core.ModRegistries;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalGun;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.Map;

@EventBusSubscriber(modid = PortalGunClassic.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class EventHandlerClientGame
{
    // One icon per slot — we tint them with the gun's dye color at render time
    public static final ResourceLocation TX_SLOT_EMPTY = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/overlay/lempty.png");
    public static final ResourceLocation TX_SLOT_FULL  = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/overlay/lfull.png");

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (ClientState.teleportCooldown > 0 && !mc.isPaused())
            ClientState.teleportCooldown--;

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

        // Check if holding a portal gun
        ItemStack mainHand = mc.player.getMainHandItem();
        ItemStack offHand  = mc.player.getOffhandItem();
        ItemStack gunStack = null;
        if (mainHand.getItem() instanceof ItemPortalGun) gunStack = mainHand;
        else if (offHand.getItem() instanceof ItemPortalGun) gunStack = offHand;
        if (gunStack == null) return;

        int heldColor = ItemPortalGun.getColorIndex(gunStack);
        Map<Integer, Integer> statusMap = ClientState.getPortalStatusSnapshot();

        int w    = mc.getWindow().getGuiScaledWidth();
        int h    = mc.getWindow().getGuiScaledHeight();
        int size = 20;
        int x    = w / 2 - size - 2;
        int y    = h / 2 - size / 2;

        net.minecraft.client.gui.GuiGraphics gui = event.getGuiGraphics();
        RenderSystem.enableBlend();

        // Draw slot A (left) and slot B (right) for the currently held gun's color
        int bits     = statusMap.getOrDefault(heldColor, 0);
        boolean aActive = (bits & 1) != 0;
        boolean bActive = (bits & 2) != 0;

        DyeColor dye = DyeColor.byId(heldColor);
        float[] c    = dye.getTextureDiffuseColors();

        // Slot A — tinted with dye color
        RenderSystem.setShaderColor(c[0], c[1], c[2], 1f);
        gui.blit(aActive ? TX_SLOT_FULL : TX_SLOT_EMPTY,
            x, y, 0, 0, size, size, size, size);

        // Slot B — slightly desaturated tint to distinguish
        RenderSystem.setShaderColor(c[0] * 0.6f, c[1] * 0.6f, c[2] * 0.6f, 1f);
        gui.blit(bActive ? TX_SLOT_FULL : TX_SLOT_EMPTY,
            x + size + 4, y, 0, 0, size, size, size, size);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void onClientConnected(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn event)
    {
        ClientState.updatePortalStatus(null, new java.util.HashMap<>());
        ClientState.justTeleported = false;
    }
}
