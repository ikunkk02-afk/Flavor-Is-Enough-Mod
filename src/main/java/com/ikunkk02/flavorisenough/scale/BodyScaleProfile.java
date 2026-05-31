package com.ikunkk02.flavorisenough.scale;

record BodyScaleProfile(int stageId, float widthScale, float hitboxHeightScale, float visualHeightScale, double speedPenalty) {
	static BodyScaleProfile forObesityValue(int obesityValue) {
		if (obesityValue < 20) {
			return new BodyScaleProfile(0, 1.0F, 1.0F, 1.0F, 0.0D);
		}
		if (obesityValue < 40) {
			return new BodyScaleProfile(1, 1.08F, 0.98F, 1.0F, -0.03D);
		}
		if (obesityValue < 60) {
			return new BodyScaleProfile(2, 1.18F, 0.96F, 1.0F, -0.07D);
		}
		if (obesityValue < 80) {
			return new BodyScaleProfile(3, 1.32F, 0.94F, 1.0F, -0.12D);
		}
		return new BodyScaleProfile(4, 1.5F, 0.92F, 1.0F, -0.18D);
	}
}
