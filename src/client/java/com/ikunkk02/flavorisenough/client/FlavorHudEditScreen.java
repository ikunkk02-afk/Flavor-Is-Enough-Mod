package com.ikunkk02.flavorisenough.client;

import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class FlavorHudEditScreen extends Screen {
	private static final int HELP_PANEL_BACKGROUND = 0xB0101018;
	private static final int HELP_PANEL_BORDER = 0xCC8F8F98;
	private static final int HELP_TITLE_COLOR = 0xFFFFD166;
	private static final int HELP_TEXT_COLOR = 0xFFEDEDF2;

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
		super.render(context, mouseX, mouseY, delta);
		renderHelpPanel(context);
		FlavorHudRenderer.renderConfiguredPanel(context, true);
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
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (FlavorHudRenderer.isInsidePanel(mouseX, mouseY)) {
			FlavorHudRenderer.adjustHudScale(verticalAmount, width, height);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	private void renderHelpPanel(GuiGraphics context) {
		Font font = Minecraft.getInstance().font;
		Component instructions = Component.translatable("screen." + FlavorIsEnoughMod.MOD_ID + ".hud_editor.instructions");
		int maxPanelWidth = Math.max(120, width - 16);
		int panelWidth = Math.min(maxPanelWidth, Math.max(font.width(title), font.width(instructions)) + 24);
		int panelHeight = 31;
		int panelX = (width - panelWidth) / 2;
		int panelY = 8;
		int textMaxWidth = Math.max(1, panelWidth - 16);

		context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, HELP_PANEL_BACKGROUND);
		drawBorder(context, panelX, panelY, panelWidth, panelHeight, HELP_PANEL_BORDER);
		context.drawCenteredString(font, fitText(font, title, textMaxWidth), width / 2, panelY + 6, HELP_TITLE_COLOR);
		context.drawCenteredString(font, fitText(font, instructions, textMaxWidth), width / 2, panelY + 18, HELP_TEXT_COLOR);
	}

	private static String fitText(Font font, Component text, int maxWidth) {
		String value = text.getString();
		if (font.width(value) <= maxWidth) {
			return value;
		}

		String ellipsis = "...";
		return font.plainSubstrByWidth(value, Math.max(0, maxWidth - font.width(ellipsis))) + ellipsis;
	}

	private static void drawBorder(GuiGraphics context, int x, int y, int width, int height, int color) {
		context.fill(x, y, x + width, y + 1, color);
		context.fill(x, y + height - 1, x + width, y + height, color);
		context.fill(x, y, x + 1, y + height, color);
		context.fill(x + width - 1, y, x + width, y + height, color);
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
