package com.ikunkk02.flavorisenough.client;

import com.ikunkk02.flavorisenough.client.render.FatBodyFeatureRenderer;
import com.ikunkk02.flavorisenough.client.render.LiangziEntityRenderer;
import com.ikunkk02.flavorisenough.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class FlavorIsEnoughModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FlavorClientConfig.load();
		FatBodyFeatureRenderer.registerModelLayers();
		FlavorHudRenderer.register();
		EntityRendererRegistry.register(ModEntities.LIANGZI, LiangziEntityRenderer::new);
	}
}
