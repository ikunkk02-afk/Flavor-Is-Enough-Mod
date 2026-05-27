package com.ikunkk02.flavorisenough.health;

import com.ikunkk02.flavorisenough.item.FlavorFoodItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Set;

public final class VanillaFoodHealthHandler {
	private static final FlavorHealthChange HEALTHY = new FlavorHealthChange(2, -1, 4, 2);
	private static final FlavorHealthChange STAPLE = new FlavorHealthChange(4, 1, 1, 5);
	private static final FlavorHealthChange MEAT = new FlavorHealthChange(6, 3, 0, 7);
	private static final FlavorHealthChange RAW_MEAT = new FlavorHealthChange(6, 3, -2, 7);
	private static final FlavorHealthChange HIGH_CALORIE = new FlavorHealthChange(8, 6, -2, 9);

	private static final Set<Item> HEALTHY_FOODS = Set.of(
			Items.APPLE,
			Items.CARROT,
			Items.BEETROOT,
			Items.BAKED_POTATO,
			Items.MELON_SLICE,
			Items.SWEET_BERRIES,
			Items.GLOW_BERRIES,
			Items.DRIED_KELP,
			Items.GOLDEN_CARROT);

	private static final Set<Item> STAPLE_FOODS = Set.of(
			Items.BREAD,
			Items.POTATO,
			Items.MUSHROOM_STEW,
			Items.BEETROOT_SOUP,
			Items.RABBIT_STEW,
			Items.SUSPICIOUS_STEW);

	private static final Set<Item> MEAT_FOODS = Set.of(
			Items.COOKED_BEEF,
			Items.COOKED_PORKCHOP,
			Items.COOKED_CHICKEN,
			Items.COOKED_MUTTON,
			Items.COOKED_RABBIT,
			Items.COOKED_COD,
			Items.COOKED_SALMON);

	private static final Set<Item> RAW_MEAT_FOODS = Set.of(
			Items.BEEF,
			Items.PORKCHOP,
			Items.CHICKEN,
			Items.MUTTON,
			Items.RABBIT,
			Items.COD,
			Items.SALMON,
			Items.TROPICAL_FISH);

	private static final Set<Item> HIGH_CALORIE_FOODS = Set.of(
			Items.COOKIE,
			Items.PUMPKIN_PIE,
			Items.CAKE,
			Items.HONEY_BOTTLE);

	private VanillaFoodHealthHandler() {
	}

	public static boolean applyVanillaFood(Player player, ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof FlavorFoodItem || !isMinecraftItem(item)) {
			return false;
		}

		FlavorHealthChange change = getChange(item);
		if (change == null) {
			return false;
		}

		FlavorHealthApplier.apply(player, change, true);
		return true;
	}

	public static void applyCakeSlice(Player player) {
		FlavorHealthApplier.apply(player, HIGH_CALORIE, true);
	}

	private static FlavorHealthChange getChange(Item item) {
		if (HEALTHY_FOODS.contains(item)) {
			return HEALTHY;
		}
		if (STAPLE_FOODS.contains(item)) {
			return STAPLE;
		}
		if (MEAT_FOODS.contains(item)) {
			return MEAT;
		}
		if (RAW_MEAT_FOODS.contains(item)) {
			return RAW_MEAT;
		}
		if (HIGH_CALORIE_FOODS.contains(item)) {
			return HIGH_CALORIE;
		}
		if (item == Items.GOLDEN_APPLE) {
			return new FlavorHealthChange(10, 1, 8, 4);
		}
		if (item == Items.ENCHANTED_GOLDEN_APPLE) {
			return new FlavorHealthChange(15, 0, 15, 2);
		}
		if (item == Items.CHORUS_FRUIT) {
			return new FlavorHealthChange(6, 0, 1, 4);
		}
		if (item == Items.ROTTEN_FLESH) {
			return new FlavorHealthChange(-2, 1, -8, 6);
		}
		if (item == Items.SPIDER_EYE) {
			return new FlavorHealthChange(-5, 0, -10, 4);
		}
		if (item == Items.PUFFERFISH) {
			return new FlavorHealthChange(-5, 0, -15, 5);
		}
		if (item == Items.POISONOUS_POTATO) {
			return new FlavorHealthChange(-3, 0, -8, 4);
		}
		return null;
	}

	private static boolean isMinecraftItem(Item item) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
		return "minecraft".equals(id.getNamespace());
	}
}
