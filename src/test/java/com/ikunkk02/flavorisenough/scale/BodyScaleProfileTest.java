package com.ikunkk02.flavorisenough.scale;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BodyScaleProfileTest {
	@Test
	void mapsObesityValuesToExactStageProfiles() {
		assertProfile(0, 0, 1.0F, 1.0F, 1.0F, 0.0D);
		assertProfile(19, 0, 1.0F, 1.0F, 1.0F, 0.0D);
		assertProfile(20, 1, 1.08F, 0.98F, 1.0F, -0.03D);
		assertProfile(39, 1, 1.08F, 0.98F, 1.0F, -0.03D);
		assertProfile(40, 2, 1.18F, 0.96F, 1.0F, -0.07D);
		assertProfile(59, 2, 1.18F, 0.96F, 1.0F, -0.07D);
		assertProfile(60, 3, 1.32F, 0.94F, 1.0F, -0.12D);
		assertProfile(79, 3, 1.32F, 0.94F, 1.0F, -0.12D);
		assertProfile(80, 4, 1.5F, 0.92F, 1.0F, -0.18D);
		assertProfile(100, 4, 1.5F, 0.92F, 1.0F, -0.18D);
	}

	private static void assertProfile(int obesityValue, int stageId, float widthScale, float hitboxHeightScale, float visualHeightScale, double speedPenalty) {
		BodyScaleProfile profile = BodyScaleProfile.forObesityValue(obesityValue);

		assertEquals(stageId, profile.stageId());
		assertEquals(widthScale, profile.widthScale());
		assertEquals(hitboxHeightScale, profile.hitboxHeightScale());
		assertEquals(visualHeightScale, profile.visualHeightScale());
		assertEquals(speedPenalty, profile.speedPenalty());
	}
}
