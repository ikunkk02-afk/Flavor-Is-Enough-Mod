package com.ikunkk02.flavorisenough.item;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public final class ModItems {
	public static final Item FLAVOR_SNACK = register("flavor_snack",
			new FlavorFoodItem(new Item.Properties().food(food(3, 0.4F)), 5, 1, 0, 3));
	public static final Item BIG_STOMACH_BUN = register("big_stomach_bun",
			new FlavorFoodItem(new Item.Properties().food(food(8, 0.8F)), 15, 8, 0, 12));
	public static final Item OILY_MEAT = register("oily_meat",
			new FlavorFoodItem(new Item.Properties().food(food(10, 1.0F)), 10, 15, -5, 18));
	public static final Item HEALTH_LEAF = register("health_leaf",
			new FlavorFoodItem(new Item.Properties().food(food(1, 0.2F)), 0, -1, 6, -3));
	public static final Item MEASURING_TAPE = register("measuring_tape", new MeasuringTapeItem(new Item.Properties()));
	public static final Item WARNING_CARD = register("warning_card", new WarningCardItem(new Item.Properties()));

	private ModItems() {
	}

	public static void register() {
		FlavorIsEnoughMod.LOGGER.info("Registering items for {}", FlavorIsEnoughMod.MOD_ID);
	}

	private static Item register(String name, Item item) {
		return Registry.register(BuiltInRegistries.ITEM, id(name), item);
	}

	private static ResourceLocation id(String name) {
		return ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, name);
	}

	private static FoodProperties food(int nutrition, float saturation) {
		return new FoodProperties.Builder()
				.nutrition(nutrition)
				.saturationModifier(saturation)
				.build();
	}
}
