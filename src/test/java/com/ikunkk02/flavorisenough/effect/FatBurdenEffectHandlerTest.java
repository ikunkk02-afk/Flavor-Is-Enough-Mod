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
    void obesityDiseaseEffectsUseMildAndSevereThresholds() {
        assertFalse(FatBurdenEffectHandler.shouldApplyMildObesityDisease(74));
        assertTrue(FatBurdenEffectHandler.shouldApplyMildObesityDisease(75));

        assertFalse(FatBurdenEffectHandler.shouldApplySevereObesityDisease(3));
        assertTrue(FatBurdenEffectHandler.shouldApplySevereObesityDisease(4));
    }

    @Test
    void resistanceRefreshesWhenMissingLowDurationOrUnderAmplified() {
        assertTrue(FatBurdenEffectHandler.shouldRefreshEffect(-1, 0, 0));
        assertTrue(FatBurdenEffectHandler.shouldRefreshEffect(0, 99, 0));
        assertTrue(FatBurdenEffectHandler.shouldRefreshEffect(0, 140, 1));
        assertFalse(FatBurdenEffectHandler.shouldRefreshEffect(0, 140, 0));
        assertFalse(FatBurdenEffectHandler.shouldRefreshEffect(2, 20, 1));
    }
}
