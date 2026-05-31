package com.ikunkk02.flavorisenough.client.render;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import com.ikunkk02.flavorisenough.appearance.FatBodyRenderProfile;
import com.ikunkk02.flavorisenough.appearance.FatBodySkinUv;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

public final class FatBodyModel {
	public static final ModelLayerLocation DEFAULT_LAYER = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "fat_body_layer"),
			"default");
	public static final ModelLayerLocation SLIM_LAYER = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "fat_body_layer"),
			"slim");

	private static final String BELLY_SMALL = "belly_small";
	private static final String BELLY_MEDIUM = "belly_medium";
	private static final String BELLY_LARGE = "belly_large";
	private static final String BELLY_HUGE = "belly_huge";
	private static final String ARM_PADDING_RIGHT = "arm_padding_right";
	private static final String ARM_PADDING_LEFT = "arm_padding_left";
	private static final String LEG_PADDING_RIGHT = "leg_padding_right";
	private static final String LEG_PADDING_LEFT = "leg_padding_left";

	private final ModelPart bellySmall;
	private final ModelPart bellyMedium;
	private final ModelPart bellyLarge;
	private final ModelPart bellyHuge;
	private final ModelPart armPaddingRight;
	private final ModelPart armPaddingLeft;
	private final ModelPart legPaddingRight;
	private final ModelPart legPaddingLeft;

	public FatBodyModel(ModelPart root) {
		this.bellySmall = root.getChild(BELLY_SMALL);
		this.bellyMedium = root.getChild(BELLY_MEDIUM);
		this.bellyLarge = root.getChild(BELLY_LARGE);
		this.bellyHuge = root.getChild(BELLY_HUGE);
		this.armPaddingRight = root.getChild(ARM_PADDING_RIGHT);
		this.armPaddingLeft = root.getChild(ARM_PADDING_LEFT);
		this.legPaddingRight = root.getChild(LEG_PADDING_RIGHT);
		this.legPaddingLeft = root.getChild(LEG_PADDING_LEFT);
	}

	public static LayerDefinition createDefaultLayer() {
		return createBodyLayer(false);
	}

	public static LayerDefinition createSlimLayer() {
		return createBodyLayer(true);
	}

	public void copyPoseFrom(PlayerModel<AbstractClientPlayer> playerModel) {
		copyBodyPose(playerModel, bellySmall);
		copyBodyPose(playerModel, bellyMedium);
		copyBodyPose(playerModel, bellyLarge);
		copyBodyPose(playerModel, bellyHuge);
		armPaddingRight.copyFrom(playerModel.rightArm);
		armPaddingLeft.copyFrom(playerModel.leftArm);
		legPaddingRight.copyFrom(playerModel.rightLeg);
		legPaddingLeft.copyFrom(playerModel.leftLeg);
	}

	public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, FatBodyRenderProfile profile) {
		if (!profile.rendersLayer()) {
			return;
		}

		getBellyPart(profile.stageId()).render(poseStack, vertexConsumer, packedLight, packedOverlay);

		if (profile.rendersLimbs()) {
			armPaddingRight.render(poseStack, vertexConsumer, packedLight, packedOverlay);
			armPaddingLeft.render(poseStack, vertexConsumer, packedLight, packedOverlay);
			legPaddingRight.render(poseStack, vertexConsumer, packedLight, packedOverlay);
			legPaddingLeft.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		}
	}

	private static LayerDefinition createBodyLayer(boolean slim) {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition root = meshDefinition.getRoot();

		addBellySmall(root);
		addBellyMedium(root);
		addBellyLarge(root);
		addBellyHuge(root);
		addArmPadding(root, slim);
		addLegPadding(root);

		return LayerDefinition.create(meshDefinition, 64, 64);
	}

	private static void addBellySmall(PartDefinition root) {
		root.addOrReplaceChild(BELLY_SMALL,
				CubeListBuilder.create()
						.texOffs(FatBodySkinUv.BELLY_SMALL_CENTER.u(), FatBodySkinUv.BELLY_SMALL_CENTER.v())
						.addBox(-3.0F, 3.0F, -4.0F, 6.0F, 7.0F, 2.0F)
						.texOffs(FatBodySkinUv.BELLY_SMALL_LOWER.u(), FatBodySkinUv.BELLY_SMALL_LOWER.v())
						.addBox(-2.4F, 8.2F, -3.6F, 4.8F, 3.3F, 1.6F),
				PartPose.ZERO);
	}

	private static void addBellyMedium(PartDefinition root) {
		root.addOrReplaceChild(BELLY_MEDIUM,
				CubeListBuilder.create()
						.texOffs(FatBodySkinUv.BELLY_MEDIUM_CENTER.u(), FatBodySkinUv.BELLY_MEDIUM_CENTER.v())
						.addBox(-3.8F, 2.6F, -4.7F, 7.6F, 8.4F, 2.7F)
						.texOffs(FatBodySkinUv.BELLY_MEDIUM_LOWER.u(), FatBodySkinUv.BELLY_MEDIUM_LOWER.v())
						.addBox(-3.2F, 8.8F, -4.2F, 6.4F, 3.4F, 2.2F)
						.texOffs(FatBodySkinUv.BELLY_MEDIUM_TOP.u(), FatBodySkinUv.BELLY_MEDIUM_TOP.v())
						.addBox(-2.8F, 1.7F, -4.0F, 5.6F, 2.1F, 2.0F),
				PartPose.ZERO);
	}

	private static void addBellyLarge(PartDefinition root) {
		root.addOrReplaceChild(BELLY_LARGE,
				CubeListBuilder.create()
						.texOffs(FatBodySkinUv.BELLY_LARGE_CENTER.u(), FatBodySkinUv.BELLY_LARGE_CENTER.v())
						.addBox(-4.4F, 2.4F, -5.3F, 8.8F, 9.2F, 3.3F)
						.texOffs(FatBodySkinUv.BELLY_LARGE_LOWER.u(), FatBodySkinUv.BELLY_LARGE_LOWER.v())
						.addBox(-3.8F, 9.5F, -4.8F, 7.6F, 3.6F, 2.8F)
						.texOffs(FatBodySkinUv.BELLY_LARGE_TOP.u(), FatBodySkinUv.BELLY_LARGE_TOP.v())
						.addBox(-3.3F, 1.4F, -4.5F, 6.6F, 1.9F, 2.5F)
						.texOffs(FatBodySkinUv.BELLY_LARGE_FRONT.u(), FatBodySkinUv.BELLY_LARGE_FRONT.v())
						.addBox(-3.1F, 4.5F, -5.8F, 6.2F, 5.1F, 0.7F),
				PartPose.ZERO);
	}

	private static void addBellyHuge(PartDefinition root) {
		root.addOrReplaceChild(BELLY_HUGE,
				CubeListBuilder.create()
						.texOffs(FatBodySkinUv.BELLY_HUGE_CENTER.u(), FatBodySkinUv.BELLY_HUGE_CENTER.v())
						.addBox(-4.8F, 2.2F, -5.9F, 9.6F, 10.0F, 3.9F)
						.texOffs(FatBodySkinUv.BELLY_HUGE_LOWER.u(), FatBodySkinUv.BELLY_HUGE_LOWER.v())
						.addBox(-4.2F, 9.8F, -5.3F, 8.4F, 4.2F, 3.3F)
						.texOffs(FatBodySkinUv.BELLY_HUGE_TOP.u(), FatBodySkinUv.BELLY_HUGE_TOP.v())
						.addBox(-3.6F, 1.2F, -4.9F, 7.2F, 1.9F, 2.9F)
						.texOffs(FatBodySkinUv.BELLY_HUGE_FRONT.u(), FatBodySkinUv.BELLY_HUGE_FRONT.v())
						.addBox(-3.4F, 4.0F, -6.4F, 6.8F, 6.8F, 0.8F)
						.texOffs(FatBodySkinUv.BELLY_HUGE_LOW_FRONT.u(), FatBodySkinUv.BELLY_HUGE_LOW_FRONT.v())
						.addBox(-2.7F, 9.9F, -6.0F, 5.4F, 3.1F, 0.8F),
				PartPose.ZERO);
	}

	private static void addArmPadding(PartDefinition root, boolean slim) {
		if (slim) {
			root.addOrReplaceChild(ARM_PADDING_RIGHT,
					CubeListBuilder.create()
							.texOffs(FatBodySkinUv.RIGHT_ARM_BASE.u(), FatBodySkinUv.RIGHT_ARM_BASE.v())
							.addBox(-2.15F, -2.0F, -2.25F, 3.05F, 9.8F, 4.5F),
					PartPose.offset(-5.0F, 2.0F, 0.0F));
			root.addOrReplaceChild(ARM_PADDING_LEFT,
					CubeListBuilder.create()
							.texOffs(FatBodySkinUv.LEFT_ARM_BASE.u(), FatBodySkinUv.LEFT_ARM_BASE.v())
							.addBox(-0.9F, -2.0F, -2.25F, 3.05F, 9.8F, 4.5F),
					PartPose.offset(5.0F, 2.0F, 0.0F));
			return;
		}

		root.addOrReplaceChild(ARM_PADDING_RIGHT,
				CubeListBuilder.create()
						.texOffs(FatBodySkinUv.RIGHT_ARM_BASE.u(), FatBodySkinUv.RIGHT_ARM_BASE.v())
						.addBox(-2.45F, -2.0F, -2.25F, 3.45F, 9.8F, 4.5F),
				PartPose.offset(-5.0F, 2.0F, 0.0F));
		root.addOrReplaceChild(ARM_PADDING_LEFT,
				CubeListBuilder.create()
						.texOffs(FatBodySkinUv.LEFT_ARM_BASE.u(), FatBodySkinUv.LEFT_ARM_BASE.v())
						.addBox(-1.0F, -2.0F, -2.25F, 3.45F, 9.8F, 4.5F),
				PartPose.offset(5.0F, 2.0F, 0.0F));
	}

	private static void addLegPadding(PartDefinition root) {
		root.addOrReplaceChild(LEG_PADDING_RIGHT,
				CubeListBuilder.create()
						.texOffs(FatBodySkinUv.RIGHT_LEG_OUTER.u(), FatBodySkinUv.RIGHT_LEG_OUTER.v())
						.addBox(-2.15F, 0.0F, -2.3F, 3.95F, 11.8F, 4.6F),
				PartPose.offset(-1.9F, 12.0F, 0.0F));
		root.addOrReplaceChild(LEG_PADDING_LEFT,
				CubeListBuilder.create()
						.texOffs(FatBodySkinUv.LEFT_LEG_OUTER.u(), FatBodySkinUv.LEFT_LEG_OUTER.v())
						.addBox(-1.8F, 0.0F, -2.3F, 3.95F, 11.8F, 4.6F),
				PartPose.offset(1.9F, 12.0F, 0.0F));
	}

	private ModelPart getBellyPart(int stageId) {
		return switch (stageId) {
			case 1 -> bellySmall;
			case 2 -> bellyMedium;
			case 3 -> bellyLarge;
			case 4 -> bellyHuge;
			default -> bellySmall;
		};
	}

	private static void copyBodyPose(PlayerModel<AbstractClientPlayer> playerModel, ModelPart part) {
		part.copyFrom(playerModel.body);
	}
}
