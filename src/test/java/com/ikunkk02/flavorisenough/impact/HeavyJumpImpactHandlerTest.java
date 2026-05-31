package com.ikunkk02.flavorisenough.impact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HeavyJumpImpactHandlerTest {
    @BeforeAll
    static void bootstrapMinecraftRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void profileRequiresConfiguredMinimumObesity() {
        HeavyJumpImpactHandler.ImpactProfile inactive = HeavyJumpImpactHandler.profileForObesity(59);

        assertFalse(inactive.active());
        assertEquals(0, inactive.radius());
        assertEquals(0, inactive.depth());
    }

    @Test
    void obesitySixtyToSeventyNineUsesSmallImpact() {
        HeavyJumpImpactHandler.ImpactProfile atSixty = HeavyJumpImpactHandler.profileForObesity(60);
        HeavyJumpImpactHandler.ImpactProfile atSeventyNine = HeavyJumpImpactHandler.profileForObesity(79);

        assertTrue(atSixty.active());
        assertEquals(3, atSixty.radius());
        assertEquals(3, atSixty.depth());
        assertEquals(0.6D, atSixty.knockbackStrength());
        assertEquals(1.0F, atSixty.damage());
        assertEquals("重踏冲击！", atSixty.actionbarText());

        assertEquals(atSixty, atSeventyNine);
    }

    @Test
    void obesityEightyAndAboveUsesLargeImpact() {
        HeavyJumpImpactHandler.ImpactProfile profile = HeavyJumpImpactHandler.profileForObesity(80);

        assertTrue(profile.active());
        assertEquals(5, profile.radius());
        assertEquals(5, profile.depth());
        assertEquals(1.0D, profile.knockbackStrength());
        assertEquals(2.0F, profile.damage());
        assertEquals("严重负担重踏！", profile.actionbarText());
    }

    @Test
    void heavyJumpCooldownAllowsFirstUseAndThenRequiresConfiguredDelay() {
        assertTrue(HeavyJumpImpactHandler.canTriggerHeavyJump(0L, -100L));
        assertFalse(HeavyJumpImpactHandler.canTriggerHeavyJump(1040L, 1000L));
        assertTrue(HeavyJumpImpactHandler.canTriggerHeavyJump(1100L, 1000L));
    }

    @Test
    void cooldownMessageIsLimitedToOncePerSecond() {
        assertTrue(HeavyJumpImpactHandler.canShowCooldownMessage(20L, 0L));
        assertFalse(HeavyJumpImpactHandler.canShowCooldownMessage(39L, 20L));
        assertTrue(HeavyJumpImpactHandler.canShowCooldownMessage(40L, 20L));
    }

    @Test
    void protectedBlocksAreNeverBreakableByHeavyJump() {
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.BEDROCK));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.END_PORTAL_FRAME));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.END_PORTAL));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.NETHER_PORTAL));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.COMMAND_BLOCK));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.CHAIN_COMMAND_BLOCK));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.REPEATING_COMMAND_BLOCK));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.BARRIER));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.STRUCTURE_BLOCK));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.STRUCTURE_VOID));
        assertTrue(HeavyJumpImpactHandler.isProtectedBlock(Blocks.JIGSAW));

        assertFalse(HeavyJumpImpactHandler.isProtectedBlock(Blocks.STONE));
    }
}
