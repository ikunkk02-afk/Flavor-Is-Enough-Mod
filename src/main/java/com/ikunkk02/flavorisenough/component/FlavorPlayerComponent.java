package com.ikunkk02.flavorisenough.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.CopyableComponent;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class FlavorPlayerComponent implements ComponentV3, CopyableComponent<FlavorPlayerComponent>, AutoSyncedComponent {
	private static final int MIN_VALUE = 0;
	private static final int MAX_VALUE = 100;
	private static final long INITIAL_HEAVY_JUMP_TIME = -100L;
	private static final long INITIAL_HEAVY_JUMP_COOLDOWN_MESSAGE_TIME = -20L;

	private static final String FLAVOR_VALUE_KEY = "FlavorValue";
	private static final String OBESITY_VALUE_KEY = "ObesityValue";
	private static final String HEALTH_VALUE_KEY = "HealthValue";
	private static final String STOMACH_LOAD_KEY = "StomachLoad";
	private static final String LAST_FAT_BURDEN_TIME_KEY = "LastFatBurdenTime";
	private static final String FIRST_FAT_BURDEN_TRIGGER_KEY = "FirstFatBurdenTrigger";
	private static final String LAST_HUNGER_TIME_KEY = "LastHungerTime";
	private static final String LAST_HEAVY_JUMP_TIME_KEY = "LastHeavyJumpTime";
	private static final String LAST_HEAVY_JUMP_COOLDOWN_MESSAGE_TIME_KEY = "LastHeavyJumpCooldownMessageTime";
	private static final String EXERCISE_VALUE_KEY = "ExerciseValue";
	private static final String LAST_EXERCISE_REWARD_TIME_KEY = "LastExerciseRewardTime";
	private static final String LAST_WORKOUT_MESSAGE_TIME_KEY = "LastWorkoutMessageTime";
	private static final String FUN_MODE_ACTIVATED_KEY = "FunModeActivated";
	private static final String FUN_MODE_FOOD_EATEN_KEY = "FunModeFoodEaten";
	private static final String FUN_MODE_OFFERING_COUNT_KEY = "FunModeOfferingCount";

	private int flavorValue;
	private int obesityValue;
	private int healthValue = MAX_VALUE;
	private int stomachLoad;
	private long lastFatBurdenEffectTime;
	private boolean firstFatBurdenTrigger;
	private long lastHungerEffectTime;
	private long lastHeavyJumpTime = INITIAL_HEAVY_JUMP_TIME;
	private long lastHeavyJumpCooldownMessageTime = INITIAL_HEAVY_JUMP_COOLDOWN_MESSAGE_TIME;
	private int exerciseValue;
	private long lastExerciseRewardTime;
	private long lastWorkoutMessageTime;
	private boolean funModeActivated;
	private int funModeFoodEaten;
	private int funModeOfferingCount;

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

	public long getLastHeavyJumpTime() {
		return lastHeavyJumpTime;
	}

	public void setLastHeavyJumpTime(long time) {
		this.lastHeavyJumpTime = time;
	}

	public long getLastHeavyJumpCooldownMessageTime() {
		return lastHeavyJumpCooldownMessageTime;
	}

	public void setLastHeavyJumpCooldownMessageTime(long time) {
		this.lastHeavyJumpCooldownMessageTime = time;
	}

	public int getExerciseValue() {
		return exerciseValue;
	}

	public void setExerciseValue(int value) {
		this.exerciseValue = clamp(value);
	}

	public void addExerciseValue(int amount) {
		setExerciseValue(exerciseValue + amount);
	}

	public long getLastExerciseRewardTime() {
		return lastExerciseRewardTime;
	}

	public void setLastExerciseRewardTime(long time) {
		this.lastExerciseRewardTime = time;
	}

	public long getLastWorkoutMessageTime() {
		return lastWorkoutMessageTime;
	}

	public void setLastWorkoutMessageTime(long time) {
		this.lastWorkoutMessageTime = time;
	}

	public boolean isFunModeActivated() {
		return funModeActivated;
	}

	public void setFunModeActivated(boolean activated) {
		this.funModeActivated = activated;
	}

	public int getFunModeFoodEaten() {
		return funModeFoodEaten;
	}

	public void setFunModeFoodEaten(int count) {
		this.funModeFoodEaten = Math.max(0, count);
	}

	public void incrementFunModeFoodEaten() {
		this.funModeFoodEaten++;
	}

	public int getFunModeOfferingCount() {
		return funModeOfferingCount;
	}

	public void setFunModeOfferingCount(int count) {
		this.funModeOfferingCount = Math.max(0, count);
	}

	public void addFunModeOffering(int amount) {
		this.funModeOfferingCount += amount;
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
		lastHeavyJumpTime = tag.contains(LAST_HEAVY_JUMP_TIME_KEY)
				? tag.getLong(LAST_HEAVY_JUMP_TIME_KEY)
				: INITIAL_HEAVY_JUMP_TIME;
		lastHeavyJumpCooldownMessageTime = tag.contains(LAST_HEAVY_JUMP_COOLDOWN_MESSAGE_TIME_KEY)
				? tag.getLong(LAST_HEAVY_JUMP_COOLDOWN_MESSAGE_TIME_KEY)
				: INITIAL_HEAVY_JUMP_COOLDOWN_MESSAGE_TIME;
		exerciseValue = tag.getInt(EXERCISE_VALUE_KEY);
		lastExerciseRewardTime = tag.getLong(LAST_EXERCISE_REWARD_TIME_KEY);
		lastWorkoutMessageTime = tag.getLong(LAST_WORKOUT_MESSAGE_TIME_KEY);
		funModeActivated = tag.getBoolean(FUN_MODE_ACTIVATED_KEY);
		funModeFoodEaten = tag.getInt(FUN_MODE_FOOD_EATEN_KEY);
		funModeOfferingCount = tag.getInt(FUN_MODE_OFFERING_COUNT_KEY);
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
		tag.putLong(LAST_HEAVY_JUMP_TIME_KEY, lastHeavyJumpTime);
		tag.putLong(LAST_HEAVY_JUMP_COOLDOWN_MESSAGE_TIME_KEY, lastHeavyJumpCooldownMessageTime);
		tag.putInt(EXERCISE_VALUE_KEY, exerciseValue);
		tag.putLong(LAST_EXERCISE_REWARD_TIME_KEY, lastExerciseRewardTime);
		tag.putLong(LAST_WORKOUT_MESSAGE_TIME_KEY, lastWorkoutMessageTime);
		tag.putBoolean(FUN_MODE_ACTIVATED_KEY, funModeActivated);
		tag.putInt(FUN_MODE_FOOD_EATEN_KEY, funModeFoodEaten);
		tag.putInt(FUN_MODE_OFFERING_COUNT_KEY, funModeOfferingCount);
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
		lastHeavyJumpTime = other.lastHeavyJumpTime;
		lastHeavyJumpCooldownMessageTime = other.lastHeavyJumpCooldownMessageTime;
		exerciseValue = other.exerciseValue;
		lastExerciseRewardTime = other.lastExerciseRewardTime;
		lastWorkoutMessageTime = other.lastWorkoutMessageTime;
		funModeActivated = other.funModeActivated;
		funModeFoodEaten = other.funModeFoodEaten;
		funModeOfferingCount = other.funModeOfferingCount;
	}

	private static int clamp(int value) {
		return Math.max(MIN_VALUE, Math.min(MAX_VALUE, value));
	}
}
