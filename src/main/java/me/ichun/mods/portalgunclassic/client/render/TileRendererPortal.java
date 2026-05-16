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
import org.joml.Matrix4f;

public class TileRendererPortal implements BlockEntityRenderer<TileEntityPortal>
{
    public static final ResourceLocation TX_BLUE_BTM   = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/blocks/blue_bottom.png");
    public static final ResourceLocation TX_BLUE_TOP   = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/blocks/blue_top.png");
    public static final ResourceLocation TX_BLUE_Y     = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/blocks/blue_floor.png");
    public static final ResourceLocation TX_ORANGE_BTM = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/blocks/orange_bottom.png");
    public static final ResourceLocation TX_ORANGE_TOP = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/blocks/orange_top.png");
    public static final ResourceLocation TX_ORANGE_Y   = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/blocks/orange_floor.png");

    public TileRendererPortal(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(TileEntityPortal be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (!be.setup) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);

        // Tilt for floor/ceiling portals, then rotate for wall direction
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(be.face.getStepY() * 90F));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-be.face.toYRot()));
        poseStack.translate(0D, 0D, -0.495D);

        ResourceLocation tex;
        if (be.face.getAxis() == Direction.Axis.Y)
        {
            tex = be.orange ? TX_ORANGE_Y : TX_BLUE_Y;
        }
        else if (be.top)
        {
            tex = be.orange ? TX_ORANGE_TOP : TX_BLUE_TOP;
        }
        else
        {
            tex = be.orange ? TX_ORANGE_BTM : TX_BLUE_BTM;
        }

        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(tex));
        Matrix4f mat = poseStack.last().pose();

        vc.addVertex(mat, -0.5f, -0.5f, 0f).setColor(255, 255, 255, 255).setUv(0f, 1f).setLight(15728880).setOverlay(combinedOverlay).setNormal(0, 0, 1);
        vc.addVertex(mat,  0.5f, -0.5f, 0f).setColor(255, 255, 255, 255).setUv(1f, 1f).setLight(15728880).setOverlay(combinedOverlay).setNormal(0, 0, 1);
        vc.addVertex(mat,  0.5f,  0.5f, 0f).setColor(255, 255, 255, 255).setUv(1f, 0f).setLight(15728880).setOverlay(combinedOverlay).setNormal(0, 0, 1);
        vc.addVertex(mat, -0.5f,  0.5f, 0f).setColor(255, 255, 255, 255).setUv(0f, 0f).setLight(15728880).setOverlay(combinedOverlay).setNormal(0, 0, 1);

        poseStack.popPose();
    }
}
