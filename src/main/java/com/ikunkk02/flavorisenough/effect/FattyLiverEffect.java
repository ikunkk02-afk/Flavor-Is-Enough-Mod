package com.ikunkk02.flavorisenough.effect;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FattyLiverEffect extends MobEffect {
    public FattyLiverEffect() {
        super(MobEffectCategory.HARMFUL, 0xC9A64A);

        addAttributeModifier(
                Attributes.MAX_HEALTH,
                ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "fatty_liver_health"),
                -2.0D,
                AttributeModifier.Operation.ADD_VALUE);

        addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "fatty_liver_strength"),
                -0.05D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
