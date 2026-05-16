package me.ichun.mods.portalgunclassic.common.world;

import me.ichun.mods.portalgunclassic.common.packet.PacketPortalStatus;
import me.ichun.mods.portalgunclassic.common.portal.PortalInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class PortalSavedData extends SavedData
{
    public static final String DATA_ID = "PortalGunClassicSaveData";

    // Keyed by dimension ResourceKey location string
    public HashMap<ResourceKey<Level>, HashMap<String, PortalInfo>> portalInfo = new HashMap<>();

    public PortalSavedData() {}

    public static PortalSavedData getOrCreate(Level level)
    {
        return level.getServer().overworld().getDataStorage()
            .computeIfAbsent(new SavedData.Factory<PortalSavedData>(PortalSavedData::new, PortalSavedData::load, DataFixTypes.LEVEL), DATA_ID);
    }

    public void set(Level world, boolean orange, BlockPos pos)
    {
        HashMap<String, PortalInfo> map = portalInfo.computeIfAbsent(world.dimension(), k -> new HashMap<>());
        map.put(orange ? "orange" : "blue", new PortalInfo(orange, pos));
        setDirty();
        broadcast(world, map);
    }

    public void kill(Level world, boolean orange)
    {
        HashMap<String, PortalInfo> map = portalInfo.get(world.dimension());
        if (map != null)
        {
            PortalInfo info = map.get(orange ? "orange" : "blue");
            if (info != null)
            {
                info.kill(world);
                map.remove(orange ? "orange" : "blue");
                if (map.isEmpty()) portalInfo.remove(world.dimension());
                setDirty();
            }
            broadcast(world, map);
        }
    }

    private void broadcast(Level world, HashMap<String, PortalInfo> map)
    {
        PacketPortalStatus packet = new PacketPortalStatus(
            map != null && map.containsKey("blue"),
            map != null && map.containsKey("orange"));

        for (ServerPlayer player : ((ServerLevel) world).players())
        {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

    public static PortalSavedData load(CompoundTag tag, HolderLookup.Provider provider)
    {
        PortalSavedData data = new PortalSavedData();
        int count = tag.getInt("dimCount");
        for (int i = 0; i < count; i++)
        {
            CompoundTag dimTag = tag.getCompound("dim" + i);
            HashMap<String, PortalInfo> map = new HashMap<>();
            if (dimTag.contains("blue"))   map.put("blue",   PortalInfo.createFromNBT(dimTag.getCompound("blue")));
            if (dimTag.contains("orange")) map.put("orange", PortalInfo.createFromNBT(dimTag.getCompound("orange")));
            if (!map.isEmpty())
            {
                ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION,
                    ResourceLocation.parse(dimTag.getString("dim")));
                data.portalInfo.put(key, map);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
    {
        tag.putInt("dimCount", portalInfo.size());
        int i = 0;
        for (Map.Entry<ResourceKey<Level>, HashMap<String, PortalInfo>> e : portalInfo.entrySet())
        {
            CompoundTag dimTag = new CompoundTag();
            dimTag.putString("dim", e.getKey().location().toString());
            if (e.getValue().containsKey("blue"))   dimTag.put("blue",   e.getValue().get("blue").toNBT());
            if (e.getValue().containsKey("orange")) dimTag.put("orange", e.getValue().get("orange").toNBT());
            tag.put("dim" + i, dimTag);
            i++;
        }
        return tag;
    }
}
