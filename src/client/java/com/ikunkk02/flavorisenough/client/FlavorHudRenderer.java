package com.ikunkk02.flavorisenough.client;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import com.ikunkk02.flavorisenough.component.FlavorPlayerComponent;
import com.ikunkk02.flavorisenough.component.ModEntityComponents;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class FlavorHudRenderer {
	public static final int PANEL_WIDTH = 176;
	public static final int PANEL_HEIGHT = 128;

	private static final int PADDING = 8;
	private static final int BAR_HEIGHT = 5;
	private static final int ROW_HEIGHT = 18;
	private static final int BACKGROUND_COLOR = 0xAA101018;
	private static final int BORDER_COLOR = 0xFF8F8F98;
	private static final int INNER_BORDER_COLOR = 0x6633333A;
	private static final int TEXT_COLOR = 0xFFFFFFFF;
	private static final int MUTED_TEXT_COLOR = 0xFFD7D7DD;
	private static final int TITLE_COLOR = 0xFFFFD166;
	private static final int BAR_BACKGROUND_COLOR = 0xFF272733;
	private static final int FLAVOR_COLOR = 0xFFFFC83D;
	private static final int OBESITY_COLOR = 0xFFFF6542;
	private static final int HEALTH_COLOR = 0xFF58D47A;
	private static final int STOMACH_COLOR = 0xFFB767FF;
	private static final int EXERCISE_COLOR = 0xFF4DA6FF;

	private static KeyMapping toggleHudKey;
	private static KeyMapping editHudKey;

	private FlavorHudRenderer() {
	}

	public static void register() {
		toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key." + FlavorIsEnoughMod.MOD_ID + ".toggle_hud",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_H,
				"category." + FlavorIsEnoughMod.MOD_ID));

		editHudKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key." + FlavorIsEnoughMod.MOD_ID + ".edit_hud",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_J,
				"category." + FlavorIsEnoughMod.MOD_ID));

		ClientTickEvents.END_CLIENT_TICK.register(FlavorHudRenderer::handleKeyInput);
		HudRenderCallback.EVENT.register(FlavorHudRenderer::render);
	}

	private static void handleKeyInput(Minecraft client) {
		while (toggleHudKey.consumeClick()) {
			FlavorClientConfig config = FlavorClientConfig.get();
			config.setHudEnabled(!config.isHudEnabled());
			config.save();

			if (client.player != null) {
				client.player.displayClientMessage(Component.literal(
						config.isHudEnabled() ? "味真足 HUD：已开启" : "味真足 HUD：已关闭"), false);
			}
		}

		while (editHudKey.consumeClick()) {
			if (client.player != null && client.screen == null) {
				client.setScreen(new FlavorHudEditScreen());
			}
		}
	}

	private static void render(GuiGraphics context, net.minecraft.client.DeltaTracker tickCounter) {
		Minecraft client = Minecraft.getInstance();
		if (client.screen instanceof FlavorHudEditScreen) {
			return;
		}

		renderConfiguredPanel(context, false);
	}

	public static void renderConfiguredPanel(GuiGraphics context, boolean forceVisible) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.level == null) {
			return;
		}

		FlavorClientConfig config = FlavorClientConfig.get();
		if (!forceVisible && !config.isHudEnabled()) {
			return;
		}

		clampHudToScreen(context.guiWidth(), context.guiHeight(), true);

		FlavorPlayerComponent component = ModEntityComponents.FLAVOR_PLAYER.get(client.player);
		renderPanelScaled(context, config.getHudX(), config.getHudY(), config.getHudScale(), component);
	}

	public static boolean clampHudToScreen(int screenWidth, int screenHeight, boolean save) {
		FlavorClientConfig config = FlavorClientConfig.get();
		boolean changed = config.clampHudPosition(screenWidth, screenHeight, getScaledPanelWidth(), getScaledPanelHeight());
		if (changed && save) {
			config.save();
		}
		return changed;
	}

	public static boolean isInsidePanel(double mouseX, double mouseY) {
		FlavorClientConfig config = FlavorClientConfig.get();
		return mouseX >= config.getHudX()
				&& mouseX <= config.getHudX() + getScaledPanelWidth()
				&& mouseY >= config.getHudY()
				&& mouseY <= config.getHudY() + getScaledPanelHeight();
	}

	public static void setHudPositionClamped(int x, int y, int screenWidth, int screenHeight) {
		FlavorClientConfig config = FlavorClientConfig.get();
		config.setHudPosition(x, y);
		config.clampHudPosition(screenWidth, screenHeight, getScaledPanelWidth(), getScaledPanelHeight());
	}

	public static void adjustHudScale(double scrollAmount, int screenWidth, int screenHeight) {
		FlavorClientConfig config = FlavorClientConfig.get();
		config.adjustHudScale(scrollAmount);
		config.clampHudPosition(screenWidth, screenHeight, getScaledPanelWidth(), getScaledPanelHeight());
		config.save();
	}

	private static int getScaledPanelWidth() {
		return Math.round(PANEL_WIDTH * FlavorClientConfig.get().getHudScale());
	}

	private static int getScaledPanelHeight() {
		return Math.round(PANEL_HEIGHT * FlavorClientConfig.get().getHudScale());
	}

	private static void renderPanelScaled(GuiGraphics context, int x, int y, float scale, FlavorPlayerComponent component) {
		context.pose().pushPose();
		context.pose().translate(x, y, 0.0F);
		context.pose().scale(scale, scale, 1.0F);
		renderPanel(context, 0, 0, component);
		context.pose().popPose();
	}

	private static void renderPanel(GuiGraphics context, int x, int y, FlavorPlayerComponent component) {
		Minecraft client = Minecraft.getInstance();
		Font font = client.font;

		context.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, BACKGROUND_COLOR);
		drawBorder(context, x, y, PANEL_WIDTH, PANEL_HEIGHT, BORDER_COLOR);
		drawBorder(context, x + 2, y + 2, PANEL_WIDTH - 4, PANEL_HEIGHT - 4, INNER_BORDER_COLOR);

		String title = "味真足状态";
		int titleX = x + (PANEL_WIDTH - font.width(title)) / 2;
		context.drawString(font, title, titleX, y + PADDING, TITLE_COLOR, true);

		int rowY = y + 23;
		int barWidth = PANEL_WIDTH - (PADDING * 2);
		drawMetric(context, font, "味真足", component.getFlavorValue(), x + PADDING, rowY, barWidth, FLAVOR_COLOR);
		drawMetric(context, font, "肥胖值", component.getObesityValue(), x + PADDING, rowY + ROW_HEIGHT, barWidth, OBESITY_COLOR);
		drawMetric(context, font, "健康值", component.getHealthValue(), x + PADDING, rowY + (ROW_HEIGHT * 2), barWidth, HEALTH_COLOR);
		drawMetric(context, font, "胃袋负荷", component.getStomachLoad(), x + PADDING, rowY + (ROW_HEIGHT * 3), barWidth, STOMACH_COLOR);
		drawMetric(context, font, "运动进度", component.getExerciseValue(), x + PADDING, rowY + (ROW_HEIGHT * 4), barWidth, EXERCISE_COLOR);

		String stage = "体态阶段：" + component.getObesityStageText();
		context.drawString(font, stage, x + PADDING, y + PANEL_HEIGHT - PADDING - 9, MUTED_TEXT_COLOR, true);
	}

	private static void drawMetric(GuiGraphics context, Font font, String label, int value, int x, int y, int barWidth, int fillColor) {
		context.drawString(font, label + "：" + value + "/100", x, y, TEXT_COLOR, true);
		drawProgressBar(context, x, y + 10, barWidth, BAR_HEIGHT, value, fillColor);
	}

	private static void drawProgressBar(GuiGraphics context, int x, int y, int width, int height, int value, int fillColor) {
		context.fill(x, y, x + width, y + height, BAR_BACKGROUND_COLOR);
		int fillWidth = Math.round((width - 2) * Math.max(0, Math.min(100, value)) / 100.0F);
		context.fill(x + 1, y + 1, x + 1 + fillWidth, y + height - 1, fillColor);
	}

	private static void drawBorder(GuiGraphics context, int x, int y, int width, int height, int color) {
		context.fill(x, y, x + width, y + 1, color);
		context.fill(x, y + height - 1, x + width, y + height, color);
		context.fill(x, y, x + 1, y + height, color);
		context.fill(x + width - 1, y, x + width, y + height, color);
	}
}
