package com.ikunkk02.flavorisenough.impact;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class HeavyJumpImpactHandler {
    static final boolean HEAVY_JUMP_IMPACT_ENABLED = true;
    static final int HEAVY_JUMP_COOLDOWN_TICKS = 100;
    static final int HEAVY_JUMP_MIN_OBESITY = 60;
    static final boolean HEAVY_JUMP_BREAK_BLOCKS = true;

    private static final int SEVERE_OBESITY = 80;
    private static final int COOLDOWN_MESSAGE_INTERVAL_TICKS = 20;
    private static final Component COOLDOWN_MESSAGE = Component.literal("重踏冷却中");

    private static final ImpactProfile NO_IMPACT = new ImpactProfile(
            false, 0, 0, 0.0D, 0.0F, "", 0.0F, 1.0F, 0, 0);
    private static final ImpactProfile SMALL_IMPACT = new ImpactProfile(
            true, 3, 3, 0.6D, 1.0F, "重踏冲击！", 2.5F, 0.95F, 1, 36);
    private static final ImpactProfile LARGE_IMPACT = new ImpactProfile(
            true, 5, 5, 1.0D, 2.0F, "严重负担重踏！", 4.0F, 0.75F, 2, 90);

    private HeavyJumpImpactHandler() {
    }

    public static void triggerHeavyJumpImpact(ServerPlayer player) {
        if (!HEAVY_JUMP_IMPACT_ENABLED || player.level().isClientSide() || player.isCreative() || player.isSpectator()) {
            return;
        }

        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        ImpactProfile profile = profileForObesity(component.getObesityValue());
        if (!profile.active()) {
            return;
        }

        long currentGameTime = player.level().getGameTime();
        if (!canTriggerHeavyJump(currentGameTime, component.getLastHeavyJumpTime())) {
            showCooldownMessageIfNeeded(player, component, currentGameTime);
            return;
        }

        component.setLastHeavyJumpTime(currentGameTime);
        ModEntityComponents.FLAVOR_PLAYER.sync(player);

        player.displayClientMessage(Component.literal(profile.actionbarText()), true);
        applyImpact(player, profile);
    }

    static ImpactProfile profileForObesity(int obesityValue) {
        if (obesityValue < HEAVY_JUMP_MIN_OBESITY) {
            return NO_IMPACT;
        }
        if (obesityValue < SEVERE_OBESITY) {
            return SMALL_IMPACT;
        }
        return LARGE_IMPACT;
    }

    static boolean canTriggerHeavyJump(long currentGameTime, long lastHeavyJumpTime) {
        return currentGameTime - lastHeavyJumpTime >= HEAVY_JUMP_COOLDOWN_TICKS;
    }

    static boolean canShowCooldownMessage(long currentGameTime, long lastMessageTime) {
        return currentGameTime - lastMessageTime >= COOLDOWN_MESSAGE_INTERVAL_TICKS;
    }

    static boolean isProtectedBlock(Block block) {
        return block == Blocks.BEDROCK
                || block == Blocks.END_PORTAL_FRAME
                || block == Blocks.END_PORTAL
                || block == Blocks.NETHER_PORTAL
                || block == Blocks.COMMAND_BLOCK
                || block == Blocks.CHAIN_COMMAND_BLOCK
                || block == Blocks.REPEATING_COMMAND_BLOCK
                || block == Blocks.BARRIER
                || block == Blocks.STRUCTURE_BLOCK
                || block == Blocks.STRUCTURE_VOID
                || block == Blocks.JIGSAW;
    }

    private static void showCooldownMessageIfNeeded(ServerPlayer player, FlavorPlayerComponent component, long currentGameTime) {
        if (!canShowCooldownMessage(currentGameTime, component.getLastHeavyJumpCooldownMessageTime())) {
            return;
        }

        component.setLastHeavyJumpCooldownMessageTime(currentGameTime);
        player.displayClientMessage(COOLDOWN_MESSAGE, true);
    }

    private static void applyImpact(ServerPlayer player, ImpactProfile profile) {
        ServerLevel level = player.serverLevel();
        BlockPos center = player.blockPosition().below();

        playImpactEffects(level, center, profile);
        if (HEAVY_JUMP_BREAK_BLOCKS) {
            carveCrater(level, center, profile);
        }
        knockBackNearbyMobs(player, level, center, profile);
    }

    private static void playImpactEffects(ServerLevel level, BlockPos center, ImpactProfile profile) {
        double x = center.getX() + 0.5D;
        double y = center.getY() + 1.0D;
        double z = center.getZ() + 0.5D;

        level.playSound(null, center, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                profile.soundVolume(), profile.soundPitch());
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z,
                profile.explosionParticles(), 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y + 0.2D, z,
                profile.smokeParticles(), profile.radius() * 0.35D, 0.25D, profile.radius() * 0.35D, 0.03D);
    }

    private static void carveCrater(ServerLevel level, BlockPos center, ImpactProfile profile) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int dx = -profile.radius(); dx <= profile.radius(); dx++) {
            for (int dz = -profile.radius(); dz <= profile.radius(); dz++) {
                if (dx * dx + dz * dz > profile.radius() * profile.radius()) {
                    continue;
                }

                for (int dy = 0; dy < profile.depth(); dy++) {
                    mutablePos.set(center.getX() + dx, center.getY() - dy, center.getZ() + dz);
                    BlockState state = level.getBlockState(mutablePos);
                    if (canBreakBlock(level, mutablePos, state)) {
                        level.setBlock(mutablePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private static boolean canBreakBlock(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.isAir() || isProtectedBlock(state.getBlock())) {
            return false;
        }
        if (!state.getFluidState().isEmpty()) {
            return false;
        }
        return state.getDestroySpeed(level, pos) >= 0.0F;
    }

    private static void knockBackNearbyMobs(ServerPlayer player, ServerLevel level, BlockPos center, ImpactProfile profile) {
        double range = profile.radius() + 2.0D;
        AABB area = new AABB(center).inflate(range);

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, HeavyJumpImpactHandler::canKnockBack)) {
            if (entity == player) {
                continue;
            }

            entity.knockback(profile.knockbackStrength(), player.getX() - entity.getX(), player.getZ() - entity.getZ());
            if (profile.damage() > 0.0F) {
                entity.hurt(level.damageSources().generic(), profile.damage());
            }
        }
    }

    private static boolean canKnockBack(LivingEntity entity) {
        return !(entity instanceof Player);
    }

    record ImpactProfile(
            boolean active,
            int radius,
            int depth,
            double knockbackStrength,
            float damage,
            String actionbarText,
            float soundVolume,
            float soundPitch,
            int explosionParticles,
            int smokeParticles) {
    }
}
