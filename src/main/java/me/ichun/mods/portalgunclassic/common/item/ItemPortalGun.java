package me.ichun.mods.portalgunclassic.common.item;

import me.ichun.mods.portalgunclassic.common.core.ModRegistries;
import me.ichun.mods.portalgunclassic.common.entity.EntityPortalProjectile;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemPortalGun extends Item
{
    public final boolean isOrange;

    public ItemPortalGun(boolean isOrange)
    {
        super(new Item.Properties().stacksTo(1));
        this.isOrange = isOrange;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        if (!level.isClientSide)
        {
            level.playSound(null, player.getX(), player.getEyeY(), player.getZ(),
                isOrange ? SoundRegistry.FIRERED.get() : SoundRegistry.FIREBLUE.get(),
                SoundSource.PLAYERS, 0.3F, 1.0F);

            EntityPortalProjectile proj = new EntityPortalProjectile(level, player, isOrange);
            level.addFreshEntity(proj);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
