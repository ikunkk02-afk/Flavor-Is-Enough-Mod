package com.ikunkk02.flavorisenough.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WarningCardItem extends Item {
	private static final int USE_COOLDOWN_TICKS = 20;
	private static final Component WARNING_MESSAGE = Component.literal(
			"游戏可以抽象，现实不要暴饮暴食。长期肥胖可能影响健康。");

	public WarningCardItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (!level.isClientSide()) {
			player.displayClientMessage(WARNING_MESSAGE, false);
			player.getCooldowns().addCooldown(this, USE_COOLDOWN_TICKS);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
}
