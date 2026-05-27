package com.ikunkk02.flavorisenough.item;

import com.ikunkk02.flavorisenough.health.FlavorHealthApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FlavorFoodItem extends Item {
	private final int flavorChange;
	private final int obesityChange;
	private final int healthChange;
	private final int stomachLoadChange;

	public FlavorFoodItem(Properties properties, int flavorChange, int obesityChange, int healthChange, int stomachLoadChange) {
		super(properties);
		this.flavorChange = flavorChange;
		this.obesityChange = obesityChange;
		this.healthChange = healthChange;
		this.stomachLoadChange = stomachLoadChange;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		ItemStack result = super.finishUsingItem(stack, level, livingEntity);

		if (!level.isClientSide() && livingEntity instanceof Player player) {
			FlavorHealthApplier.apply(player, flavorChange, obesityChange, healthChange, stomachLoadChange, false);
		}

		return result;
	}
}
