package com.ikunkk02.flavorisenough.funmode;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Scores fun-mode food by rarity. The score is used as Big Stomach power XP:
 * ordinary blocks give tiny progress, rare blocks/items give a large jump.
 */
public final class FunModeRarity {
    public static final int COMMON_SCORE = 1;
    public static final int USEFUL_SCORE = 3;
    public static final int RARE_SCORE = 8;
    public static final int SPECIAL_SCORE = 12;
    public static final int GODLIKE_SCORE = 25;

    private FunModeRarity() {
    }

    public static int score(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return COMMON_SCORE;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return score(id.getNamespace(), id.getPath());
    }

    public static int score(String namespace, String path) {
        String normalizedNamespace = namespace == null ? "" : namespace.toLowerCase(Locale.ROOT);
        String normalizedPath = path == null ? "" : path.toLowerCase(Locale.ROOT);

        if (isGodlike(normalizedNamespace, normalizedPath)) {
            return GODLIKE_SCORE;
        }
        if (isSpecial(normalizedNamespace, normalizedPath)) {
            return SPECIAL_SCORE;
        }
        if (isRare(normalizedNamespace, normalizedPath)) {
            return RARE_SCORE;
        }
        if (isUseful(normalizedNamespace, normalizedPath)) {
            return USEFUL_SCORE;
        }
        return COMMON_SCORE;
    }

    public static FoodProperties syntheticFood(ItemStack stack) {
        int score = score(stack);
        int nutrition = Math.min(12, 3 + score / 2);
        float saturation = Math.min(2.0F, 0.4F + score / 20.0F);
        float eatSeconds = eatSeconds(score);
        return new FoodProperties(nutrition, saturation, true, eatSeconds, Optional.empty(), List.of());
    }

    public static float eatSeconds(int score) {
        if (score >= GODLIKE_SCORE) {
            return 2.4F;
        }
        if (score >= SPECIAL_SCORE) {
            return 1.8F;
        }
        if (score >= RARE_SCORE) {
            return 1.4F;
        }
        if (score >= USEFUL_SCORE) {
            return 1.0F;
        }
        return 0.8F;
    }

    public static String tierName(int score) {
        if (score >= GODLIKE_SCORE) {
            return "godlike";
        }
        if (score >= SPECIAL_SCORE) {
            return "special";
        }
        if (score >= RARE_SCORE) {
            return "rare";
        }
        if (score >= USEFUL_SCORE) {
            return "useful";
        }
        return "common";
    }

    private static boolean isGodlike(String namespace, String path) {
        return namespace.equals("minecraft") && (
                path.equals("dragon_egg")
                        || path.equals("netherite_block")
                        || path.equals("beacon")
                        || path.equals("end_portal_frame")
                        || path.equals("command_block")
                        || path.equals("chain_command_block")
                        || path.equals("repeating_command_block")
                        || path.equals("structure_block")
                        || path.equals("jigsaw")
                        || path.equals("barrier")
                        || path.equals("bedrock"));
    }

    private static boolean isSpecial(String namespace, String path) {
        return namespace.equals("minecraft") && (
                path.contains("netherite")
                        || path.contains("ancient_debris")
                        || path.contains("elytra")
                        || path.contains("totem")
                        || path.contains("shulker_shell")
                        || path.contains("shulker_box")
                        || path.contains("echo_shard")
                        || path.contains("disc_fragment")
                        || path.contains("heart_of_the_sea")
                        || path.contains("conduit")
                        || path.contains("crying_obsidian")
                        || path.contains("respawn_anchor")
                        || path.contains("purpur")
                        || path.contains("chorus")
                        || path.contains("end_stone")
                        || path.contains("end_crystal"));
    }

    private static boolean isRare(String namespace, String path) {
        return namespace.equals("minecraft") && (
                path.contains("diamond")
                        || path.contains("emerald")
                        || path.contains("gold")
                        || path.contains("obsidian")
                        || path.contains("amethyst")
                        || path.contains("lapis")
                        || path.contains("enchanting_table")
                        || path.contains("ender")
                        || path.contains("blaze")
                        || path.contains("ghast")
                        || path.contains("nether_star"));
    }

    private static boolean isUseful(String namespace, String path) {
        return namespace.equals("minecraft") && (
                path.contains("iron")
                        || path.contains("copper")
                        || path.contains("coal")
                        || path.contains("redstone")
                        || path.contains("quartz")
                        || path.contains("glowstone")
                        || path.contains("prismarine")
                        || path.contains("slime")
                        || path.contains("honey")
                        || path.contains("sculk")
                        || path.contains("magma")
                        || path.contains("bookshelf")
                        || path.contains("anvil"));
    }
}
