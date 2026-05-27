package com.ikunkk02.flavorisenough.client;

import net.fabricmc.api.ClientModInitializer;

public class FlavorIsEnoughModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FlavorClientConfig.load();
		FlavorHudRenderer.register();
	}
}
