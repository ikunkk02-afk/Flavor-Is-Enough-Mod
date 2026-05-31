package com.ikunkk02.flavorisenough.scale;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BodyScaleProfileTest {

	private static final float DELTA = 0.001F;

	@Test
	void mapsObesityValuesToContinuousProfiles() {
		// Extreme values (same as before)
		assertProfile(0,   0, 1.0F,  1.0F,   1.0F,  0.0D);
		assertProfile(100, 4, 1.5F,  0.92F,  1.0F, -0.18D);

		// Stage boundaries (continuous interpolation)
		assertProfile(19, 0, 1.095F, 0.9848F, 1.0F, -0.0342D);
		assertProfile(20, 1, 1.10F,  0.984F,  1.0F, -0.036D);
		assertProfile(39, 1, 1.195F, 0.9688F, 1.0F, -0.0702D);
		assertProfile(40, 2, 1.20F,  0.968F,  1.0F, -0.072D);
		assertProfile(59, 2, 1.295F, 0.9528F, 1.0F, -0.1062D);
		assertProfile(60, 3, 1.30F,  0.952F,  1.0F, -0.108D);
		assertProfile(79, 3, 1.395F, 0.9368F, 1.0F, -0.1422D);
		assertProfile(80, 4, 1.40F,  0.936F,  1.0F, -0.144D);
	}

	@Test
	void smallObesityChangeProducesDifferentScale() {
		BodyScaleProfile p62 = BodyScaleProfile.forObesityValue(62);
		BodyScaleProfile p65 = BodyScaleProfile.forObesityValue(65);

		// Same stage, but scale should differ (continuous)
		assertEquals(3, p62.stageId());
		assertEquals(3, p65.stageId());
		// Width should be different (65 > 62 => wider)
		assertEquals(-1, Float.compare(p62.widthScale(), p65.widthScale()));
	}

	private static void assertProfile(int obesityValue, int stageId,
			float widthScale, float hitboxHeightScale,
			float visualHeightScale, double speedPenalty) {
		BodyScaleProfile profile = BodyScaleProfile.forObesityValue(obesityValue);

		assertEquals(stageId, profile.stageId());
		assertEquals(widthScale, profile.widthScale(), DELTA);
		assertEquals(hitboxHeightScale, profile.hitboxHeightScale(), DELTA);
		assertEquals(visualHeightScale, profile.visualHeightScale(), DELTA);
		assertEquals(speedPenalty, profile.speedPenalty(), DELTA);
	}
}
