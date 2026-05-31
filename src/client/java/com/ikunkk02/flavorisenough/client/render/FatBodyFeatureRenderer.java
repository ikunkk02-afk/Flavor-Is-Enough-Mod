package com.ikunkk02.flavorisenough.client.render;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import com.ikunkk02.flavorisenough.appearance.FatBodyRenderProfile;
import com.ikunkk02.flavorisenough.client.FlavorClientConfig;
import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public final class FatBodyFeatureRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	private static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
			FlavorIsEnoughMod.MOD_ID,
			"textures/entity/fat_body_layer.png");

	private final FatBodyModel model;

	public FatBodyFeatureRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, EntityRendererProvider.Context context, boolean slim) {
		super(parent);
		this.model = new FatBodyModel(context.bakeLayer(slim ? FatBodyModel.SLIM_LAYER : FatBodyModel.DEFAULT_LAYER));
	}

	public static void registerModelLayers() {
		EntityModelLayerRegistry.registerModelLayer(FatBodyModel.DEFAULT_LAYER, FatBodyModel::createDefaultLayer);
		EntityModelLayerRegistry.registerModelLayer(FatBodyModel.SLIM_LAYER, FatBodyModel::createSlimLayer);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!FlavorClientConfig.get().isFatBodyLayerEnabled() || player.isSpectator() || player.isInvisible()) {
			return;
		}

		FlavorPlayerComponent component = getComponent(player);
		if (component == null) {
			return;
		}

		FatBodyRenderProfile profile = FatBodyRenderProfile.forObesityValue(component.getObesityValue());
		if (!profile.rendersLayer()) {
			return;
		}

		model.copyPoseFrom(getParentModel());
		ResourceLocation skinTexture = resolvePlayerSkinTexture(player);
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(skinTexture));
		model.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, profile);
	}

	private static ResourceLocation resolvePlayerSkinTexture(AbstractClientPlayer player) {
		try {
			ResourceLocation skinTexture = player.getSkin().texture();
			if (skinTexture != null) {
				return skinTexture;
			}
		} catch (Exception exception) {
			// Keep the layer renderable if a skin lookup is unavailable during client startup.
		}
		return FALLBACK_TEXTURE;
	}

	private static FlavorPlayerComponent getComponent(AbstractClientPlayer player) {
		try {
			return ModEntityComponents.FLAVOR_PLAYER.get(player);
		} catch (Exception exception) {
			return null;
		}
	}
}
