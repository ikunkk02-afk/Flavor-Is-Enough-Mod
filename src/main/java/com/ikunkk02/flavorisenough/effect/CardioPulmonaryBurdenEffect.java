package com.ikunkk02.flavorisenough.effect;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CardioPulmonaryBurdenEffect extends MobEffect {
    public CardioPulmonaryBurdenEffect() {
        super(MobEffectCategory.HARMFUL, 0x7E3B2F);

        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "cardiopulmonary_burden_speed"),
                -0.08D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        addAttributeModifier(
                Attributes.ATTACK_SPEED,
                ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "cardiopulmonary_burden_attack_speed"),
                -0.12D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
