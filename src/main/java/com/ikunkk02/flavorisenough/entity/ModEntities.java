package com.ikunkk02.flavorisenough.entity;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ModEntities {

    public static final EntityType<LiangziEntity> LIANGZI = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            id("liangzi"),
            EntityType.Builder.of(LiangziEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(10)
                    .build("liangzi")
    );

    public static final Item LIANGZI_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM,
            id("liangzi_spawn_egg"),
            new SpawnEggItem(LIANGZI, 0xCC4444, 0xFFD700, new Item.Properties())
    );

    private ModEntities() {
    }

    public static void register() {
        FlavorIsEnoughMod.LOGGER.info("Registering entities for {}", FlavorIsEnoughMod.MOD_ID);

        // Register attributes
        FabricDefaultAttributeRegistry.register(LIANGZI, LiangziEntity.createAttributes());

        // Register spawn placement - can spawn in plains and forest biomes
        SpawnPlacements.register(LIANGZI, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                LiangziEntity::checkMobSpawnRules);

        // Add to many common biomes
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS,
                        Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST,
                        Biomes.DARK_FOREST, Biomes.MEADOW, Biomes.SAVANNA,
                        Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_HILLS,
                        Biomes.TAIGA, Biomes.OLD_GROWTH_BIRCH_FOREST,
                        Biomes.CHERRY_GROVE),
                MobCategory.CREATURE,
                LIANGZI,
                20, 1, 3
        );
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, name);
    }
}
