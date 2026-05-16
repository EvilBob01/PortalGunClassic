package me.ichun.mods.portalgunclassic.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.ichun.mods.portalgunclassic.common.tileentity.TileEntityPortal;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.joml.Matrix4f;

public class TileRendererPortal implements BlockEntityRenderer<TileEntityPortal>
{
    // Single set of textures — tinted at render time with dye color
    public static final ResourceLocation TX_BTM   = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/blocks/blue_bottom.png");
    public static final ResourceLocation TX_TOP   = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/blocks/blue_top.png");
    public static final ResourceLocation TX_FLOOR = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/blocks/blue_floor.png");

    public TileRendererPortal(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(TileEntityPortal be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (!be.setup) return;

        // Tint by color index: slot A = full brightness, slot B = darker
        DyeColor dye = DyeColor.byId(be.colorIndex);
        float[] c    = dye.getTextureDiffuseColors();
        float dim    = (be.slot != null && be.slot.equals("b")) ? 0.65f : 1.0f;
        int r = (int)(c[0] * 255 * dim);
        int g = (int)(c[1] * 255 * dim);
        int b = (int)(c[2] * 255 * dim);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(be.face.getStepY() * 90F));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-be.face.toYRot()));
        poseStack.translate(0D, 0D, -0.495D);

        ResourceLocation tex = be.face.getAxis() == Direction.Axis.Y ? TX_FLOOR
                             : be.top ? TX_TOP : TX_BTM;

        VertexConsumer vc  = buffers.getBuffer(RenderType.entityCutoutNoCull(tex));
        Matrix4f       mat = poseStack.last().pose();

        vc.addVertex(mat, -0.5f, -0.5f, 0f).setColor(r, g, b, 255).setUv(0f, 1f).setLight(15728880).setOverlay(combinedOverlay).setNormal(0, 0, 1);
        vc.addVertex(mat,  0.5f, -0.5f, 0f).setColor(r, g, b, 255).setUv(1f, 1f).setLight(15728880).setOverlay(combinedOverlay).setNormal(0, 0, 1);
        vc.addVertex(mat,  0.5f,  0.5f, 0f).setColor(r, g, b, 255).setUv(1f, 0f).setLight(15728880).setOverlay(combinedOverlay).setNormal(0, 0, 1);
        vc.addVertex(mat, -0.5f,  0.5f, 0f).setColor(r, g, b, 255).setUv(0f, 0f).setLight(15728880).setOverlay(combinedOverlay).setNormal(0, 0, 1);

        poseStack.popPose();
    }
}
