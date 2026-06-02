package com.ikunkk02.flavorisenough.entity;

import com.ikunkk02.flavorisenough.item.ModItems;
import com.ikunkk02.flavorisenough.sound.ModSounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class LiangziEntity extends PathfinderMob {

    private static final int MEAT_MIN_NUTRITION = 3;
    private static final int TRADE_SOUND_COOLDOWN = 1200; // 60 seconds, avoids overlapping long voice lines
    private static final float TRADE_SOUND_VOLUME = 0.35F;
    private int lastTradeSoundTick = -TRADE_SOUND_COOLDOWN;

    public LiangziEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EatNearbyFoodGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (isMeat(held)) {
            if (!this.level().isClientSide()) {
                // Consume one meat
                held.shrink(1);

                // Give random mod food
                ItemStack reward = getRandomReward();
                ItemEntity dropped = new ItemEntity(this.level(),
                        this.getX(), this.getY() + 0.5D, this.getZ(), reward);
                this.level().addFreshEntity(dropped);

                // Effects
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getBbHeight() + 0.5D, this.getZ(),
                        5, 0.3D, 0.2D, 0.3D, 0.0D);
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);

                // Trade sound with cooldown to avoid spam
                int tick = this.tickCount;
                if (tick - lastTradeSoundTick >= TRADE_SOUND_COOLDOWN) {
                    this.playSound(ModSounds.LIANGZI_TRADE, TRADE_SOUND_VOLUME, 1.0F);
                    lastTradeSoundTick = tick;
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    private static boolean isMeat(ItemStack stack) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        return food != null && food.nutrition() >= MEAT_MIN_NUTRITION;
    }

    private ItemStack getRandomReward() {
        return switch (this.getRandom().nextInt(4)) {
            case 0 -> new ItemStack(ModItems.FLAVOR_SNACK, 1 + this.getRandom().nextInt(2));
            case 1 -> new ItemStack(ModItems.BIG_STOMACH_BUN, 1 + this.getRandom().nextInt(2));
            case 2 -> new ItemStack(ModItems.OILY_MEAT, 1);
            case 3 -> new ItemStack(ModItems.HEALTH_LEAF, 1 + this.getRandom().nextInt(3));
            default -> new ItemStack(ModItems.FLAVOR_SNACK, 1);
        };
    }

    /**
     * Custom AI goal: scan for nearby food items on the ground and eat them.
     */
    static class EatNearbyFoodGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final LiangziEntity liangzi;
        private int cooldown;

        EatNearbyFoodGoal(LiangziEntity liangzi) {
            this.liangzi = liangzi;
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.cooldown = 0;
        }

        @Override
        public boolean canUse() {
            if (cooldown > 0) {
                cooldown--;
                return false;
            }
            return !liangzi.level().getEntitiesOfClass(ItemEntity.class,
                    getSearchBox(), e -> isFood(e.getItem())).isEmpty();
        }

        @Override
        public void start() {
            List<ItemEntity> foodItems = liangzi.level().getEntitiesOfClass(ItemEntity.class,
                    getSearchBox(), e -> isFood(e.getItem()));

            if (!foodItems.isEmpty()) {
                ItemEntity target = foodItems.get(liangzi.getRandom().nextInt(foodItems.size()));
                liangzi.getNavigation().moveTo(target, 1.0D);

                for (ItemEntity item : foodItems) {
                    eatFoodItem(item);
                }
            }

            cooldown = 60 + liangzi.getRandom().nextInt(40);
        }

        private static boolean isFood(ItemStack stack) {
            return stack.has(DataComponents.FOOD);
        }

        private void eatFoodItem(ItemEntity item) {
            if (!item.isAlive()) return;

            ItemStack stack = item.getItem();
            int count = stack.getCount();

            item.discard();

            if (liangzi.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        item.getX(), item.getY() + 0.5D, item.getZ(),
                        8, 0.3D, 0.3D, 0.3D, 0.0D);
            }

            liangzi.playSound(SoundEvents.GENERIC_EAT, 1.0F,
                    1.0F + (liangzi.getRandom().nextFloat() - 0.5F) * 0.2F);

            if (count > 0) {
                liangzi.heal(count * 0.5F);
            }
        }

        private AABB getSearchBox() {
            return liangzi.getBoundingBox().inflate(8.0D, 4.0D, 8.0D);
        }
    }
}
