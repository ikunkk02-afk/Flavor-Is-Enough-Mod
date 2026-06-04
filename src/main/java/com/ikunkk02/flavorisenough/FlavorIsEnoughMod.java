package com.ikunkk02.flavorisenough;

import com.ikunkk02.flavorisenough.config.FlavorModConfig;
import com.ikunkk02.flavorisenough.effect.FatBurdenEffectHandler;
import com.ikunkk02.flavorisenough.effect.ModStatusEffects;
import com.ikunkk02.flavorisenough.entity.ModEntities;
import com.ikunkk02.flavorisenough.exercise.ExerciseHandler;
import com.ikunkk02.flavorisenough.funmode.FunModeActivationHandler;
import com.ikunkk02.flavorisenough.funmode.FunModeBuffScaler;
import com.ikunkk02.flavorisenough.funmode.FunModeEatHandler;
import com.ikunkk02.flavorisenough.item.ModItemGroups;
import com.ikunkk02.flavorisenough.item.ModItems;
import com.ikunkk02.flavorisenough.scale.PlayerBodyScaleHandler;
import com.ikunkk02.flavorisenough.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlavorIsEnoughMod implements ModInitializer {
	public static final String MOD_ID = "flavor-is-enough-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.register();
		ModItemGroups.register();
		ModSounds.register();
		ModEntities.register();
		ModStatusEffects.register();
		FatBurdenEffectHandler.register();
		ExerciseHandler.register();
		PlayerBodyScaleHandler.register();
		FlavorModConfig.register();
		FunModeActivationHandler.register();
		FunModeBuffScaler.register();
		FunModeEatHandler.register();
		LOGGER.info("Flavor Is Enough Mod initialized! Fun mode ready.");
	}
}
