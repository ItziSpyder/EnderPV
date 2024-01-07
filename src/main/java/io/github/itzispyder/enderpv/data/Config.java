package io.github.itzispyder.enderpv.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class Config {

    public static FileConfiguration get() {
        File file = new File("plugins/EnderPV/config.yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public static class Plugin {
        private static final String path = "config.plugin.";
        public static boolean overrideEnderChests() {
            return get().getBoolean(path + "override-enderchests");
        }
    }
}