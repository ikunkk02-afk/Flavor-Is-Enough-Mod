package com.ikunkk02.flavorisenough.health;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.effect.FatBurdenEffectHandler;
import com.ikunkk02.flavorisenough.scale.PlayerBodyScaleHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class FlavorHealthApplier {
	private FlavorHealthApplier() {
	}

	public static void apply(Player player, FlavorHealthChange change, boolean notifyStageChange) {
		if (player.level().isClientSide()) {
			return;
		}

		FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
		int oldStage = component.getObesityStageId();

		component.addFlavorValue(change.flavorValue());
		component.addObesityValue(change.obesityValue());
		component.addHealthValue(change.healthValue());
		component.addStomachLoad(change.stomachLoad());

		ModEntityComponents.FLAVOR_PLAYER.sync(player);

		if (player instanceof ServerPlayer serverPlayer) {
			PlayerBodyScaleHandler.refreshAfterHealthChange(serverPlayer, oldStage);
		}

		FatBurdenEffectHandler.applyAfterEating(player);
	}

	public static void apply(Player player, int flavorValue, int obesityValue, int healthValue, int stomachLoad, boolean notifyStageChange) {
		apply(player, new FlavorHealthChange(flavorValue, obesityValue, healthValue, stomachLoad), notifyStageChange);
	}
}
