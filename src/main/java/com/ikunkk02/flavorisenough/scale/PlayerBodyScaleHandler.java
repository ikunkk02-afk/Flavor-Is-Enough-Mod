package com.ikunkk02.flavorisenough.scale;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerBodyScaleHandler {
	private static final int REFRESH_INTERVAL_TICKS = 20;
	private static final ResourceLocation MOVEMENT_SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(
			FlavorIsEnoughMod.MOD_ID,
			"obesity_movement_speed");
	private static final Component OBESE_WARNING = Component.literal(
			"你的身体负担正在增加，游戏可以抽象，现实请注意饮食和运动。");
	private static final Component SEVERE_WARNING = Component.literal(
			"严重负担阶段：移动能力明显下降。现实中长期肥胖可能影响健康。");
	private static final Map<UUID, Integer> LAST_STAGE_BY_PLAYER = new HashMap<>();

	private PlayerBodyScaleHandler() {
	}

	public static void register() {
		ServerPlayerEvents.JOIN.register(PlayerBodyScaleHandler::refresh);
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> refresh(newPlayer));
		ServerPlayerEvents.LEAVE.register(player -> LAST_STAGE_BY_PLAYER.remove(player.getUUID()));
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % REFRESH_INTERVAL_TICKS != 0) {
				return;
			}

			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				refresh(player);
			}
		});
	}

	public static void refresh(ServerPlayer player) {
		BodyScaleProfile profile = profileFor(player);
		applyProfile(player, profile);
	}

	public static void refreshAfterHealthChange(ServerPlayer player, int previousStage) {
		BodyScaleProfile profile = profileFor(player);
		applyProfile(player, profile);
		showStageWarning(player, previousStage, profile.stageId());
	}

	private static BodyScaleProfile profileFor(ServerPlayer player) {
		FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
		return BodyScaleProfile.forObesityValue(component.getObesityValue());
	}

	private static void applyProfile(ServerPlayer player, BodyScaleProfile profile) {
		applyScale(ScaleTypes.WIDTH, player, profile.widthScale());
		applyScale(ScaleTypes.HEIGHT, player, profile.visualHeightScale());
		applyScale(ScaleTypes.HITBOX_HEIGHT, player, profile.hitboxHeightScale());
		applyMovementSpeedPenalty(player, profile.speedPenalty());
		LAST_STAGE_BY_PLAYER.put(player.getUUID(), profile.stageId());
	}

	private static void applyScale(ScaleType scaleType, ServerPlayer player, float scale) {
		ScaleData scaleData = scaleType.getScaleData(player);
		if (Float.compare(scaleData.getBaseScale(), scale) == 0
				&& Float.compare(scaleData.getTargetScale(), scale) == 0) {
			return;
		}

		scaleData.setScale(scale);
	}

	private static void applyMovementSpeedPenalty(ServerPlayer player, double speedPenalty) {
		AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (movementSpeed == null) {
			return;
		}

		movementSpeed.removeModifier(MOVEMENT_SPEED_MODIFIER_ID);
		if (speedPenalty != 0.0D) {
			movementSpeed.addTransientModifier(new AttributeModifier(
					MOVEMENT_SPEED_MODIFIER_ID,
					speedPenalty,
					AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		}
	}

	private static void showStageWarning(ServerPlayer player, int previousStage, int currentStage) {
		if (previousStage == currentStage) {
			return;
		}

		if (currentStage == 3) {
			player.displayClientMessage(OBESE_WARNING, false);
		} else if (currentStage == 4) {
			player.displayClientMessage(SEVERE_WARNING, false);
		}
	}
}
