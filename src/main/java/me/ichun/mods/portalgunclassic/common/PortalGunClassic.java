package me.ichun.mods.portalgunclassic.common;

import me.ichun.mods.portalgunclassic.common.core.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(PortalGunClassic.MOD_ID)
public class PortalGunClassic
{
    public static final String MOD_ID = "portalgunclassic";

    public PortalGunClassic(IEventBus modBus)
    {
        ModRegistries.register(modBus);
    }
}
