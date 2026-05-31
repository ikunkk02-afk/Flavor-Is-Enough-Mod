package com.ikunkk02.flavorisenough.client.mixin;

import com.ikunkk02.flavorisenough.client.render.FatBodyFeatureRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void flavor_is_enough$addFatBodyLayer(EntityRendererProvider.Context context, boolean slim, CallbackInfo callbackInfo) {
		PlayerRenderer renderer = (PlayerRenderer) (Object) this;
		@SuppressWarnings("unchecked")
		LivingEntityRendererAccessor<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> accessor =
				(LivingEntityRendererAccessor<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) (Object) this;
		accessor.flavor_is_enough$getLayers().add(0, new FatBodyFeatureRenderer(renderer, context, slim));
	}
}
