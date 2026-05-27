package com.ikunkk02.flavorisenough.component;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public final class ModEntityComponents implements EntityComponentInitializer {
	public static final ComponentKey<FlavorPlayerComponent> FLAVOR_PLAYER = ComponentRegistry.getOrCreate(
			ResourceLocation.fromNamespaceAndPath(FlavorIsEnoughMod.MOD_ID, "flavor_player"),
			FlavorPlayerComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(FLAVOR_PLAYER, player -> new FlavorPlayerComponent(), RespawnCopyStrategy.ALWAYS_COPY);
	}
}
