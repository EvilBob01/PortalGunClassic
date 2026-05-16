package me.ichun.mods.portalgunclassic.common.tileentity;

import me.ichun.mods.portalgunclassic.client.ClientState;
import me.ichun.mods.portalgunclassic.common.core.ModRegistries;
import me.ichun.mods.portalgunclassic.common.packet.PacketEntityLocation;
import me.ichun.mods.portalgunclassic.common.packet.PacketRequestTeleport;
import me.ichun.mods.portalgunclassic.common.portal.PortalInfo;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import me.ichun.mods.portalgunclassic.common.world.PortalSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

public class TileEntityPortal extends BlockEntity
{
    public boolean setup;
    public boolean top;
    public int     colorIndex;
    public String  slot;      // "a" or "b"
    public UUID    ownerUUID;
    public Direction face;

    public TileEntityPortal(BlockPos pos, BlockState state)
    {
        super(ModRegistries.TILE_PORTAL.get(), pos, state);
        top        = false;
        colorIndex = 0;
        slot       = "a";
        face       = Direction.DOWN;
    }

    public void tick(Level level, BlockPos pos, BlockState state)
    {
        if (top || ownerUUID == null) return;

        BlockPos pairLocation = null;

        if (!level.isClientSide)
        {
            PortalSavedData data = PortalSavedData.getOrCreate(level);
            PortalInfo pair = data.getPair(ownerUUID, colorIndex, slot);
            if (pair == null) return;
            pairLocation = pair.pos;
        }
        else
        {
            // Client side: check our local status map
            int bits = ClientState.getPortalBits(colorIndex);
            String pairSlot = slot.equals("a") ? "b" : "a";
            boolean pairActive = slot.equals("a") ? (bits & 2) != 0 : (bits & 1) != 0;
            if (!pairActive) return;
        }

        int extY = face.getAxis() != Direction.Axis.Y ? 2 : 1;
        AABB aabbScan = new AABB(pos.getX(), pos.getY(), pos.getZ(),
            pos.getX() + 1, pos.getY() + extY, pos.getZ() + 1)
            .expandTowards(face.getStepX() * 4D, face.getStepY() * 4D, face.getStepZ() * 4D);

        AABB aabbInside = new AABB(pos.getX(), pos.getY(), pos.getZ(),
            pos.getX() + 1, pos.getY() + extY, pos.getZ() + 1)
            .expandTowards(face.getStepX() * 9D, face.getStepY() * 9D, face.getStepZ() * 9D)
            .move(-face.getStepX() * 9.999D, -face.getStepY() * 9.999D, -face.getStepZ() * 9.999D);

        List<? extends Entity> ents = level.isClientSide
            ? level.getEntitiesOfClass(Player.class, aabbScan)
            : level.getEntitiesOfClass(Entity.class, aabbScan);

        for (Entity ent : ents)
        {
            if (!level.isClientSide && ent instanceof Player) continue;

            if (ent.getBoundingBox().move(ent.getDeltaMovement()).intersects(aabbInside))
            {
                if (level.isClientSide)
                    handleClientTeleport((Player) ent);
                else
                {
                    BlockEntity te = level.getBlockEntity(pairLocation);
                    if (te instanceof TileEntityPortal pair)
                        teleport(level, ent, pair);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleClientTeleport(Player player)
    {
        if (ClientState.teleportCooldown <= 0 && player == Minecraft.getInstance().player)
        {
            ClientState.teleportCooldown = 3;
            PacketDistributor.sendToServer(new PacketRequestTeleport(worldPosition));
        }
    }

    public void teleport(Level level, Entity ent, TileEntityPortal pair)
    {
        Vec3 size = new Vec3(ent.getBoundingBox().getXsize(),
                             ent.getBoundingBox().getYsize(),
                             ent.getBoundingBox().getZsize());

        double px = pair.getBlockPos().getX() + 0.5D - (0.5D - size.x / 2D) * 0.99D * pair.face.getStepX();
        double pz = pair.getBlockPos().getZ() + 0.5D - (0.5D - size.z / 2D) * 0.99D * pair.face.getStepZ();
        double py = pair.getBlockPos().getY() + (pair.face.getStepY() < 0 ? -size.y + 0.999D : 0.001D);
        ent.setPos(px, py, pz);

        Vec3 motion = ent.getDeltaMovement();
        double mX = motion.x, mY = motion.y, mZ = motion.z;

        if (face.getAxis() != Direction.Axis.Y && pair.face.getAxis() != Direction.Axis.Y)
        {
            float yawDiff = face.toYRot() - pair.face.getOpposite().toYRot();
            ent.setYRot(ent.getYRot() - yawDiff);
            ent.yRotO = ent.yRotO - yawDiff;
            if (pair.face == face) { mX = -mX; mZ = -mZ; }
            else if (face.getAxis() == Direction.Axis.X)
            {
                if (pair.face == Direction.NORTH)      { double t = mX; mZ = -t * -face.getStepX(); mX = mZ * -face.getStepX(); }
                else if (pair.face == Direction.SOUTH) { double t = mX; mZ =  t * -face.getStepX(); mX = -mZ * -face.getStepX(); }
            }
            else if (face.getAxis() == Direction.Axis.Z)
            {
                if (pair.face == Direction.EAST)      { double t = mX; mZ = -t * -face.getStepZ(); mX = mZ * -face.getStepZ(); }
                else if (pair.face == Direction.WEST) { double t = mX; mZ =  t * -face.getStepZ(); mX = -mZ * -face.getStepZ(); }
            }
        }
        else if (face.getAxis() == Direction.Axis.Y && pair.face.getAxis() != Direction.Axis.Y)
        {
            ent.setXRot(0F); ent.setYRot(pair.face.toYRot());
            mX = Math.abs(mY) * pair.face.getStepX();
            mZ = Math.abs(mY) * pair.face.getStepZ();
            mY = 0D; ent.fallDistance = 0F;
        }
        else if (face.getAxis() != Direction.Axis.Y && pair.face.getAxis() == Direction.Axis.Y)
        {
            mY = Math.sqrt(mX * mX + mZ * mZ) * pair.face.getStepY();
        }
        else
        {
            if (pair.face == face) mY = -mY;
            ent.fallDistance = 0F;
        }

        mX += pair.face.getStepX() * 0.2D;
        mY += pair.face.getStepY() * 0.2D;
        mZ += pair.face.getStepZ() * 0.2D;
        ent.setDeltaMovement(mX, mY, mZ);
        ent.moveTo(ent.getX(), ent.getY(), ent.getZ(), ent.getYRot(), ent.getXRot());

        double entCenterY  = worldPosition.getY()          + (face.getAxis()      != Direction.Axis.Y ? 1D : 0.5D);
        double pairCenterY = pair.getBlockPos().getY()      + (pair.face.getAxis() != Direction.Axis.Y ? 1D : 0.5D);

        level.playSound(null, worldPosition.getX() + 0.5D, entCenterY,          worldPosition.getZ() + 0.5D, SoundRegistry.ENTER.get(), SoundSource.BLOCKS, 0.1F, 1.0F);
        level.playSound(null, pair.getBlockPos().getX() + 0.5D, pairCenterY, pair.getBlockPos().getZ() + 0.5D, SoundRegistry.EXIT.get(),  SoundSource.BLOCKS, 0.1F, 1.0F);

        PacketDistributor.sendToPlayersNear(
            (net.minecraft.server.level.ServerLevel) level, null,
            worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D,
            256D, new PacketEntityLocation(ent));
    }

    public void setup(boolean top, UUID ownerUUID, int colorIndex, String slot, Direction face)
    {
        this.setup      = true;
        this.top        = top;
        this.ownerUUID  = ownerUUID;
        this.colorIndex = colorIndex;
        this.slot       = slot;
        this.face       = face;
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) { loadAdditional(tag, registries); }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putBoolean("setup", setup);
        tag.putBoolean("top", top);
        tag.putInt("colorIndex", colorIndex);
        tag.putString("slot", slot != null ? slot : "a");
        if (ownerUUID != null) tag.putUUID("ownerUUID", ownerUUID);
        tag.putInt("face", face.get3DDataValue());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        setup      = tag.getBoolean("setup");
        top        = tag.getBoolean("top");
        colorIndex = tag.getInt("colorIndex");
        slot       = tag.getString("slot");
        if (slot.isEmpty()) slot = "a";
        ownerUUID  = tag.contains("ownerUUID") ? tag.getUUID("ownerUUID") : null;
        face       = Direction.from3DDataValue(tag.getInt("face"));
    }
}
