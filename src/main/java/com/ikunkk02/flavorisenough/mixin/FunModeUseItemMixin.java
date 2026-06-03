package com.ikunkk02.flavorisenough.mixin;

import com.ikunkk02.flavorisenough.funmode.FunModeHandler;
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

import java.util.List;
import java.util.Optional;

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

        // If item already has food data, let vanilla handle it
        if (stack.has(DataComponents.FOOD)) {
            return;
        }

        // Add synthetic food data so vanilla eating system can process it.
        // Uses very short eatSeconds (0.2s) for fast-paced eating.
        // canAlwaysEat=true so player can eat even when hunger bar is full.
        FoodProperties funFood = new FoodProperties(
                4,      // nutrition
                0.6f,   // saturation
                true,   // canAlwaysEat
                0.2f,   // eatSeconds
                Optional.empty(),
                List.of()
        );
        stack.set(DataComponents.FOOD, funFood);

        // Let vanilla proceed — it will now find food data and call startUsingItem()
    }
}
