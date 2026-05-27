package com.ikunkk02.flavorisenough.item;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MeasuringTapeItem extends Item {
	private static final int USE_COOLDOWN_TICKS = 20;

	public MeasuringTapeItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (!level.isClientSide()) {
			FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
			ModEntityComponents.FLAVOR_PLAYER.sync(player);
			player.displayClientMessage(Component.literal("味真足值：" + component.getFlavorValue() + "/100"), false);
			player.displayClientMessage(Component.literal("肥胖值：" + component.getObesityValue() + "/100"), false);
			player.displayClientMessage(Component.literal("健康值：" + component.getHealthValue() + "/100"), false);
			player.displayClientMessage(Component.literal("胃袋负荷：" + component.getStomachLoad() + "/100"), false);
			player.displayClientMessage(Component.literal("当前体态：" + component.getObesityStageText()), false);
			player.getCooldowns().addCooldown(this, USE_COOLDOWN_TICKS);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
}
