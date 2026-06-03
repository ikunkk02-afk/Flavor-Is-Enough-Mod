package com.ikunkk02.flavorisenough.funmode;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.config.FlavorModConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class FunModeEatHandler {

    private FunModeEatHandler() {
    }

    public static void register() {
        // Eat blocks instead of placing them
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!FlavorModConfig.get().funModeEnabled) return InteractionResult.PASS;
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!FunModeHandler.isFunModeActive(player)) return InteractionResult.PASS;

            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) return InteractionResult.PASS;

            consumeItemAsFood(player, hand, stack);
            return InteractionResult.SUCCESS;
        });

        // Bite mobs when right-clicking them
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!FlavorModConfig.get().funModeEnabled) return InteractionResult.PASS;
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!FunModeHandler.isFunModeActive(player)) return InteractionResult.PASS;
            if (!(entity instanceof LivingEntity living)) return InteractionResult.PASS;
            if (entity instanceof Player) return InteractionResult.PASS;
            if (entity.getType() == EntityType.ENDER_DRAGON) return InteractionResult.PASS;

            // Bite the mob
            FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
            int tier = component.getFunModeFoodEaten() / 50;
            float biteDamage = 10.0f + tier * 2.0f;
            living.hurt(player.damageSources().playerAttack(player), biteDamage);

            // Give food and saturation
            player.getFoodData().eat(6, 0.8f);

            // Particles
            if (world instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                        living.getX(), living.getY() + living.getBbHeight() / 2, living.getZ(),
                        10, 0.3, 0.3, 0.3, 0.1);
            }

            // Sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0f, 0.8f);

            component.incrementFunModeFoodEaten();
            ModEntityComponents.FLAVOR_PLAYER.sync(player);

            return InteractionResult.SUCCESS;
        });
    }

    private static void consumeItemAsFood(Player player, InteractionHand hand, ItemStack stack) {
        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);

        // Give nutrition based on stack size (max stack = more filling)
        int nutrition = 4 + Math.min(stack.getMaxStackSize() / 16, 4);
        player.getFoodData().eat(nutrition, 0.6f);

        // Consume one item
        stack.shrink(1);

        // Sound
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0f, 1.0f);

        component.incrementFunModeFoodEaten();
        ModEntityComponents.FLAVOR_PLAYER.sync(player);
    }
}
