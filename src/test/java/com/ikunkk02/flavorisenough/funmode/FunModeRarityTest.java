package com.ikunkk02.flavorisenough.funmode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunModeRarityTest {
    @Test
    void ordinaryBlocksGiveCommonScore() {
        assertEquals(1, FunModeRarity.score("minecraft", "dirt"));
        assertEquals(1, FunModeRarity.score("minecraft", "stone"));
        assertEquals(1, FunModeRarity.score("minecraft", "oak_log"));
    }

    @Test
    void usefulResourceBlocksGiveModerateScore() {
        assertEquals(3, FunModeRarity.score("minecraft", "iron_block"));
        assertEquals(3, FunModeRarity.score("minecraft", "redstone_block"));
        assertEquals(3, FunModeRarity.score("minecraft", "copper_ore"));
    }

    @Test
    void rareBlocksGiveLargeScore() {
        assertEquals(8, FunModeRarity.score("minecraft", "diamond_block"));
        assertEquals(8, FunModeRarity.score("minecraft", "emerald_ore"));
        assertEquals(8, FunModeRarity.score("minecraft", "obsidian"));
    }

    @Test
    void specialEndAndNetherItemsGiveBiggerScore() {
        assertEquals(12, FunModeRarity.score("minecraft", "ancient_debris"));
        assertEquals(12, FunModeRarity.score("minecraft", "crying_obsidian"));
        assertEquals(12, FunModeRarity.score("minecraft", "end_stone"));
    }

    @Test
    void godlikeBlocksGiveMaximumScore() {
        assertEquals(25, FunModeRarity.score("minecraft", "dragon_egg"));
        assertEquals(25, FunModeRarity.score("minecraft", "netherite_block"));
        assertEquals(25, FunModeRarity.score("minecraft", "beacon"));
    }

    @Test
    void rarerBlocksTakeLongerToEat() {
        assertEquals(0.8F, FunModeRarity.eatSeconds(1));
        assertEquals(1.0F, FunModeRarity.eatSeconds(3));
        assertEquals(1.4F, FunModeRarity.eatSeconds(8));
        assertEquals(1.8F, FunModeRarity.eatSeconds(12));
        assertEquals(2.4F, FunModeRarity.eatSeconds(25));
    }
}
