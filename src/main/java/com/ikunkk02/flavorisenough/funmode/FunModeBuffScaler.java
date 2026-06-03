package com.ikunkk02.flavorisenough.funmode;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;

public final class FunModeBuffScaler {
    private static final ResourceLocation HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("flavor-is-enough-mod", "fun_mode_health");
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("flavor-is-enough-mod", "fun_mode_speed");
    private static final ResourceLocation DAMAGE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("flavor-is-enough-mod", "fun_mode_damage");
    private static final ResourceLocation ARMOR_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("flavor-is-enough-mod", "fun_mode_armor");

    private static final int TICK_INTERVAL = 40; // Every 2 seconds

    private FunModeBuffScaler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FunModeBuffScaler::onServerTick);
    }

    private static void onServerTick(net.minecraft.server.MinecraftServer server) {
        if (server.getTickCount() % TICK_INTERVAL != 0) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.isSpectator() || !FunModeHandler.isFunModeActive(player)) {
                continue;
            }
            applyFunModeBuffs(player);
        }
    }

    public static void applyFunModeBuffs(ServerPlayer player) {
        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        int foodEaten = component.getFunModeFoodEaten();

        // Tier system: every 50 items eaten = 1 tier
        int tier = foodEaten / 50;
        // Cap at tier 20 (1000 items) to prevent overflow
        tier = Math.min(tier, 20);

        // === Permanent Attribute Modifiers (health goes off screen!) ===
        applyHealthBoost(player, tier);

        // === Permanent Speed ===
        applySpeedBoost(player, tier);

        // === Permanent Damage Boost ===
        applyDamageBoost(player, tier);

        // === Permanent Armor ===
        applyArmorBoost(player, tier);

        // === Effect-based Buffs (refreshed each tick) ===
        applyEffectBuffs(player, tier);
    }

    private static void applyHealthBoost(ServerPlayer player, int tier) {
        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr == null) return;

        // +4 per tier → at tier 20 = +80 max health → 100 total
        double bonus = tier * 4.0;
        updateModifier(healthAttr, HEALTH_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void applySpeedBoost(ServerPlayer player, int tier) {
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        // +0.01 per tier → at tier 20 = +0.2 (20% boost)
        double bonus = tier * 0.01;
        updateModifier(speedAttr, SPEED_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void applyDamageBoost(ServerPlayer player, int tier) {
        AttributeInstance damageAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr == null) return;

        // +1 per tier → at tier 20 = +20 attack damage
        double bonus = tier * 1.0;
        updateModifier(damageAttr, DAMAGE_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void applyArmorBoost(ServerPlayer player, int tier) {
        AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
        if (armorAttr == null) return;

        // +1 per tier → at tier 20 = +20 armor
        double bonus = tier * 1.0;
        updateModifier(armorAttr, ARMOR_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void applyEffectBuffs(ServerPlayer player, int tier) {
        int duration = 100; // 5 seconds, refreshed each tick

        // Resistance: starts at level 0, +1 every 3 tiers
        int resistanceLevel = tier / 3;
        if (resistanceLevel > 0) {
            refreshEffect(player, MobEffects.DAMAGE_RESISTANCE, resistanceLevel - 1, duration);
        }

        // Regeneration: starts at tier 1
        if (tier >= 1) {
            int regenLevel = Math.min(tier / 2, 9); // up to level 9 at tier 18+
            refreshEffect(player, MobEffects.REGENERATION, regenLevel, duration);
        }

        // Fire Resistance: at tier 3+
        if (tier >= 3) {
            refreshEffect(player, MobEffects.FIRE_RESISTANCE, 0, duration);
        }

        // Water Breathing: at tier 5+
        if (tier >= 5) {
            refreshEffect(player, MobEffects.WATER_BREATHING, 0, duration);
        }

        // Night Vision: at tier 7+
        if (tier >= 7) {
            refreshEffect(player, MobEffects.NIGHT_VISION, 0, duration);
        }

        // Strength: extra boost at tier 10+
        if (tier >= 10) {
            int strengthLevel = (tier - 10) / 2;
            refreshEffect(player, MobEffects.DAMAGE_BOOST, Math.min(strengthLevel, 4), duration);
        }

        // Haste: at tier 12+
        if (tier >= 12) {
            int hasteLevel = Math.min((tier - 12) / 3, 2);
            refreshEffect(player, MobEffects.DIG_SPEED, hasteLevel, duration);
        }

        // Absorption: at tier 15+
        if (tier >= 15) {
            int absorptionLevel = Math.min((tier - 15) / 2, 4);
            refreshEffect(player, MobEffects.ABSORPTION, absorptionLevel, duration);
        }
    }

    private static void refreshEffect(ServerPlayer player, Holder<MobEffect> effect, int amplifier, int duration) {
        MobEffectInstance existing = player.getEffect(effect);
        if (existing == null || existing.getAmplifier() < amplifier || existing.getDuration() < 60) {
            player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
        }
    }

    private static void updateModifier(AttributeInstance attr, ResourceLocation id, double value, AttributeModifier.Operation op) {
        AttributeModifier existing = attr.getModifier(id);
        if (existing != null) {
            if (existing.amount() != value) {
                attr.removeModifier(id);
                attr.addPermanentModifier(new AttributeModifier(id, value, op));
            }
        } else {
            attr.addPermanentModifier(new AttributeModifier(id, value, op));
        }
    }
}
