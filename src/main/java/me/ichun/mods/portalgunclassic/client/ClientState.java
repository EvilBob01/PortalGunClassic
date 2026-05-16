package me.ichun.mods.portalgunclassic.client;

import me.ichun.mods.portalgunclassic.client.portal.PortalStatus;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;

public class ClientState
{
    public static final KeyMapping KEY_SWITCH = new KeyMapping("key.portalgunclassic.switch", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.categories.portalgun");
    public static final KeyMapping KEY_RESET  = new KeyMapping("key.portalgunclassic.reset",  InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.portalgun");

    public static boolean keySwitchDown = false;
    public static boolean keyResetDown  = false;

    public static PortalStatus status = null;
    public static int teleportCooldown = 0;

    public static boolean justTeleported = false;
    public static double mX = 0D;
    public static double mY = 0D;
    public static double mZ = 0D;
}
