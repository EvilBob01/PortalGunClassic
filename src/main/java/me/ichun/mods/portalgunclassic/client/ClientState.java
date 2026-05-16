package me.ichun.mods.portalgunclassic.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientState
{
    // ── Portal status ────────────────────────────────────────────────────────
    // Per-color bitmask: bit 0 = slot A active, bit 1 = slot B active
    // Only stored for the local player's UUID
    private static UUID       localOwner   = null;
    private static Map<Integer, Integer> portalStatus = new HashMap<>();

    public static void updatePortalStatus(UUID owner, Map<Integer, Integer> statusMap)
    {
        localOwner   = owner;
        portalStatus = new HashMap<>(statusMap);
    }

    /** Returns bitmask for the given color (0 if none active) */
    public static int getPortalBits(int colorIndex)
    {
        return portalStatus.getOrDefault(colorIndex, 0);
    }

    public static boolean hasAnyPortals()
    {
        return !portalStatus.isEmpty() && portalStatus.values().stream().anyMatch(v -> v != 0);
    }

    /** Returns a snapshot of the full status map for HUD rendering */
    public static Map<Integer, Integer> getPortalStatusSnapshot()
    {
        return new HashMap<>(portalStatus);
    }

    // ── Teleport state ───────────────────────────────────────────────────────
    public static int     teleportCooldown = 0;
    public static boolean justTeleported  = false;
    public static double  mX = 0D, mY = 0D, mZ = 0D;
}
