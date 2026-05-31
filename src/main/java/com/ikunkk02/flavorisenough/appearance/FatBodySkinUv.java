package com.ikunkk02.flavorisenough.appearance;

import java.util.List;

public final class FatBodySkinUv {
	public static final int TEXTURE_WIDTH = 64;
	public static final int TEXTURE_HEIGHT = 64;

	public static final BoxUv BELLY_SMALL_CENTER = torsoBox("belly_small_center", 18, 18, 2.0F, 6.0F, 7.0F);
	public static final BoxUv BELLY_SMALL_LOWER = torsoBox("belly_small_lower", 18, 26, 1.6F, 4.8F, 3.3F);
	public static final BoxUv BELLY_MEDIUM_CENTER = torsoBox("belly_medium_center", 17, 18, 2.7F, 7.6F, 8.4F);
	public static final BoxUv BELLY_MEDIUM_LOWER = torsoBox("belly_medium_lower", 17, 26, 2.2F, 6.4F, 3.4F);
	public static final BoxUv BELLY_MEDIUM_TOP = torsoBox("belly_medium_top", 18, 18, 2.0F, 5.6F, 2.1F);
	public static final BoxUv BELLY_LARGE_CENTER = torsoBox("belly_large_center", 16, 18, 3.3F, 8.8F, 9.2F);
	public static final BoxUv BELLY_LARGE_LOWER = torsoBox("belly_large_lower", 17, 25, 2.8F, 7.6F, 3.6F);
	public static final BoxUv BELLY_LARGE_TOP = torsoBox("belly_large_top", 17, 18, 2.5F, 6.6F, 1.9F);
	public static final BoxUv BELLY_LARGE_FRONT = torsoBox("belly_large_front", 19, 22, 0.7F, 6.2F, 5.1F);
	public static final BoxUv BELLY_HUGE_CENTER = torsoBox("belly_huge_center", 16, 17, 3.9F, 9.6F, 10.0F);
	public static final BoxUv BELLY_HUGE_LOWER = torsoBox("belly_huge_lower", 16, 24, 3.3F, 8.4F, 4.2F);
	public static final BoxUv BELLY_HUGE_TOP = torsoBox("belly_huge_top", 17, 18, 2.9F, 7.2F, 1.9F);
	public static final BoxUv BELLY_HUGE_FRONT = torsoBox("belly_huge_front", 19, 23, 0.8F, 6.8F, 6.8F);
	public static final BoxUv BELLY_HUGE_LOW_FRONT = torsoBox("belly_huge_low_front", 19, 28, 0.8F, 5.4F, 3.1F);

	public static final TextureOffset RIGHT_ARM_BASE = new TextureOffset(40, 16);
	public static final TextureOffset LEFT_ARM_BASE = new TextureOffset(32, 48);
	public static final TextureOffset RIGHT_LEG_OUTER = new TextureOffset(0, 32);
	public static final TextureOffset LEFT_LEG_OUTER = new TextureOffset(0, 47);

	private static final List<BoxUv> BELLY_UVS = List.of(
			BELLY_SMALL_CENTER,
			BELLY_SMALL_LOWER,
			BELLY_MEDIUM_CENTER,
			BELLY_MEDIUM_LOWER,
			BELLY_MEDIUM_TOP,
			BELLY_LARGE_CENTER,
			BELLY_LARGE_LOWER,
			BELLY_LARGE_TOP,
			BELLY_LARGE_FRONT,
			BELLY_HUGE_CENTER,
			BELLY_HUGE_LOWER,
			BELLY_HUGE_TOP,
			BELLY_HUGE_FRONT,
			BELLY_HUGE_LOW_FRONT);

	private FatBodySkinUv() {
	}

	public static List<BoxUv> bellyUvs() {
		return BELLY_UVS;
	}

	private static BoxUv torsoBox(String name, int u, int v, float depth, float width, float height) {
		return new BoxUv(name, u, v, depth, width, height);
	}

	public record TextureOffset(int u, int v) {
	}

	public record BoxUv(String name, int u, int v, float depth, float width, float height) {
		public float frontU() {
			return u + depth;
		}

		public float frontV() {
			return v + depth;
		}

		public float frontMaxU() {
			return frontU() + width;
		}

		public float frontMaxV() {
			return frontV() + height;
		}
	}
}
