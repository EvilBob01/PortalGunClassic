package me.ichun.mods.portalgunclassic.common.core;

import me.ichun.mods.portalgunclassic.common.PortalGunClassic;
import me.ichun.mods.portalgunclassic.common.block.BlockPortal;
import me.ichun.mods.portalgunclassic.common.entity.EntityPortalProjectile;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalCore;
import me.ichun.mods.portalgunclassic.common.item.ItemPortalGun;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRegistries
{
    public static final DeferredRegister.Blocks BLOCKS        = DeferredRegister.createBlocks(PortalGunClassic.MOD_ID);
    public static final DeferredRegister.Items  ITEMS         = DeferredRegister.createItems(PortalGunClassic.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PortalGunClassic.MOD_ID);
    public static final DeferredRegister<EntityType<?>>      ENTITIES       = DeferredRegister.create(Registries.ENTITY_TYPE, PortalGunClassic.MOD_ID);

    public static final DeferredBlock<BlockPortal> BLOCK_PORTAL = BLOCKS.register("portal",
        () -> new BlockPortal(BlockBehaviour.Properties.of()
            .strength(-1F, 3600000F)
            .lightLevel(state -> 7)
            .noCollission()
            .noOcclusion()));

    // Single portal gun item — color stored as NBT, default cyan
    public static final DeferredItem<ItemPortalGun>  ITEM_PORTAL_GUN  = ITEMS.register("portalgun",    ItemPortalGun::new);
    public static final DeferredItem<ItemPortalCore> ITEM_PORTAL_CORE = ITEMS.register("portal_core",  ItemPortalCore::new);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityPortal>> TILE_PORTAL =
        BLOCK_ENTITIES.register("tile_portal",
            () -> BlockEntityType.Builder.of(TileEntityPortal::new, BLOCK_PORTAL.get()).build(null));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityPortalProjectile>> ENTITY_PORTAL_PROJECTILE =
        ENTITIES.register("portal_projectile",
            () -> EntityType.Builder.<EntityPortalProjectile>of(EntityPortalProjectile::new, MobCategory.MISC)
                .sized(0.3F, 0.3F)
                .clientTrackingRange(16)
                .updateInterval(1)
                .build("portalgunclassic:portal_projectile"));

    public static void register(IEventBus modBus)
    {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        ENTITIES.register(modBus);
        SoundRegistry.register(modBus);
    }
}
