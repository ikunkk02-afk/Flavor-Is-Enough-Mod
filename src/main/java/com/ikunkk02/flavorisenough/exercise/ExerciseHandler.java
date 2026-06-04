package com.ikunkk02.flavorisenough.exercise;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.funmode.FunModeHandler;
import com.ikunkk02.flavorisenough.scale.PlayerBodyScaleHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ExerciseHandler {
    private static final int EXERCISE_THRESHOLD = 20;
    private static final int SPRINT_TICK_INTERVAL = 20;
    private static final int SWIM_TICK_INTERVAL = 20;
    private static final int SWIM_OBESITY_INTERVAL = 100; // 5 seconds
    private static final int JUMP_COOLDOWN_TICKS = 20; // 1 second
    private static final int MESSAGE_COOLDOWN_TICKS = 100; // 5 seconds
    private static final double MIN_HORIZONTAL_MOVE = 0.03;

    private static final Component RECOVERY_MESSAGE = Component.translatable(
            "exercise.flavor-is-enough-mod.recovery");
    private static final Component ENCOURAGE_MESSAGE = Component.translatable(
            "exercise.flavor-is-enough-mod.encourage");

    // Transient tracking (not persisted)
    private static final Map<UUID, Vec3> LAST_POS = new HashMap<>();
    private static final Map<UUID, Long> LAST_SPRINT_TICK = new HashMap<>();
    private static final Map<UUID, Long> LAST_SWIM_TICK = new HashMap<>();
    private static final Map<UUID, Long> LAST_SWIM_OBESITY_TICK = new HashMap<>();
    private static final Map<UUID, Long> LAST_JUMP_TICK = new HashMap<>();
    private static final Map<UUID, Boolean> WAS_ON_GROUND = new HashMap<>();

    private ExerciseHandler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ExerciseHandler::onServerTick);
    }

    private static void onServerTick(net.minecraft.server.MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.isSpectator() || player.isCreative() || FunModeHandler.isFunModeActive(player)) {
                continue;
            }

            UUID uuid = player.getUUID();
            Vec3 currentPos = player.position();
            Vec3 lastPos = LAST_POS.get(uuid);

            // Anti-AFK: require real horizontal movement
            double horizontalMove = lastPos != null
                    ? currentPos.subtract(lastPos).horizontalDistance()
                    : 0.0;
            LAST_POS.put(uuid, currentPos);

            boolean isMoving = horizontalMove > MIN_HORIZONTAL_MOVE;

            // Swim uses full 3D distance — swimming has a large vertical component
            double fullDistance = lastPos != null
                    ? currentPos.subtract(lastPos).length()
                    : 0.0;
            boolean isMoving3D = fullDistance > MIN_HORIZONTAL_MOVE;

            boolean inVehicle = player.getVehicle() != null;
            boolean isFlying = player.getAbilities().flying;

            if (inVehicle || isFlying) {
                continue;
            }

            long gameTime = player.level().getGameTime();

            // ---- Sprint detection ----
            if (player.isSprinting() && isMoving && player.onGround()) {
                if (gameTime - LAST_SPRINT_TICK.getOrDefault(uuid, 0L) >= SPRINT_TICK_INTERVAL) {
                    addExercise(player, 1, gameTime);
                    reduceStomachLoad(player, 1);
                    LAST_SPRINT_TICK.put(uuid, gameTime);
                }
            }

            // ---- Swim detection ----
            if (player.isSwimming() && isMoving3D) {
                if (gameTime - LAST_SWIM_TICK.getOrDefault(uuid, 0L) >= SWIM_TICK_INTERVAL) {
                    addExercise(player, 2, gameTime);
                    reduceStomachLoad(player, 1);
                    LAST_SWIM_TICK.put(uuid, gameTime);
                }
                if (gameTime - LAST_SWIM_OBESITY_TICK.getOrDefault(uuid, 0L) >= SWIM_OBESITY_INTERVAL) {
                    reduceObesity(player, 1);
                    LAST_SWIM_OBESITY_TICK.put(uuid, gameTime);
                }
            }

            // ---- Jump detection ----
            boolean onGround = player.onGround();
            Boolean wasGround = WAS_ON_GROUND.getOrDefault(uuid, true);
            if (wasGround && !onGround && player.getDeltaMovement().y > 0.05) {
                // Player just jumped
                if (gameTime - LAST_JUMP_TICK.getOrDefault(uuid, 0L) >= JUMP_COOLDOWN_TICKS) {
                    addExercise(player, 1, gameTime);
                    LAST_JUMP_TICK.put(uuid, gameTime);
                }
            }
            WAS_ON_GROUND.put(uuid, onGround);
        }
    }

    private static void addExercise(ServerPlayer player, int amount, long gameTime) {
        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        component.addExerciseValue(amount);
        int current = component.getExerciseValue();

        // Encourage message at 80+
        if (current >= 80 && gameTime - component.getLastWorkoutMessageTime() >= MESSAGE_COOLDOWN_TICKS) {
            player.displayClientMessage(ENCOURAGE_MESSAGE, true);
            component.setLastWorkoutMessageTime(gameTime);
        }

        // Recovery at threshold
        if (current >= EXERCISE_THRESHOLD) {
            triggerRecovery(player, component);
            component.setExerciseValue(current - EXERCISE_THRESHOLD);
        }

        ModEntityComponents.FLAVOR_PLAYER.sync(player);
    }

    private static void triggerRecovery(ServerPlayer player, FlavorPlayerComponent component) {
        int obesity = component.getObesityValue();
        int obesityDrop;
        int stomachDrop;
        int healthGain;

        if (obesity >= 60) {
            obesityDrop = 3;
            stomachDrop = 5;
            healthGain = 2;
        } else if (obesity >= 40) {
            obesityDrop = 2;
            stomachDrop = 4;
            healthGain = 2;
        } else if (obesity >= 20) {
            obesityDrop = 1;
            stomachDrop = 3;
            healthGain = 1;
        } else {
            obesityDrop = 0;
            stomachDrop = 2;
            healthGain = 1;
        }

        int oldStage = component.getObesityStageId();

        if (obesityDrop > 0) {
            component.addObesityValue(-obesityDrop);
        }
        component.addStomachLoad(-stomachDrop);
        component.addHealthValue(healthGain);

        ModEntityComponents.FLAVOR_PLAYER.sync(player);

        // Refresh Pehkui if obesity changed
        if (obesityDrop > 0) {
            PlayerBodyScaleHandler.refreshAfterHealthChange(player, oldStage);
        }

        player.displayClientMessage(RECOVERY_MESSAGE, true);
    }

    private static void reduceStomachLoad(ServerPlayer player, int amount) {
        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        if (component.getStomachLoad() > 0) {
            component.addStomachLoad(-amount);
            ModEntityComponents.FLAVOR_PLAYER.sync(player);
        }
    }

    private static void reduceObesity(ServerPlayer player, int amount) {
        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        if (component.getObesityValue() > 0) {
            int oldStage = component.getObesityStageId();
            component.addObesityValue(-amount);
            ModEntityComponents.FLAVOR_PLAYER.sync(player);
            PlayerBodyScaleHandler.refreshAfterHealthChange(player, oldStage);
        }
    }
}
