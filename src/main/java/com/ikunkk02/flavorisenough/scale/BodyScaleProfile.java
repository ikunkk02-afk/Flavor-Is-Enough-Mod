package com.ikunkk02.flavorisenough.scale;

record BodyScaleProfile(int stageId, float widthScale, float hitboxHeightScale, float visualHeightScale, double speedPenalty) {

	/** Max width scale at obesity 100. */
	private static final float MAX_WIDTH = 1.5F;
	/** Min hitbox height scale at obesity 100. */
	private static final float MIN_HITBOX_HEIGHT = 0.92F;
	/** Max speed penalty at obesity 100. */
	private static final double MAX_SPEED_PENALTY = -0.18D;

	static BodyScaleProfile forObesityValue(int obesityValue) {
		int stageId = stageIdFor(obesityValue);
		float t = Math.min(1.0F, obesityValue / 100.0F);

		float widthScale = 1.0F + t * (MAX_WIDTH - 1.0F);
		float hitboxHeightScale = 1.0F + t * (MIN_HITBOX_HEIGHT - 1.0F);
		double speedPenalty = t * MAX_SPEED_PENALTY;

		return new BodyScaleProfile(stageId, widthScale, hitboxHeightScale, 1.0F, speedPenalty);
	}

	private static int stageIdFor(int obesityValue) {
		if (obesityValue < 20) return 0;
		if (obesityValue < 40) return 1;
		if (obesityValue < 60) return 2;
		if (obesityValue < 80) return 3;
		return 4;
	}
}
