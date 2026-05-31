package com.ikunkk02.flavorisenough.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class FatBurdenEffect extends MobEffect {
    public static final UUID MOVEMENT_SPEED_MODIFIER_ID = UUID.fromString("3e7e8c5a-1b9d-4a3f-8c2e-6f1a9b0d4e7c");

    public FatBurdenEffect() {
        super(MobEffectCategory.HARMFUL, 0xD4A054);

        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath("flavor-is-enough-mod", "fat_burden_speed"),
                -0.10D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
