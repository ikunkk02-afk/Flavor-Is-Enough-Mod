package com.ikunkk02.flavorisenough.mixin;

import com.ikunkk02.flavorisenough.funmode.FunModeHandler;
import com.ikunkk02.flavorisenough.funmode.FunModeRarity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Server-side mixin: makes all items edible for fun-mode players.
 * When the "use item" packet arrives from the client, if the player
 * has fun mode activated, we add FoodProperties to non-food items
 * so vanilla can process them through the normal eating pipeline.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class FunModeUseItemMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleUseItem", at = @At("HEAD"))
    private void flavorIsEnough$onUseItem(ServerboundUseItemPacket packet, CallbackInfo ci) {
        if (!FunModeHandler.isFunModeActive(this.player)) {
            return;
        }

        ItemStack stack = this.player.getItemInHand(packet.getHand());
        if (stack.isEmpty()) {
            return;
        }

        FoodProperties existing = stack.get(DataComponents.FOOD);
        if (existing != null) {
            // Already food — force canAlwaysEat so full-hunger eating works
            if (!existing.canAlwaysEat()) {
                stack.set(DataComponents.FOOD, FunModeRarity.makeAlwaysEdible(existing));
            }
        } else {
            // Not food — add synthetic food data so vanilla eating system can process it.
            // Rarer blocks take longer to eat and give more nutrition/saturation.
            stack.set(DataComponents.FOOD, FunModeRarity.syntheticFood(stack));
        }

        // Let vanilla proceed — it will now find food data and call startUsingItem()
    }
}
