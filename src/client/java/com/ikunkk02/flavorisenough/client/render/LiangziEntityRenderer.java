package com.ikunkk02.flavorisenough.client.render;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import com.ikunkk02.flavorisenough.entity.LiangziEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class LiangziEntityRenderer extends EntityRenderer<LiangziEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            FlavorIsEnoughMod.MOD_ID, "textures/entity/liangzi.png");

    private static final float HALF_WIDTH = 0.7F;

    public LiangziEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.4F;
    }

    @Override
    public void render(LiangziEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Billboard: always face the camera
        poseStack.pushPose();

        // Position at entity center height
        poseStack.translate(0.0D, entity.getBbHeight() * 0.65D, 0.0D);

        // Billboard rotation — always face camera
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        // Render the textured quad
        RenderType renderType = RenderType.entityCutoutNoCull(TEXTURE);
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float halfW = HALF_WIDTH;
        float halfH = entity.getBbHeight() * 0.55F;

        // Quad: front face only, always facing camera after billboard rotation
        // Z=0 faces the camera
        vertexConsumer.addVertex(matrix, -halfW, -halfH, 0.0F)
                .setColor(0xFFFFFFFF)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(matrix, -halfW, halfH, 0.0F)
                .setColor(0xFFFFFFFF)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(matrix, halfW, halfH, 0.0F)
                .setColor(0xFFFFFFFF)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(matrix, halfW, -halfH, 0.0F)
                .setColor(0xFFFFFFFF)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, 1.0F);

        poseStack.popPose();

        // Render name tag
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(LiangziEntity entity) {
        return TEXTURE;
    }
}
