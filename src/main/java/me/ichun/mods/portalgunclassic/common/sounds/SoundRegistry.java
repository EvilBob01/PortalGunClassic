package me.ichun.mods.portalgunclassic.common.sounds;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class SoundRegistry
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, PortalGunClassic.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ENTER    = reg("enter");
    public static final DeferredHolder<SoundEvent, SoundEvent> EXIT     = reg("exit");
    public static final DeferredHolder<SoundEvent, SoundEvent> FIZZLE   = reg("fizzle");
    public static final DeferredHolder<SoundEvent, SoundEvent> INVALID  = reg("invalid");
    public static final DeferredHolder<SoundEvent, SoundEvent> OPENBLUE = reg("openblue");
    public static final DeferredHolder<SoundEvent, SoundEvent> OPENRED  = reg("openred");
    public static final DeferredHolder<SoundEvent, SoundEvent> FIREBLUE = reg("fireblue");
    public static final DeferredHolder<SoundEvent, SoundEvent> FIRERED  = reg("firered");
    public static final DeferredHolder<SoundEvent, SoundEvent> RESET    = reg("reset");
    public static final DeferredHolder<SoundEvent, SoundEvent> ACTIVE   = reg("active");

    private static DeferredHolder<SoundEvent, SoundEvent> reg(String name)
    {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(PortalGunClassic.MOD_ID, name)));
    }

    public static void register(IEventBus modBus)
    {
        SOUNDS.register(modBus);
    }
}
