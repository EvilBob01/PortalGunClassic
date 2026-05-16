package me.ichun.mods.portalgunclassic.common.world;

import me.ichun.mods.portalgunclassic.common.packet.PacketPortalStatus;
import me.ichun.mods.portalgunclassic.common.portal.PortalInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class PortalSavedData extends SavedData
{
    public static final String DATA_ID = "PortalGunClassicSaveData";

    /**
     * Flat map: key = "ownerUUID:colorIndex:slot"  →  PortalInfo
     * Portals from different players/colors/slots never interact.
     */
    public final HashMap<String, PortalInfo> portals = new HashMap<>();

    public PortalSavedData() {}

    public static PortalSavedData getOrCreate(Level level)
    {
        return level.getServer().overworld().getDataStorage()
            .computeIfAbsent(new SavedData.Factory<PortalSavedData>(
                PortalSavedData::new, PortalSavedData::load, DataFixTypes.LEVEL), DATA_ID);
    }

    // ── Mutation ─────────────────────────────────────────────────────────────

    public void set(Level world, UUID owner, int colorIndex, String slot, BlockPos pos)
    {
        String key = PortalInfo.makeKey(owner, colorIndex, slot);
        portals.put(key, new PortalInfo(owner, colorIndex, slot, pos));
        setDirty();
        sendStatusToPlayer(world, owner);
    }

    public void killForPlayer(Level world, UUID owner, int colorIndex, String slot)
    {
        String key = PortalInfo.makeKey(owner, colorIndex, slot);
        PortalInfo info = portals.remove(key);
        if (info != null)
        {
            info.kill(world);
            setDirty();
        }
        sendStatusToPlayer(world, owner);
    }

    /** Returns the paired portal for a given portal (same owner+color, opposite slot), or null */
    public PortalInfo getPair(UUID owner, int colorIndex, String slot)
    {
        return portals.get(PortalInfo.pairKey(owner, colorIndex, slot));
    }

    // ── Networking ───────────────────────────────────────────────────────────

    /**
     * Sends the current portal status for a specific player to that player.
     * Status = per-colorIndex bitmask: bit 0 = slot A active, bit 1 = slot B active
     */
    public void sendStatusToPlayer(Level world, UUID ownerUUID)
    {
        if (!(world instanceof ServerLevel serverLevel)) return;
        ServerPlayer target = serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
        if (target == null) return;

        Map<Integer, Integer> statusMap = buildStatusMap(ownerUUID);
        PacketDistributor.sendToPlayer(target, new PacketPortalStatus(ownerUUID, statusMap));
    }

    public Map<Integer, Integer> buildStatusMap(UUID ownerUUID)
    {
        Map<Integer, Integer> map = new HashMap<>();
        for (PortalInfo info : portals.values())
        {
            if (!info.ownerUUID.equals(ownerUUID)) continue;
            int bits = map.getOrDefault(info.colorIndex, 0);
            bits |= info.slot.equals("a") ? 1 : 2;
            map.put(info.colorIndex, bits);
        }
        return map;
    }

    // ── Serialisation ────────────────────────────────────────────────────────

    public static PortalSavedData load(CompoundTag tag, HolderLookup.Provider provider)
    {
        PortalSavedData data = new PortalSavedData();
        ListTag list = tag.getList("portals", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            PortalInfo info = PortalInfo.createFromNBT(list.getCompound(i));
            data.portals.put(info.key(), info);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
    {
        ListTag list = new ListTag();
        for (PortalInfo info : portals.values())
            list.add(info.toNBT());
        tag.put("portals", list);
        return tag;
    }
}
