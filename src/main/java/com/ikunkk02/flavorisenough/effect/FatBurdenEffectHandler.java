package com.ikunkk02.flavorisenough.effect;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FatBurdenEffectHandler {
    private static final int EAT_COOLDOWN_TICKS = 200;
    private static final int EFFECT_TICK_INTERVAL = 20;
    private static final int HUNGER_INTERVAL_TICKS = 6000;
    private static final int EFFECT_REFRESH_TICKS = 100;
    private static final int CRATER_COOLDOWN_TICKS = 100;
    private static final int TRIGGER_OBESITY = 60;
    private static final int NO_EXISTING_EFFECT = -1;

    private static final Component FIRST_TRIGGER_MESSAGE = Component.translatable(
            "effect.flavor-is-enough-mod.fat_burden.first_trigger");

    private static final Map<UUID, Boolean> WAS_ON_GROUND = new HashMap<>();
    private static final Map<UUID, Double> PEAK_AIRBORNE_Y = new HashMap<>();
    private static final Map<UUID, Long> LAST_CRATER_TIME = new HashMap<>();

    private FatBurdenEffectHandler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FatBurdenEffectHandler::onServerTick);
    }

    public static void applyAfterEating(Player player) {
        if (player.level().isClientSide()) {
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
                clearJumpCraterTracking(player);
                continue;
            }

            FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
            int stage = component.getObesityStageId();
            MobEffectInstance fatBurden = player.getEffect(ModStatusEffects.FAT_BURDEN_HOLDER);
            boolean hasFatBurden = fatBurden != null;
            int burdenAmplifier = burdenAmplifierForState(
                    stage,
                    hasFatBurden,
                    hasFatBurden ? fatBurden.getAmplifier() : 0);

            if (shouldApplyBurdenEffects(stage, hasFatBurden)) {
                handleJumpCrater(player, stage, hasFatBurden, burdenAmplifier);
            } else {
                clearJumpCraterTracking(player);
            }

            if (!refreshTimedEffects) {
                continue;
            }

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

    static boolean shouldCreateCrater(long currentGameTime, long lastCraterTime) {
        return currentGameTime - lastCraterTime >= CRATER_COOLDOWN_TICKS;
    }

    private static void handleJumpCrater(ServerPlayer player, int obesityStageId, boolean hasFatBurdenEffect, int burdenAmplifier) {
        UUID uuid = player.getUUID();
        boolean onGround = player.onGround();
        double currentY = player.getY();

        if (!onGround) {
            double peak = PEAK_AIRBORNE_Y.getOrDefault(uuid, currentY);
            if (currentY > peak) {
                PEAK_AIRBORNE_Y.put(uuid, currentY);
            }
            WAS_ON_GROUND.put(uuid, false);
            return;
        }

        boolean wasOnGround = WAS_ON_GROUND.getOrDefault(uuid, true);
        if (!wasOnGround) {
            double peak = PEAK_AIRBORNE_Y.getOrDefault(uuid, currentY);
            double fallDistance = peak - currentY;
            long currentGameTime = player.level().getGameTime();
            long lastCraterTime = LAST_CRATER_TIME.getOrDefault(uuid, Long.MIN_VALUE / 2);

            if (shouldCreateCrater(currentGameTime, lastCraterTime)) {
                createCrater(player, Math.max(0.0D, fallDistance), obesityStageId, hasFatBurdenEffect, burdenAmplifier);
                LAST_CRATER_TIME.put(uuid, currentGameTime);
            }
        }

        WAS_ON_GROUND.put(uuid, true);
        PEAK_AIRBORNE_Y.remove(uuid);
    }

    private static void clearJumpCraterTracking(ServerPlayer player) {
        UUID uuid = player.getUUID();
        WAS_ON_GROUND.remove(uuid);
        PEAK_AIRBORNE_Y.remove(uuid);
        LAST_CRATER_TIME.remove(uuid);
    }

    private static void createCrater(ServerPlayer player, double fallDistance, int obesityStageId, boolean hasFatBurdenEffect, int burdenAmplifier) {
        CraterProfile profile = craterProfileForLanding(fallDistance, obesityStageId, hasFatBurdenEffect, burdenAmplifier);
        if (profile.radius() == 0 || profile.depth() == 0) {
            return;
        }

        ServerLevel level = player.serverLevel();
        double centerX = player.getX();
        double centerZ = player.getZ();
        int baseY = player.getBlockY() - 1;

        playHarmlessExplosion(level, centerX, baseY + 1.0D, centerZ, profile);

        for (int dx = -profile.radius(); dx <= profile.radius(); dx++) {
            for (int dz = -profile.radius(); dz <= profile.radius(); dz++) {
                if (Math.abs(dx) == profile.radius() && Math.abs(dz) == profile.radius()
                        && profile.radius() > 1) {
                    continue;
                }

                for (int dy = 0; dy < profile.depth(); dy++) {
                    BlockPos pos = new BlockPos(
                            (int) Math.floor(centerX) + dx,
                            baseY - dy,
                            (int) Math.floor(centerZ) + dz);
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir() || state.getDestroySpeed(level, pos) < 0) {
                        continue;
                    }

                    level.destroyBlock(pos, false);
                }
            }
        }

        FlavorIsEnoughMod.LOGGER.debug("Player {} created crater: fall={}, radius={}, depth={}",
                player.getName().getString(), String.format("%.1f", fallDistance), profile.radius(), profile.depth());
    }

    private static void playHarmlessExplosion(ServerLevel level, double x, double y, double z, CraterProfile profile) {
        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                profile.radius() >= 3 ? 4.0F : 2.5F,
                profile.radius() >= 3 ? 0.75F : 0.9F);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y + 0.2D, z,
                profile.radius() * 8, profile.radius() * 0.35D, 0.25D, profile.radius() * 0.35D, 0.02D);
    }

    static CraterProfile craterProfileForLanding(double fallDistance, int obesityStageId, boolean hasFatBurdenEffect, int fatBurdenAmplifier) {
        if (!shouldApplyBurdenEffects(obesityStageId, hasFatBurdenEffect)) {
            return CraterProfile.NONE;
        }

        int burdenAmplifier = burdenAmplifierForState(obesityStageId, hasFatBurdenEffect, fatBurdenAmplifier);
        int radius = burdenAmplifier >= 1 ? 3 : 2;
        int depth = burdenAmplifier >= 1 ? 2 : 1;

        if (fallDistance >= 3.0D) {
            radius++;
        }
        if (fallDistance >= 4.0D) {
            depth++;
        }
        if (fallDistance >= 6.0D) {
            radius++;
        }

        return new CraterProfile(Math.min(radius, 5), Math.min(depth, 3));
    }

    record CraterProfile(int radius, int depth) {
        private static final CraterProfile NONE = new CraterProfile(0, 0);
    }

    private static final int TRIGGER_STOMACH = 75;
    private static final int HIGH_OBESITY = 80;
    private static final int HIGH_STOMACH = 90;
}
