package me.ichun.mods.portalgunclassic.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.ichun.mods.portalgunclassic.common.entity.EntityPortalProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class RenderPortalProjectile extends EntityRenderer<EntityPortalProjectile>
{
    public static final ResourceLocation TX_BLUE   = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/entity/portalball_blue.png");
    public static final ResourceLocation TX_ORANGE = ResourceLocation.fromNamespaceAndPath("portalgunclassic", "textures/entity/portalball_orange.png");

    public RenderPortalProjectile(EntityRendererProvider.Context ctx)
    {
        super(ctx);
    }

    @Override
    public void render(EntityPortalProjectile entity, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffers, int packedLight)
    {
        if (entity.age < 1) return;

        poseStack.pushPose();
        poseStack.translate(0D, 0.15D, 0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));

        ResourceLocation texture = entity.isOrange() ? TX_ORANGE : TX_BLUE;
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));
        Matrix4f mat = poseStack.last().pose();

        vc.addVertex(mat, -0.5f, -0.5f, 0f).setColor(255, 255, 255, 255)
            .setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 1, 0);
        vc.addVertex(mat,  0.5f, -0.5f, 0f).setColor(255, 255, 255, 255)
            .setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 1, 0);
        vc.addVertex(mat,  0.5f,  0.5f, 0f).setColor(255, 255, 255, 255)
            .setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 1, 0);
        vc.addVertex(mat, -0.5f,  0.5f, 0f).setColor(255, 255, 255, 255)
            .setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0, 1, 0);

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityPortalProjectile entity)
    {
        return entity.isOrange() ? TX_ORANGE : TX_BLUE;
    }
}
