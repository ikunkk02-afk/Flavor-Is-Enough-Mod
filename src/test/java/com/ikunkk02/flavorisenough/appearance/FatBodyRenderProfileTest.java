package com.ikunkk02.flavorisenough.appearance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FatBodyRenderProfileTest {
	@Test
	void mapsObesityValuesToAppearanceStages() {
		assertProfile(0, 0, false, false);
		assertProfile(19, 0, false, false);
		assertProfile(20, 1, true, false);
		assertProfile(39, 1, true, false);
		assertProfile(40, 2, true, false);
		assertProfile(59, 2, true, false);
		assertProfile(60, 3, true, true);
		assertProfile(79, 3, true, true);
		assertProfile(80, 4, true, true);
		assertProfile(100, 4, true, true);
	}

	@Test
	void keepsLimbsHiddenForSlightlyFatStage() {
		FatBodyRenderProfile profile = FatBodyRenderProfile.forObesityValue(20);

		assertTrue(profile.rendersLayer());
		assertFalse(profile.rendersLimbs());
	}

	private static void assertProfile(int obesityValue, int stageId, boolean rendersLayer, boolean rendersLimbs) {
		FatBodyRenderProfile profile = FatBodyRenderProfile.forObesityValue(obesityValue);

		assertEquals(stageId, profile.stageId());
		assertEquals(rendersLayer, profile.rendersLayer());
		assertEquals(rendersLimbs, profile.rendersLimbs());
	}
}
