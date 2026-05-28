package com.ikunkk02.flavorisenough.sound;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
	public static final SoundEvent EASTER_FOOD = register("easter_food");

	private ModSounds() {
	}

	public static void register() {
		FlavorIsEnoughMod.LOGGER.info("Registering sounds for {}", FlavorIsEnoughMod.MOD_ID);
	}

	private static SoundEvent register(String name) {
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, name);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}
}
