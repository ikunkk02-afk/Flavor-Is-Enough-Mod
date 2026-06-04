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
 * 脂肪肝 — 肥胖 ≥75 时施加。
 * 肝脏无法正常代谢，周期性饥饿 + 虚弱。
 */
public class FattyLiverEffect extends MobEffect {
    private static final int TICK_INTERVAL = 40; // 每 2 秒

    public FattyLiverEffect() {
        super(MobEffectCategory.HARMFUL, 0xC9A64A);

        // 最大生命值 −2（每级）
        addAttributeModifier(
                Attributes.MAX_HEALTH,
                ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "fatty_liver_health"),
                -2.0D,
                AttributeModifier.Operation.ADD_VALUE);

        // 攻击力 −5%（每级）
        addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "fatty_liver_strength"),
                -0.05D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % TICK_INTERVAL == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 饥饿：身体无法有效利用营养，食物消耗加快
        entity.addEffect(new MobEffectInstance(
                MobEffects.HUNGER, 100, amplifier,
                false, false, true));

        // 虚弱：脂肪肝导致全身乏力（amplifier 0 = 攻击 −4，amplifier 1 = 攻击 −7）
        entity.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS, 200, amplifier,
                false, false, true));

        return true;
    }
}
