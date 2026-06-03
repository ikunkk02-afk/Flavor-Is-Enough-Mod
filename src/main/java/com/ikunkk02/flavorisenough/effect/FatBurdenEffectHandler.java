package com.ikunkk02.flavorisenough.effect;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.funmode.FunModeHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public final class FatBurdenEffectHandler {
    private static final int EAT_COOLDOWN_TICKS = 200;
    private static final int EFFECT_TICK_INTERVAL = 20;
    private static final int HUNGER_INTERVAL_TICKS = 6000;
    private static final int EFFECT_REFRESH_TICKS = 100;
    private static final int TRIGGER_OBESITY = 60;
    private static final int MILD_DISEASE_OBESITY = 75;
    private static final int NO_EXISTING_EFFECT = -1;

    private static final Component FIRST_TRIGGER_MESSAGE = Component.translatable(
            "effect.flavor-is-enough-mod.fat_burden.first_trigger");

    private FatBurdenEffectHandler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FatBurdenEffectHandler::onServerTick);
    }

    public static void applyAfterEating(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        // In fun mode, no negative effects ever
        if (FunModeHandler.isFunModeActive(player)) {
            return;
        }

        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        int obesity = component.getObesityValue();
        int stomach = component.getStomachLoad();

        if (obesity < TRIGGER_OBESITY && stomach < TRIGGER_STOMACH) {
            return;
        }

        long currentTime = player.level().getGameTime();
        if (currentTime - component.getLastFatBurdenEffectTime() < EAT_COOLDOWN_TICKS) {
            return;
        }

        boolean isHigh = obesity >= HIGH_OBESITY || stomach >= HIGH_STOMACH;
        int fatBurdenDuration = isHigh ? 900 : 600;
        int fatBurdenAmplifier = isHigh ? 1 : 0;

        player.addEffect(new MobEffectInstance(ModStatusEffects.FAT_BURDEN_HOLDER, fatBurdenDuration, fatBurdenAmplifier,
                false, false, true));
        if (player instanceof ServerPlayer serverPlayer) {
            applyBurdenSideEffects(serverPlayer, fatBurdenAmplifier, true);
        }

        component.setLastFatBurdenEffectTime(currentTime);
        component.setLastHungerEffectTime(currentTime);
        ModEntityComponents.FLAVOR_PLAYER.sync(player);

        if (component.isFirstFatBurdenTrigger()) {
            component.markFatBurdenTriggered();
            ModEntityComponents.FLAVOR_PLAYER.sync(player);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(FIRST_TRIGGER_MESSAGE, false);
            }
        }
    }

    private static void onServerTick(net.minecraft.server.MinecraftServer server) {
        boolean refreshTimedEffects = server.getTickCount() % EFFECT_TICK_INTERVAL == 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.isSpectator()) {
                continue;
            }

            FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
            int obesity = component.getObesityValue();
            int stage = component.getObesityStageId();
            MobEffectInstance fatBurden = player.getEffect(ModStatusEffects.FAT_BURDEN_HOLDER);
            boolean hasFatBurden = fatBurden != null;
            int burdenAmplifier = burdenAmplifierForState(
                    stage,
                    hasFatBurden,
                    hasFatBurden ? fatBurden.getAmplifier() : 0);

            if (!refreshTimedEffects) {
                continue;
            }

            refreshObesityDiseaseEffects(player, obesity, stage);

            if (!shouldApplyBurdenEffects(stage, hasFatBurden)) {
                player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                continue;
            }

            if (stage >= 3) {
                refreshEffect(player, ModStatusEffects.FAT_BURDEN_HOLDER, burdenAmplifier);
            }
            applyBurdenSideEffects(player, burdenAmplifier, false);

            long currentTime = player.level().getGameTime();
            if (currentTime - component.getLastHungerEffectTime() >= HUNGER_INTERVAL_TICKS) {
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, burdenAmplifier,
                        false, false, true));
                component.setLastHungerEffectTime(currentTime);
                ModEntityComponents.FLAVOR_PLAYER.sync(player);
            }
        }
    }

    private static void applyBurdenSideEffects(ServerPlayer player, int burdenAmplifier, boolean includeImmediateHunger) {
        refreshEffect(player, MobEffects.DAMAGE_RESISTANCE, resistanceAmplifierForBurden(burdenAmplifier));
        refreshEffect(player, MobEffects.MOVEMENT_SLOWDOWN, slownessAmplifierForBurden(burdenAmplifier));

        if (includeImmediateHunger) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, burdenAmplifier,
                    false, false, true));
        }
    }

    private static void refreshObesityDiseaseEffects(ServerPlayer player, int obesityValue, int obesityStageId) {
        if (shouldApplyMildObesityDisease(obesityValue)) {
            refreshEffect(player, ModStatusEffects.FATTY_LIVER_HOLDER, 0);
        } else {
            player.removeEffect(ModStatusEffects.FATTY_LIVER_HOLDER);
        }

        if (shouldApplySevereObesityDisease(obesityStageId)) {
            refreshEffect(player, ModStatusEffects.CARDIOPULMONARY_BURDEN_HOLDER, 0);
        } else {
            player.removeEffect(ModStatusEffects.CARDIOPULMONARY_BURDEN_HOLDER);
        }
    }

    private static void refreshEffect(ServerPlayer player, Holder<MobEffect> effect, int targetAmplifier) {
        MobEffectInstance existingEffect = player.getEffect(effect);
        int existingAmplifier = existingEffect == null ? NO_EXISTING_EFFECT : existingEffect.getAmplifier();
        int existingDuration = existingEffect == null ? 0 : existingEffect.getDuration();

        if (shouldRefreshEffect(existingAmplifier, existingDuration, targetAmplifier)) {
            player.addEffect(new MobEffectInstance(effect, EFFECT_REFRESH_TICKS + 60, targetAmplifier,
                    false, false, true));
        }
    }

    static boolean shouldApplyBurdenEffects(int obesityStageId, boolean hasFatBurdenEffect) {
        return hasFatBurdenEffect || obesityStageId >= 3;
    }

    static boolean shouldApplyMildObesityDisease(int obesityValue) {
        return obesityValue >= MILD_DISEASE_OBESITY;
    }

    static boolean shouldApplySevereObesityDisease(int obesityStageId) {
        return obesityStageId >= 4;
    }

    static int resistanceAmplifierForStage(int obesityStageId) {
        return resistanceAmplifierForBurden(burdenAmplifierForState(obesityStageId, false, 0));
    }

    static int slownessAmplifierForStage(int obesityStageId) {
        return slownessAmplifierForBurden(burdenAmplifierForState(obesityStageId, false, 0));
    }

    static int burdenAmplifierForState(int obesityStageId, boolean hasFatBurdenEffect, int fatBurdenAmplifier) {
        int stageAmplifier = obesityStageId >= 4 ? 1 : 0;
        return hasFatBurdenEffect ? Math.max(stageAmplifier, fatBurdenAmplifier) : stageAmplifier;
    }

    private static int resistanceAmplifierForBurden(int burdenAmplifier) {
        return burdenAmplifier >= 1 ? 1 : 0;
    }

    private static int slownessAmplifierForBurden(int burdenAmplifier) {
        return burdenAmplifier >= 1 ? 1 : 0;
    }

    static boolean shouldRefreshEffect(int existingAmplifier, int existingDuration, int targetAmplifier) {
        if (existingAmplifier == NO_EXISTING_EFFECT) {
            return true;
        }
        if (existingAmplifier < targetAmplifier) {
            return true;
        }
        return existingAmplifier == targetAmplifier && existingDuration < EFFECT_REFRESH_TICKS;
    }

    private static final int TRIGGER_STOMACH = 75;
    private static final int HIGH_OBESITY = 80;
    private static final int HIGH_STOMACH = 90;
}
