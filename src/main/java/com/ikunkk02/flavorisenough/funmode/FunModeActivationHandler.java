package com.ikunkk02.flavorisenough.funmode;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.config.FlavorModConfig;
import com.ikunkk02.flavorisenough.entity.LiangziEntity;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FunModeActivationHandler {
    private static final int MEAT_MIN_NUTRITION = 3;
    private static final long CONFIRM_TIMEOUT_TICKS = 20L * 30L;
    private static final double MAX_CONFIRM_DISTANCE_SQR = 8.0D * 8.0D;
    private static final String CONFIRM_COMMAND = "/flavorfun confirm";
    private static final String CANCEL_COMMAND = "/flavorfun cancel";

    private static final Map<UUID, PendingActivation> PENDING_ACTIVATIONS = new HashMap<>();

    private FunModeActivationHandler() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("flavorfun")
                .then(Commands.literal("confirm").executes(context -> confirm(context.getSource())))
                .then(Commands.literal("cancel").executes(context -> cancel(context.getSource()))));
    }

    public static boolean handleSneakInteract(ServerPlayer player, LiangziEntity liangzi) {
        if (!FlavorModConfig.get().funModeEnabled) {
            player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_not_enabled"), false);
            return true;
        }

        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        if (component.isFunModeActivated()) {
            player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_already_unlocked"), false);
            return true;
        }

        int required = FlavorModConfig.get().funModeOfferingRequired;
        int available = countMeat(player);
        long expiresAt = player.serverLevel().getGameTime() + CONFIRM_TIMEOUT_TICKS;
        PENDING_ACTIVATIONS.put(player.getUUID(), new PendingActivation(liangzi.getUUID(), expiresAt));

        player.sendSystemMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_confirm_question", required, available)
                .withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(confirmButton().append(Component.literal("  ")).append(cancelButton()));
        return true;
    }

    public static boolean isMeat(ItemStack stack) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        return food != null && food.nutrition() >= MEAT_MIN_NUTRITION;
    }

    private static int confirm(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception ignored) {
            return 0;
        }

        PendingActivation pending = PENDING_ACTIVATIONS.get(player.getUUID());
        if (pending == null || player.serverLevel().getGameTime() > pending.expiresAt()) {
            PENDING_ACTIVATIONS.remove(player.getUUID());
            player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_confirm_expired"), false);
            return 0;
        }

        if (!FlavorModConfig.get().funModeEnabled) {
            PENDING_ACTIVATIONS.remove(player.getUUID());
            player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_not_enabled"), false);
            return 0;
        }

        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        if (component.isFunModeActivated()) {
            PENDING_ACTIVATIONS.remove(player.getUUID());
            player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_already_unlocked"), false);
            return 1;
        }

        LiangziEntity liangzi = findLiangzi(player, pending.liangziUuid());
        if (liangzi == null) {
            PENDING_ACTIVATIONS.remove(player.getUUID());
            player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_liangzi_too_far"), false);
            return 0;
        }

        int required = FlavorModConfig.get().funModeOfferingRequired;
        int available = countMeat(player);
        if (available < required) {
            player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_not_enough_meat", available, required), false);
            return 0;
        }

        removeMeat(player, required);
        component.setFunModeOfferingCount(required);
        FunModeHandler.activateFunMode(player);
        PENDING_ACTIVATIONS.remove(player.getUUID());

        player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_unlocked"), false);
        player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_enabled_eat_blocks"), false);

        ServerLevel level = player.serverLevel();
        level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                liangzi.getX(), liangzi.getY() + liangzi.getBbHeight() + 1.0D, liangzi.getZ(),
                100, 1.0D, 1.0D, 1.0D, 0.5D);
        level.sendParticles(ParticleTypes.HEART,
                liangzi.getX(), liangzi.getY() + liangzi.getBbHeight() + 1.0D, liangzi.getZ(),
                50, 1.5D, 1.5D, 1.5D, 0.3D);
        liangzi.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
        return 1;
    }

    private static int cancel(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception ignored) {
            return 0;
        }
        PENDING_ACTIVATIONS.remove(player.getUUID());
        player.displayClientMessage(Component.translatable("message.flavor-is-enough-mod.fun_mode_cancelled"), false);
        return 1;
    }

    private static LiangziEntity findLiangzi(ServerPlayer player, UUID uuid) {
        if (!(player.serverLevel().getEntity(uuid) instanceof LiangziEntity liangzi)) {
            return null;
        }
        if (!liangzi.isAlive() || liangzi.distanceToSqr(player) > MAX_CONFIRM_DISTANCE_SQR) {
            return null;
        }
        return liangzi;
    }

    private static int countMeat(ServerPlayer player) {
        int count = 0;
        Inventory inventory = player.getInventory();
        for (ItemStack stack : inventory.items) {
            if (isMeat(stack)) {
                count += stack.getCount();
            }
        }
        for (ItemStack stack : inventory.offhand) {
            if (isMeat(stack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void removeMeat(ServerPlayer player, int amount) {
        int remaining = amount;
        Inventory inventory = player.getInventory();
        for (ItemStack stack : inventory.items) {
            if (remaining <= 0) {
                break;
            }
            if (!isMeat(stack)) {
                continue;
            }
            int toRemove = Math.min(remaining, stack.getCount());
            stack.shrink(toRemove);
            remaining -= toRemove;
        }
        for (ItemStack stack : inventory.offhand) {
            if (remaining <= 0) {
                break;
            }
            if (!isMeat(stack)) {
                continue;
            }
            int toRemove = Math.min(remaining, stack.getCount());
            stack.shrink(toRemove);
            remaining -= toRemove;
        }
        inventory.setChanged();
    }

    private static MutableComponent confirmButton() {
        return Component.translatable("message.flavor-is-enough-mod.fun_mode_confirm_button")
                .withStyle(style -> style.withColor(ChatFormatting.GREEN)
                        .withBold(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CONFIRM_COMMAND))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("message.flavor-is-enough-mod.fun_mode_confirm_hover"))));
    }

    private static MutableComponent cancelButton() {
        return Component.translatable("message.flavor-is-enough-mod.fun_mode_cancel_button")
                .withStyle(style -> style.withColor(ChatFormatting.RED)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CANCEL_COMMAND))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("message.flavor-is-enough-mod.fun_mode_cancel_hover"))));
    }

    private record PendingActivation(UUID liangziUuid, long expiresAt) {
    }
}
