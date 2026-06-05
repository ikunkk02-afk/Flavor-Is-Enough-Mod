package com.ikunkk02.flavorisenough.funmode;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.config.FlavorModConfig;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class FunModeEatHandler {

    private FunModeEatHandler() {
    }

    public static void register() {
        // Bite mobs when right-clicking them
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!FlavorModConfig.get().funModeEnabled) return InteractionResult.PASS;
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!FunModeHandler.isFunModeActive(player)) return InteractionResult.PASS;
            if (!(entity instanceof LivingEntity living)) return InteractionResult.PASS;
            if (entity instanceof Player) return InteractionResult.PASS;
            if (entity.getType() == EntityType.ENDER_DRAGON && !FlavorModConfig.get().canEatEnderDragon) {
                return InteractionResult.PASS;
            }

            // Bite damage follows the same rarity power progression as block eating.
            FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
            int tier = component.getFunModePowerScore() / 50;
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
            int entityScore;
            if (entity.getType() == EntityType.ENDER_DRAGON) {
                entityScore = 25; // godlike — the ultimate meal
            } else if (entity.getType() == EntityType.WITHER) {
                entityScore = 12;
            } else {
                entityScore = 5;
            }
            component.addFunModePowerScore(entityScore);
            ModEntityComponents.FLAVOR_PLAYER.sync(player);

            return InteractionResult.SUCCESS;
        });
    }
}
