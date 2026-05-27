package com.ikunkk02.flavorisenough.health;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import net.minecraft.network.chat.Component;
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

		int newStage = component.getObesityStageId();
		ModEntityComponents.FLAVOR_PLAYER.sync(player);

		if (notifyStageChange && newStage != oldStage && newStage > 0) {
			player.displayClientMessage(Component.literal("你的体态阶段变为：" + component.getObesityStageText()), false);
		}
	}

	public static void apply(Player player, int flavorValue, int obesityValue, int healthValue, int stomachLoad, boolean notifyStageChange) {
		apply(player, new FlavorHealthChange(flavorValue, obesityValue, healthValue, stomachLoad), notifyStageChange);
	}
}
