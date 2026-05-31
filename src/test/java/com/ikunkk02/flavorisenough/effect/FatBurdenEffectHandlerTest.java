package com.ikunkk02.flavorisenough.effect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FatBurdenEffectHandlerTest {
    @Test
    void continuousEffectsStartAtObesityStageThree() {
        assertFalse(FatBurdenEffectHandler.shouldApplyBurdenEffects(2, false));
        assertTrue(FatBurdenEffectHandler.shouldApplyBurdenEffects(3, false));
        assertTrue(FatBurdenEffectHandler.shouldApplyBurdenEffects(4, false));
        assertTrue(FatBurdenEffectHandler.shouldApplyBurdenEffects(0, true));
    }

    @Test
    void fatBurdenEffectCanDriveSevereSecondaryEffectsWithoutObesityStage() {
        assertEquals(0, FatBurdenEffectHandler.burdenAmplifierForState(0, true, 0));
        assertEquals(1, FatBurdenEffectHandler.burdenAmplifierForState(0, true, 1));
        assertEquals(1, FatBurdenEffectHandler.burdenAmplifierForState(4, true, 0));
        assertEquals(1, FatBurdenEffectHandler.burdenAmplifierForState(4, false, 0));
    }

    @Test
    void resistanceRefreshesWhenMissingLowDurationOrUnderAmplified() {
        assertTrue(FatBurdenEffectHandler.shouldRefreshEffect(-1, 0, 0));
        assertTrue(FatBurdenEffectHandler.shouldRefreshEffect(0, 99, 0));
        assertTrue(FatBurdenEffectHandler.shouldRefreshEffect(0, 140, 1));
        assertFalse(FatBurdenEffectHandler.shouldRefreshEffect(0, 140, 0));
        assertFalse(FatBurdenEffectHandler.shouldRefreshEffect(2, 20, 1));
    }

    @Test
    void craterCooldownPreventsChainExplosions() {
        assertTrue(FatBurdenEffectHandler.shouldCreateCrater(1000L, 0L));
        assertFalse(FatBurdenEffectHandler.shouldCreateCrater(1040L, 1000L));
        assertTrue(FatBurdenEffectHandler.shouldCreateCrater(1100L, 1000L));
    }

    @Test
    void normalObeseLandingCreatesVisibleCrater() {
        FatBurdenEffectHandler.CraterProfile obeseLanding =
                FatBurdenEffectHandler.craterProfileForLanding(0.05D, 3, false, 0);
        FatBurdenEffectHandler.CraterProfile severeLanding =
                FatBurdenEffectHandler.craterProfileForLanding(0.05D, 4, false, 0);
        FatBurdenEffectHandler.CraterProfile stomachTriggeredLanding =
                FatBurdenEffectHandler.craterProfileForLanding(0.05D, 0, true, 0);

        assertEquals(2, obeseLanding.radius());
        assertEquals(1, obeseLanding.depth());
        assertEquals(3, severeLanding.radius());
        assertEquals(2, severeLanding.depth());
        assertEquals(2, stomachTriggeredLanding.radius());
        assertEquals(1, stomachTriggeredLanding.depth());
    }

    @Test
    void inactiveLandingDoesNotCreateCrater() {
        FatBurdenEffectHandler.CraterProfile profile =
                FatBurdenEffectHandler.craterProfileForLanding(1.0D, 2, false, 0);

        assertEquals(0, profile.radius());
        assertEquals(0, profile.depth());
    }
}
