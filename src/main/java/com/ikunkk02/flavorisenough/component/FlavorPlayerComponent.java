package com.ikunkk02.flavorisenough.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.CopyableComponent;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class FlavorPlayerComponent implements ComponentV3, CopyableComponent<FlavorPlayerComponent>, AutoSyncedComponent {
	private static final int MIN_VALUE = 0;
	private static final int MAX_VALUE = 100;

	private static final String FLAVOR_VALUE_KEY = "FlavorValue";
	private static final String OBESITY_VALUE_KEY = "ObesityValue";
	private static final String HEALTH_VALUE_KEY = "HealthValue";
	private static final String STOMACH_LOAD_KEY = "StomachLoad";
	private static final String LAST_FAT_BURDEN_TIME_KEY = "LastFatBurdenTime";
	private static final String FIRST_FAT_BURDEN_TRIGGER_KEY = "FirstFatBurdenTrigger";
	private static final String LAST_HUNGER_TIME_KEY = "LastHungerTime";

	private int flavorValue;
	private int obesityValue;
	private int healthValue = MAX_VALUE;
	private int stomachLoad;
	private long lastFatBurdenEffectTime;
	private boolean firstFatBurdenTrigger;
	private long lastHungerEffectTime;

	public int getFlavorValue() {
		return flavorValue;
	}

	public void setFlavorValue(int flavorValue) {
		this.flavorValue = clamp(flavorValue);
	}

	public void addFlavorValue(int amount) {
		setFlavorValue(flavorValue + amount);
	}

	public int getObesityValue() {
		return obesityValue;
	}

	public void setObesityValue(int obesityValue) {
		this.obesityValue = clamp(obesityValue);
	}

	public void addObesityValue(int amount) {
		setObesityValue(obesityValue + amount);
	}

	public int getHealthValue() {
		return healthValue;
	}

	public void setHealthValue(int healthValue) {
		this.healthValue = clamp(healthValue);
	}

	public void addHealthValue(int amount) {
		setHealthValue(healthValue + amount);
	}

	public int getStomachLoad() {
		return stomachLoad;
	}

	public void setStomachLoad(int stomachLoad) {
		this.stomachLoad = clamp(stomachLoad);
	}

	public void addStomachLoad(int amount) {
		setStomachLoad(stomachLoad + amount);
	}

	public int getObesityStageId() {
		if (obesityValue < 20) {
			return 0;
		}
		if (obesityValue < 40) {
			return 1;
		}
		if (obesityValue < 60) {
			return 2;
		}
		if (obesityValue < 80) {
			return 3;
		}
		return 4;
	}

	public String getObesityStageText() {
		return switch (getObesityStageId()) {
			case 1 -> "轻微发福";
			case 2 -> "明显发福";
			case 3 -> "肥胖体态";
			case 4 -> "严重负担";
			default -> "正常体态";
		};
	}

	public long getLastFatBurdenEffectTime() {
		return lastFatBurdenEffectTime;
	}

	public void setLastFatBurdenEffectTime(long time) {
		this.lastFatBurdenEffectTime = time;
	}

	public boolean isFirstFatBurdenTrigger() {
		return !firstFatBurdenTrigger;
	}

	public void markFatBurdenTriggered() {
		this.firstFatBurdenTrigger = true;
	}

	public long getLastHungerEffectTime() {
		return lastHungerEffectTime;
	}

	public void setLastHungerEffectTime(long time) {
		this.lastHungerEffectTime = time;
	}

	@Override
	public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		setFlavorValue(tag.getInt(FLAVOR_VALUE_KEY));
		setObesityValue(tag.getInt(OBESITY_VALUE_KEY));
		setHealthValue(tag.contains(HEALTH_VALUE_KEY) ? tag.getInt(HEALTH_VALUE_KEY) : MAX_VALUE);
		setStomachLoad(tag.getInt(STOMACH_LOAD_KEY));
		lastFatBurdenEffectTime = tag.getLong(LAST_FAT_BURDEN_TIME_KEY);
		firstFatBurdenTrigger = tag.getBoolean(FIRST_FAT_BURDEN_TRIGGER_KEY);
		lastHungerEffectTime = tag.getLong(LAST_HUNGER_TIME_KEY);
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		tag.putInt(FLAVOR_VALUE_KEY, flavorValue);
		tag.putInt(OBESITY_VALUE_KEY, obesityValue);
		tag.putInt(HEALTH_VALUE_KEY, healthValue);
		tag.putInt(STOMACH_LOAD_KEY, stomachLoad);
		tag.putLong(LAST_FAT_BURDEN_TIME_KEY, lastFatBurdenEffectTime);
		tag.putBoolean(FIRST_FAT_BURDEN_TRIGGER_KEY, firstFatBurdenTrigger);
		tag.putLong(LAST_HUNGER_TIME_KEY, lastHungerEffectTime);
	}

	@Override
	public void copyFrom(FlavorPlayerComponent other, HolderLookup.Provider registryLookup) {
		setFlavorValue(other.flavorValue);
		setObesityValue(other.obesityValue);
		setHealthValue(other.healthValue);
		setStomachLoad(other.stomachLoad);
		lastFatBurdenEffectTime = other.lastFatBurdenEffectTime;
		firstFatBurdenTrigger = other.firstFatBurdenTrigger;
		lastHungerEffectTime = other.lastHungerEffectTime;
	}

	private static int clamp(int value) {
		return Math.max(MIN_VALUE, Math.min(MAX_VALUE, value));
	}
}
