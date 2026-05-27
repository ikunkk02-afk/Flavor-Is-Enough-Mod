package com.ikunkk02.flavorisenough.client;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class FlavorHudEditScreen extends Screen {
	private boolean dragging;
	private int dragOffsetX;
	private int dragOffsetY;

	public FlavorHudEditScreen() {
		super(Component.translatable("screen." + FlavorIsEnoughMod.MOD_ID + ".hud_editor"));
	}

	@Override
	protected void init() {
		FlavorHudRenderer.clampHudToScreen(width, height, true);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		renderBackground(context, mouseX, mouseY, delta);
		FlavorHudRenderer.clampHudToScreen(width, height, false);
		FlavorHudRenderer.renderConfiguredPanel(context, true);
		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && FlavorHudRenderer.isInsidePanel(mouseX, mouseY)) {
			FlavorClientConfig config = FlavorClientConfig.get();
			dragging = true;
			dragOffsetX = (int) mouseX - config.getHudX();
			dragOffsetY = (int) mouseY - config.getHudY();
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (dragging && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			FlavorHudRenderer.setHudPositionClamped((int) mouseX - dragOffsetX, (int) mouseY - dragOffsetY, width, height);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (dragging && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dragging = false;
			FlavorClientConfig.get().save();
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void onClose() {
		dragging = false;
		FlavorClientConfig.get().save();
		super.onClose();
	}

	@Override
	public void removed() {
		dragging = false;
		FlavorClientConfig.get().save();
		super.removed();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
