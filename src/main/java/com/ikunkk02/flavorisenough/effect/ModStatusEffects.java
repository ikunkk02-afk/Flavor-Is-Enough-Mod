package com.ikunkk02.flavorisenough.effect;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public final class ModStatusEffects {
    public static final MobEffect FAT_BURDEN = register("fat_burden", new FatBurdenEffect());
    public static final Holder<MobEffect> FAT_BURDEN_HOLDER = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(FAT_BURDEN);

    private ModStatusEffects() {
    }

    public static void register() {
        FlavorIsEnoughMod.LOGGER.info("Registering status effects for {}", FlavorIsEnoughMod.MOD_ID);
    }

    private static MobEffect register(String name, MobEffect effect) {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, id(name), effect);
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, name);
    }
}
