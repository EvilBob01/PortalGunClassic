package me.ichun.mods.portalgunclassic.common.item;

import me.ichun.mods.portalgunclassic.common.entity.EntityPortalProjectile;
import me.ichun.mods.portalgunclassic.common.sounds.SoundRegistry;
import me.ichun.mods.portalgunclassic.common.world.PortalSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.util.FastColor;

import java.util.List;

public class ItemPortalGun extends Item
{
    public static final String NBT_COLOR   = "portalColor";
    public static final int    DEFAULT_COLOR = DyeColor.CYAN.getId();

    public ItemPortalGun()
    {
        super(new Item.Properties().stacksTo(1));
    }

    public static int getColorIndex(ItemStack stack)
    {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        return tag.contains(NBT_COLOR) ? tag.getInt(NBT_COLOR) : DEFAULT_COLOR;
    }

    public static void setColorIndex(ItemStack stack, int colorIndex)
    {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(NBT_COLOR, colorIndex & 0xF));
    }

    public static DyeColor getDyeColor(ItemStack stack)
    {
        return DyeColor.byId(getColorIndex(stack));
    }

    /** Returns the ARGB tint for item rendering */
    public static int getRenderColor(ItemStack stack)
    {
        return FastColor.ARGB32.opaque(getDyeColor(stack).getTextureDiffuseColor());
    }

    /** Returns float[3] RGB (0-1) from a DyeColor for vertex tinting */
    public static float[] dyeRGB(DyeColor dye)
    {
        int argb = dye.getTextureDiffuseColor();
        return new float[]{
            FastColor.ARGB32.red(argb)   / 255f,
            FastColor.ARGB32.green(argb) / 255f,
            FastColor.ARGB32.blue(argb)  / 255f
        };
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown())
        {
            ItemStack offhand = player.getOffhandItem();
            if (hand == InteractionHand.MAIN_HAND && offhand.getItem() instanceof DyeItem dye)
            {
                int newColor = dye.getDyeColor().getId();
                if (newColor != getColorIndex(stack))
                {
                    if (!level.isClientSide)
                    {
                        setColorIndex(stack, newColor);
                        if (!player.isCreative()) offhand.shrink(1);
                        player.displayClientMessage(
                            Component.literal("Portal Gun color set to ")
                                .append(Component.literal(dye.getDyeColor().getName())
                                    .withStyle(ChatFormatting.BOLD)), true);
                    }
                    return InteractionResultHolder.success(stack);
                }
            }
            // Sneak + right-click with no dye = reset portals for this color
            if (!level.isClientSide)
            {
                PortalSavedData data = PortalSavedData.getOrCreate(level);
                int colorIdx = getColorIndex(stack);
                data.killForPlayer(level, player.getUUID(), colorIdx, "a");
                data.killForPlayer(level, player.getUUID(), colorIdx, "b");
                level.playSound(null, player.getX(), player.getEyeY(), player.getZ(),
                    SoundRegistry.RESET.get(), SoundSource.PLAYERS, 0.3F, 1.0F);
                player.displayClientMessage(Component.literal("Portals reset"), true);
            }
            return InteractionResultHolder.success(stack);
        }

        // Right-click = fire slot B
        if (!level.isClientSide)
            firePortal(level, player, stack, "b");
        return InteractionResultHolder.success(stack);
    }

    public void fireSlotA(Level level, Player player, ItemStack stack)
    {
        if (!level.isClientSide)
            firePortal(level, player, stack, "a");
    }

    private void firePortal(Level level, Player player, ItemStack stack, String slot)
    {
        int colorIdx = getColorIndex(stack);
        level.playSound(null, player.getX(), player.getEyeY(), player.getZ(),
            SoundRegistry.FIREBLUE.get(), SoundSource.PLAYERS, 0.3F, 1.0F);
        EntityPortalProjectile proj = new EntityPortalProjectile(level, player, colorIdx, slot);
        level.addFreshEntity(proj);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        DyeColor color = getDyeColor(stack);
        tooltip.add(Component.literal("Color: ")
            .append(Component.literal(color.getName()).withStyle(ChatFormatting.BOLD)));
        tooltip.add(Component.literal("Left-click: Fire portal A").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Right-click: Fire portal B").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Sneak + Right-click: Reset portals").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Sneak + Right-click with dye: Recolor").withStyle(ChatFormatting.GRAY));
    }
}
