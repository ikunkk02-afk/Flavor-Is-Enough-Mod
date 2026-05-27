package com.ikunkk02.flavorisenough;

import com.ikunkk02.flavorisenough.item.ModItemGroups;
import com.ikunkk02.flavorisenough.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlavorIsEnoughMod implements ModInitializer {
	public static final String MOD_ID = "flavor-is-enough-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.register();
		ModItemGroups.register();
	}
}
