package com.ikunkk02.flavorisenough.client.mixin;

import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.config.FlavorModConfig;
import com.ikunkk02.flavorisenough.funmode.FunModeRarity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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

        // If the crosshair is on a block and the player is holding a block item,
        // do not make it edible on the client. This preserves vanilla placement:
        // aim at a block = place, aim into air = eat the held block/item.
        if (mc.hitResult instanceof BlockHitResult && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof BlockItem) {
                    stack.remove(DataComponents.FOOD);
                    return;
                }
            }
        }

        // Add synthetic FOOD data to non-food items, and force canAlwaysEat
        // on existing food so the client sends the "use item" packet even
        // when the player's hunger bar is full.
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) {
                continue;
            }
            FoodProperties existing = stack.get(DataComponents.FOOD);
            if (existing != null) {
                // Already food — force canAlwaysEat so full-hunger eating works
                if (!existing.canAlwaysEat()) {
                    stack.set(DataComponents.FOOD, FunModeRarity.makeAlwaysEdible(existing));
                }
            } else {
                // Not food — add synthetic FOOD
                stack.set(DataComponents.FOOD, FunModeRarity.syntheticFood(stack));
            }
        }
    }
}
