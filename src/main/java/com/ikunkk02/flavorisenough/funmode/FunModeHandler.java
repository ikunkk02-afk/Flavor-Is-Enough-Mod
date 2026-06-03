package com.ikunkk02.flavorisenough.funmode;

import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.ikunkk02.flavorisenough.config.FlavorModConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class FunModeHandler {

    private FunModeHandler() {
    }

    public static boolean isFunModeActive(Player player) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }
        if (!FlavorModConfig.get().funModeEnabled) {
            return false;
        }
        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        return component.isFunModeActivated();
    }

    public static void activateFunMode(ServerPlayer player) {
        FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(player);
        if (component.isFunModeActivated()) {
            return;
        }
        component.setFunModeActivated(true);
        // Reset obesity to 0 — no more negative effects
        component.setObesityValue(0);
        component.setStomachLoad(0);
        component.setHealthValue(100);
        ModEntityComponents.FLAVOR_PLAYER.sync(player);
    }
}
