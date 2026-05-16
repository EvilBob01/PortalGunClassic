package me.ichun.mods.portalgunclassic.common.block;

import com.mojang.serialization.MapCodec;
import me.ichun.mods.portalgunclassic.common.core.ModRegistries;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import me.ichun.mods.portalgunclassic.common.world.PortalSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockPortal extends BaseEntityBlock
{
    public static final MapCodec<BlockPortal> CODEC = simpleCodec(BlockPortal::new);

    public BlockPortal(BlockBehaviour.Properties props)
    {
        super(props);
    }

    @Override
    protected MapCodec<BlockPortal> codec()
    {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new TileEntityPortal(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (type == ModRegistries.TILE_PORTAL.get())
        {
            return (lvl, pos, st, be) -> ((TileEntityPortal) be).tick(lvl, pos, st);
        }
        return null;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TileEntityPortal portal)
        {
            if (portal.setup)
            {
                if (portal.face.getAxis() == Direction.Axis.Y)
                {
                    BlockPos behind = pos.relative(portal.face.getOpposite());
                    if (!world.getBlockState(behind).isFaceSturdy(world, behind, portal.face))
                    {
                        PortalSavedData.getOrCreate(world).kill(world, portal.orange);
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
                else
                {
                    BlockPos other = portal.top ? pos.below() : pos.above();
                    BlockPos behind     = pos.relative(portal.face.getOpposite());
                    BlockPos otherBehind = other.relative(portal.face.getOpposite());
                    if (!(world.getBlockState(behind).isFaceSturdy(world, behind, portal.face)
                        && world.getBlockState(otherBehind).isFaceSturdy(world, otherBehind, portal.face))
                        || world.getBlockState(other).getBlock() != this)
                    {
                        PortalSavedData.getOrCreate(world).kill(world, portal.orange);
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        else
        {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public static boolean canPlace(Level world, BlockPos pos, Direction sideHit, boolean isOrange)
    {
        BlockState state = world.getBlockState(pos);
        BlockEntity te = world.getBlockEntity(pos);
        boolean replaceable = state.isAir() || (te instanceof TileEntityPortal portal && portal.setup && portal.orange == isOrange);

        if (replaceable)
        {
            if (sideHit.getAxis() == Direction.Axis.Y)
            {
                BlockPos behind = pos.relative(sideHit.getOpposite());
                return world.getBlockState(behind).isFaceSturdy(world, behind, sideHit);
            }
            else
            {
                BlockPos posDown  = pos.below();
                BlockState downState = world.getBlockState(posDown);
                BlockEntity downTe   = world.getBlockEntity(posDown);
                boolean downReplaceable = downState.isAir() || (downTe instanceof TileEntityPortal dp && dp.setup && dp.orange == isOrange);

                BlockPos behind     = pos.relative(sideHit.getOpposite());
                BlockPos downBehind = posDown.relative(sideHit.getOpposite());
                return world.getBlockState(behind).isFaceSturdy(world, behind, sideHit)
                    && downReplaceable
                    && world.getBlockState(downBehind).isFaceSturdy(world, downBehind, sideHit);
            }
        }
        return false;
    }
}
