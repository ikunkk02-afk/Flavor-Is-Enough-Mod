package com.ikunkk02.flavorisenough.client;

import com.ikunkk02.flavorisenough.client.render.FatBodyFeatureRenderer;
import net.fabricmc.api.ClientModInitializer;

public class FlavorIsEnoughModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FlavorClientConfig.load();
		FatBodyFeatureRenderer.registerModelLayers();
		FlavorHudRenderer.register();
	}
}
