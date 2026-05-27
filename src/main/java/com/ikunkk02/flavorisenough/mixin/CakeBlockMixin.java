package com.ikunkk02.flavorisenough.mixin;

import com.ikunkk02.flavorisenough.health.VanillaFoodHealthHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {
	@Inject(
			method = "eat(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/InteractionResult;",
			at = @At("RETURN"))
	private static void flavorIsEnough$afterCakeEaten(LevelAccessor level, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<InteractionResult> callbackInfo) {
		if (!level.isClientSide() && callbackInfo.getReturnValue() == InteractionResult.SUCCESS) {
			VanillaFoodHealthHandler.applyCakeSlice(player);
		}
	}
}
