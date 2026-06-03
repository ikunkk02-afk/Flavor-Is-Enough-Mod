package com.ikunkk02.flavorisenough.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "flavor-is-enough-mod")
public class FlavorModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public boolean funModeEnabled = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 1, max = 256)
    public int funModeOfferingRequired = 128;

    public static FlavorModConfig get() {
        return AutoConfig.getConfigHolder(FlavorModConfig.class).getConfig();
    }

    public static void register() {
        AutoConfig.register(FlavorModConfig.class, GsonConfigSerializer::new);
    }
}
