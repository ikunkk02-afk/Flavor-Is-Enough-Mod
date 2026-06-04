package com.ikunkk02.flavorisenough.client.mixin;

import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.config.FlavorModConfig;
import com.ikunkk02.flavorisenough.funmode.FunModeRarity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side mixin: makes all items appear edible to the client
 * when the player has fun mode activated. Without this, the client
 * won't send the "use item" packet to the server for non-food items.
 */
@Mixin(Minecraft.class)
public class FunModeClientUseItemMixin {

    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void flavorIsEnough$onStartUseItem(CallbackInfo ci) {
        if (!FlavorModConfig.get().funModeEnabled) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }

        // Check if fun mode is activated for this player
        if (!ModEntityComponents.FLAVOR_PLAYER.get(player).isFunModeActivated()) {
            return;
        }

        // Add synthetic FOOD data to non-food items so the client
        // thinks they're edible and sends the "use item" packet
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty() && !stack.has(DataComponents.FOOD)) {
                stack.set(DataComponents.FOOD, FunModeRarity.syntheticFood(stack));
            }
        }
    }
}
