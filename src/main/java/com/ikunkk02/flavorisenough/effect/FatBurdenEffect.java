package com.ikunkk02.flavorisenough.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FatBurdenEffect extends MobEffect {
    public static final String MOD_ID = "flavor-is-enough-mod";

    public FatBurdenEffect() {
        super(MobEffectCategory.HARMFUL, 0xD4A054);

        // Movement speed penalty
        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "fat_burden_speed"),
                -0.10D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        // Bonus max health — being fat means more meat to tank hits
        addAttributeModifier(
                Attributes.MAX_HEALTH,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "fat_burden_health"),
                4.0D,
                AttributeModifier.Operation.ADD_VALUE);
    }
}
