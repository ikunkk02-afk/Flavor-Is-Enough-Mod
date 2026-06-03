package com.ikunkk02.flavorisenough.mixin;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.funmode.FunModeHandler;
import com.ikunkk02.flavorisenough.health.VanillaFoodHealthHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEatMixin {
	@Inject(
			method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
			at = @At("RETURN"))
	private void flavorIsEnough$afterEat(Level level, ItemStack stack, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> callbackInfo) {
		if (!level.isClientSide() && (Object) this instanceof Player player) {
			// Apply vanilla food health tracking
			VanillaFoodHealthHandler.applyVanillaFood(player, stack);

			// Fun mode: increment food eaten counter
			if (FunModeHandler.isFunModeActive(player)) {
				FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
				component.incrementFunModeFoodEaten();
				ModEntityComponents.FLAVOR_PLAYER.sync(player);
			}
		}
	}
}
