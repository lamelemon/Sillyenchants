package io.github.lamelemon.sillyEnchants;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class SillyEnchants extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("Hello world!");

        saveResource("enchants.yml", false);

        File enchantConfigFile = new File(getDataFolder(), "enchants.yml");
        FileConfiguration enchantsConfig = YamlConfiguration.loadConfiguration(enchantConfigFile);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Bye world!");
    }

    public static String getPluginName() {
        return SillyEnchants.class.getName().toLowerCase();
    }
}
