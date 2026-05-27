package com.ikunkk02.flavorisenough.item;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModItemGroups {
	public static final CreativeModeTab FLAVOR_IS_ENOUGH_GROUP = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "main"),
			FabricItemGroup.builder()
					.title(Component.translatable("itemGroup." + FlavorIsEnoughMod.MOD_ID + ".main"))
					.icon(() -> new ItemStack(ModItems.BIG_STOMACH_BUN))
					.displayItems((parameters, output) -> {
						output.accept(ModItems.FLAVOR_SNACK);
						output.accept(ModItems.BIG_STOMACH_BUN);
						output.accept(ModItems.OILY_MEAT);
						output.accept(ModItems.HEALTH_LEAF);
						output.accept(ModItems.MEASURING_TAPE);
						output.accept(ModItems.WARNING_CARD);
					})
					.build());

	private ModItemGroups() {
	}

	public static void register() {
		FlavorIsEnoughMod.LOGGER.info("Registering item groups for {}", FlavorIsEnoughMod.MOD_ID);
	}
}
