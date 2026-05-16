package me.ichun.mods.portalgunclassic.common.portal;

import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public class PortalInfo
{
    public UUID ownerUUID;
    public int  colorIndex;
    public String slot;   // "a" or "b"
    public BlockPos pos;

    public PortalInfo(UUID ownerUUID, int colorIndex, String slot, BlockPos pos)
    {
        this.ownerUUID  = ownerUUID;
        this.colorIndex = colorIndex;
        this.slot       = slot;
        this.pos        = pos;
    }

    /** Unique key used as map key in PortalSavedData */
    public static String makeKey(UUID owner, int colorIndex, String slot)
    {
        return owner.toString() + ":" + colorIndex + ":" + slot;
    }

    public String key() { return makeKey(ownerUUID, colorIndex, slot); }

    /** Returns the key for the paired portal (same owner+color, opposite slot) */
    public static String pairKey(UUID owner, int colorIndex, String slot)
    {
        return makeKey(owner, colorIndex, slot.equals("a") ? "b" : "a");
    }

    public void kill(Level world)
    {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TileEntityPortal portal)
        {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            if (portal.face.getAxis() != Direction.Axis.Y)
            {
                BlockPos offset = portal.top ? pos.below() : pos.above();
                if (world.getBlockEntity(offset) instanceof TileEntityPortal)
                    world.setBlock(offset, Blocks.AIR.defaultBlockState(), 3);
            }
            double cx = pos.getX() + (portal.face.getAxis() != Direction.Axis.Y ? 1D : 0.5D);
            double cy = pos.getY() + (portal.face.getAxis() == Direction.Axis.Y  ? 0D : 0.5D);
            double cz = pos.getZ() + (portal.face.getAxis() != Direction.Axis.Y ? 1D : 0.5D);
            world.playSound(null, cx, cy, cz, SoundRegistry.FIZZLE.get(), SoundSource.BLOCKS, 0.3F, 1F);
        }
        else
        {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public CompoundTag toNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("ownerUUID", ownerUUID);
        tag.putInt("colorIndex", colorIndex);
        tag.putString("slot", slot);
        tag.putLong("pos", pos.asLong());
        return tag;
    }

    public static PortalInfo createFromNBT(CompoundTag tag)
    {
        return new PortalInfo(
            tag.getUUID("ownerUUID"),
            tag.getInt("colorIndex"),
            tag.getString("slot"),
            BlockPos.of(tag.getLong("pos")));
    }
}
