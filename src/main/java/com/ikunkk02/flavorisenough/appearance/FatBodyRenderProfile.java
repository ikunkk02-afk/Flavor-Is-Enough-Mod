package com.ikunkk02.flavorisenough.appearance;

public record FatBodyRenderProfile(
		int stageId,
		boolean rendersLayer,
		boolean rendersLimbs) {
	public static FatBodyRenderProfile forObesityValue(int obesityValue) {
		if (obesityValue < 20) {
			return new FatBodyRenderProfile(0, false, false);
		}
		if (obesityValue < 40) {
			return new FatBodyRenderProfile(1, true, false);
		}
		if (obesityValue < 60) {
			return new FatBodyRenderProfile(2, true, false);
		}
		if (obesityValue < 80) {
			return new FatBodyRenderProfile(3, true, true);
		}
		return new FatBodyRenderProfile(4, true, true);
	}
}
