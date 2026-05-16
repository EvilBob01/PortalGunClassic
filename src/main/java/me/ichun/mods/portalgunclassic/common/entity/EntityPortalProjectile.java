package me.ichun.mods.portalgunclassic.common.entity;

import me.ichun.mods.portalgunclassic.common.block.BlockPortal;
import me.ichun.mods.portalgunclassic.common.core.ModRegistries;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import me.ichun.mods.portalgunclassic.common.world.PortalSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityPortalProjectile extends Entity
{
    private static final EntityDataAccessor<Boolean> ORANGE =
        SynchedEntityData.defineId(EntityPortalProjectile.class, EntityDataSerializers.BOOLEAN);

    public int age = 0;

    public EntityPortalProjectile(EntityType<EntityPortalProjectile> type, Level level)
    {
        super(type, level);
        setInvulnerable(true);
    }

    public EntityPortalProjectile(Level level, Entity shooter, boolean isOrange)
    {
        this(ModRegistries.ENTITY_PORTAL_PROJECTILE.get(), level);
        this.entityData.set(ORANGE, isOrange);
        shoot(shooter, 4.999F);
        setPos(shooter.getX(), shooter.getEyeY() - (getBbWidth() / 2F), shooter.getZ());
        setYRot(shooter.getYRot());
        setXRot(shooter.getXRot());
    }

    public void setOrange(boolean flag)
    {
        entityData.set(ORANGE, flag);
    }

    public boolean isOrange()
    {
        return entityData.get(ORANGE);
    }

    public void shoot(Entity entity, float velocity)
    {
        float f  = -Mth.sin(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);
        float f1 = -Mth.sin(entity.getXRot() * 0.017453292F);
        float f2 =  Mth.cos(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);
        shoot(f, f1, f2, velocity);
        Vec3 em = entity.getDeltaMovement();
        setDeltaMovement(getDeltaMovement().x + em.x, getDeltaMovement().y + (entity.onGround() ? em.y : 0), getDeltaMovement().z + em.z);
    }

    public void shoot(double x, double y, double z, float velocity)
    {
        float f = Mth.sqrt((float)(x * x + y * y + z * z));
        x = x / f * velocity;
        y = y / f * velocity;
        z = z / f * velocity;
        setDeltaMovement(x, y, z);
        float horiz = Mth.sqrt((float)(x * x + z * z));
        setYRot((float)(Mth.atan2(x, z) * (180D / Math.PI)));
        setXRot((float)(Mth.atan2(y, horiz) * (180D / Math.PI)));
        yRotO = getYRot();
        xRotO = getXRot();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        builder.define(ORANGE, false);
    }

    @Override
    public void tick()
    {
        if (getY() > level().getMaxBuildHeight() * 2 || getY() < -level().getMaxBuildHeight() || age > 1200)
        {
            discard();
            return;
        }

        age++;

        xOld = getX();
        yOld = getY();
        zOld = getZ();

        super.tick();

        Vec3 from = position();
        Vec3 motion = getDeltaMovement();
        Vec3 to = from.add(motion);

        if (!Double.isNaN(from.x) && !Double.isNaN(from.y) && !Double.isNaN(from.z)
            && !Double.isNaN(to.x) && !Double.isNaN(to.y) && !Double.isNaN(to.z))
        {
            int ti = Mth.floor(to.x), tj = Mth.floor(to.y), tk = Mth.floor(to.z);
            int fi = Mth.floor(from.x), fj = Mth.floor(from.y), fk = Mth.floor(from.z);

            int steps = 200;
            Vec3 cur = from;

            while (steps-- >= 0)
            {
                if (Double.isNaN(cur.x) || Double.isNaN(cur.y) || Double.isNaN(cur.z)
                    || (fi == ti && fj == tj && fk == tk))
                    break;

                boolean useX = true, useY = true, useZ = true;
                double bx = 999, by = 999, bz = 999;

                if      (ti > fi) bx = fi + 1.0;
                else if (ti < fi) bx = fi + 0.0;
                else              useX = false;

                if      (tj > fj) by = fj + 1.0;
                else if (tj < fj) by = fj + 0.0;
                else              useY = false;

                if      (tk > fk) bz = fk + 1.0;
                else if (tk < fk) bz = fk + 0.0;
                else              useZ = false;

                double dx = to.x - cur.x, dy = to.y - cur.y, dz = to.z - cur.z;
                double tx = useX ? (bx - cur.x) / dx : 999;
                double ty = useY ? (by - cur.y) / dy : 999;
                double tz = useZ ? (bz - cur.z) / dz : 999;

                if (tx == -0D) tx = -1E-4;
                if (ty == -0D) ty = -1E-4;
                if (tz == -0D) tz = -1E-4;

                Direction face;
                if (tx < ty && tx < tz)
                {
                    face = ti > fi ? Direction.WEST : Direction.EAST;
                    cur = new Vec3(bx, cur.y + dy * tx, cur.z + dz * tx);
                }
                else if (ty < tz)
                {
                    face = tj > fj ? Direction.DOWN : Direction.UP;
                    cur = new Vec3(cur.x + dx * ty, by, cur.z + dz * ty);
                }
                else
                {
                    face = tk > fk ? Direction.NORTH : Direction.SOUTH;
                    cur = new Vec3(cur.x + dx * tz, cur.y + dy * tz, bz);
                }

                fi = Mth.floor(cur.x) - (face == Direction.EAST  ? 1 : 0);
                fj = Mth.floor(cur.y) - (face == Direction.UP    ? 1 : 0);
                fk = Mth.floor(cur.z) - (face == Direction.SOUTH ? 1 : 0);

                BlockPos blockPos = new BlockPos(fi, fj, fk);
                BlockState bs = level().getBlockState(blockPos);

                VoxelShape shape = bs.getCollisionShape(level(), blockPos);
                if (!shape.isEmpty())
                {
                    BlockHitResult hit = shape.clip(cur, to, blockPos);
                    if (hit != null)
                    {
                        if (bs.getBlock() == Blocks.IRON_BARS)
                        {
                            cur = new Vec3(hit.getLocation().x + motion.x / 5000D,
                                           hit.getLocation().y + motion.y / 5000D,
                                           hit.getLocation().z + motion.z / 5000D);
                        }
                        else
                        {
                            createPortal(hit);
                            discard();
                            return;
                        }
                    }
                }
            }
        }

        Vec3 m = getDeltaMovement();
        setPos(getX() + m.x, getY() + m.y, getZ() + m.z);

        float horiz = Mth.sqrt((float)(m.x * m.x + m.z * m.z));
        float newYaw   = (float)(Mth.atan2(m.x, m.z) * (180D / Math.PI));
        float newPitch = (float)(Mth.atan2(m.y, horiz) * (180D / Math.PI));

        while (newPitch - xRotO < -180F) xRotO -= 360F;
        while (newPitch - xRotO >= 180F) xRotO += 360F;
        while (newYaw  - yRotO < -180F) yRotO -= 360F;
        while (newYaw  - yRotO >= 180F) yRotO += 360F;

        setXRot(xRotO + (newPitch - xRotO) * 0.2F);
        setYRot(yRotO + (newYaw   - yRotO) * 0.2F);
    }

    public void createPortal(BlockHitResult hit)
    {
        if (!level().isClientSide)
        {
            BlockPos pos = hit.getBlockPos().relative(hit.getDirection());
            if (BlockPortal.canPlace(level(), pos, hit.getDirection(), isOrange()))
            {
                PortalSavedData.getOrCreate(level()).kill(level(), isOrange());

                level().setBlock(pos, ModRegistries.BLOCK_PORTAL.get().defaultBlockState(), 3);
                BlockEntity te = level().getBlockEntity(pos);
                if (te instanceof TileEntityPortal portal)
                {
                    portal.setup(hit.getDirection().getAxis() != Direction.Axis.Y, isOrange(), hit.getDirection());
                }

                if (hit.getDirection().getAxis() != Direction.Axis.Y)
                {
                    BlockPos posDown = pos.below();
                    level().setBlock(posDown, ModRegistries.BLOCK_PORTAL.get().defaultBlockState(), 3);
                    te = level().getBlockEntity(posDown);
                    if (te instanceof TileEntityPortal portal)
                    {
                        portal.setup(false, isOrange(), hit.getDirection());
                    }
                }

                BlockPos savePos = hit.getDirection().getAxis() != Direction.Axis.Y ? pos.below() : pos;
                PortalSavedData.getOrCreate(level()).set(level(), isOrange(), savePos);

                level().playSound(null, getX(), getY() + getBbHeight() / 2F, getZ(),
                    isOrange() ? SoundRegistry.OPENRED.get() : SoundRegistry.OPENBLUE.get(),
                    SoundSource.BLOCKS, 0.3F, 1.0F);
            }
            else
            {
                level().playSound(null, getX(), getY() + getBbHeight() / 2F, getZ(),
                    SoundRegistry.INVALID.get(), SoundSource.NEUTRAL, 0.5F, 1.0F);
            }
        }
    }

    public boolean shouldRender(double distance)
    {
        double d = getBoundingBox().getSize() * 10D;
        if (Double.isNaN(d)) d = 1.0;
        d = d * 64.0 * getViewScale();
        return distance < d * d;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        setOrange(tag.getBoolean("orange"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        tag.putBoolean("orange", isOrange());
    }
}
