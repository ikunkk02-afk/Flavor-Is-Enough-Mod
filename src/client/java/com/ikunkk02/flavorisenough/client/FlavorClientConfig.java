package com.ikunkk02.flavorisenough.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ikunkk02.flavorisenough.FlavorIsEnoughMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FlavorClientConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("flavor_is_enough-client.json");
	private static FlavorClientConfig instance = new FlavorClientConfig();

	private boolean hudEnabled = true;
	private int hudX = 10;
	private int hudY = 10;

	public static void load() {
		boolean configExists = Files.exists(CONFIG_PATH);
		if (configExists) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
				FlavorClientConfig loaded = GSON.fromJson(reader, FlavorClientConfig.class);
				instance = loaded != null ? loaded : new FlavorClientConfig();
			} catch (Exception exception) {
				FlavorIsEnoughMod.LOGGER.warn("Failed to load Flavor Is Enough client config. Using defaults.", exception);
				instance = new FlavorClientConfig();
			}
		} else {
			instance = new FlavorClientConfig();
			instance.save();
		}
	}

	public static FlavorClientConfig get() {
		return instance;
	}

	public boolean isHudEnabled() {
		return hudEnabled;
	}

	public void setHudEnabled(boolean hudEnabled) {
		this.hudEnabled = hudEnabled;
	}

	public int getHudX() {
		return hudX;
	}

	public int getHudY() {
		return hudY;
	}

	public void setHudPosition(int hudX, int hudY) {
		this.hudX = hudX;
		this.hudY = hudY;
	}

	public boolean clampHudPosition(int screenWidth, int screenHeight, int panelWidth, int panelHeight) {
		int oldX = hudX;
		int oldY = hudY;
		int maxX = Math.max(0, screenWidth - panelWidth);
		int maxY = Math.max(0, screenHeight - panelHeight);
		hudX = Math.max(0, Math.min(hudX, maxX));
		hudY = Math.max(0, Math.min(hudY, maxY));
		return hudX != oldX || hudY != oldY;
	}

	public void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(this, writer);
			}
		} catch (Exception exception) {
			FlavorIsEnoughMod.LOGGER.warn("Failed to save Flavor Is Enough client config.", exception);
		}
	}
}
