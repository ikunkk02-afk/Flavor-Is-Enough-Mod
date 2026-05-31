package com.ikunkk02.flavorisenough.appearance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FatBodySkinUvTest {
	@Test
	void mapsBellyFrontsToPlayerTorsoBaseFront() {
		assertEquals(64, FatBodySkinUv.TEXTURE_WIDTH);
		assertEquals(64, FatBodySkinUv.TEXTURE_HEIGHT);

		for (FatBodySkinUv.BoxUv boxUv : FatBodySkinUv.bellyUvs()) {
			assertTrue(boxUv.frontU() >= 19.0F, boxUv.name() + " starts outside the torso front");
			assertTrue(boxUv.frontU() <= 20.0F, boxUv.name() + " should start at the torso front");
			assertTrue(boxUv.frontMaxU() <= 29.6F, boxUv.name() + " samples too far past the torso front");
			assertTrue(boxUv.frontV() >= 20.0F, boxUv.name() + " starts above the torso front");
			assertTrue(boxUv.frontMaxV() <= 32.0F, boxUv.name() + " samples below the torso front");
		}
	}

	@Test
	void keepsPaddingUvOnPlayerLimbRegions() {
		assertEquals(40, FatBodySkinUv.RIGHT_ARM_BASE.u());
		assertEquals(16, FatBodySkinUv.RIGHT_ARM_BASE.v());
		assertEquals(32, FatBodySkinUv.LEFT_ARM_BASE.u());
		assertEquals(48, FatBodySkinUv.LEFT_ARM_BASE.v());
		assertEquals(0, FatBodySkinUv.RIGHT_LEG_OUTER.u());
		assertEquals(32, FatBodySkinUv.RIGHT_LEG_OUTER.v());
		assertEquals(0, FatBodySkinUv.LEFT_LEG_OUTER.u());
		assertEquals(47, FatBodySkinUv.LEFT_LEG_OUTER.v());
	}
}
