package com.ikunkk02.flavorisenough.effect;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 心肺负荷 — 肥胖阶段 ≥4 时施加。
 * 心肺部承受巨大压力，周期性受伤 + 严重缓慢。
 */
public class CardioPulmonaryBurdenEffect extends MobEffect {
    private static final int TICK_INTERVAL = 30; // 每 1.5 秒

    public CardioPulmonaryBurdenEffect() {
        super(MobEffectCategory.HARMFUL, 0x7E3B2F);

        // 移速 −8%（每级）
        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "cardiopulmonary_burden_speed"),
                -0.08D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        // 攻速 −12%（每级）
        addAttributeModifier(
                Attributes.ATTACK_SPEED,
                ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "cardiopulmonary_burden_attack_speed"),
                -0.12D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % TICK_INTERVAL == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 心肺过载：每跳扣血（amplifier 0 = 1 点，amplifier 1 = 2 点）
        float damage = 1.0F + amplifier;
        entity.hurt(entity.damageSources().generic(), damage);

        // 严重气喘：短暂但剧烈的缓慢
        entity.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN, 60, 2 + amplifier,
                false, false, true));

        return true;
    }
}
