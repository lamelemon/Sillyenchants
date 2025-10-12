package io.github.lamelemon.sillyEnchants;

import io.github.lamelemon.sillyEnchants.Commands.SillyEnchantCommand;
import io.github.lamelemon.sillyEnchants.Enchantments.*;
import io.github.lamelemon.sillyEnchants.Utils.EnchantmentUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Objects;

public final class SillyEnchants extends JavaPlugin {
    private static SillyEnchants instance;
    private static YamlConfiguration enchantmentConfig;

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pluginManager = getServer().getPluginManager();
        saveResource("enchantments.yml", true);
        enchantmentConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "enchantments.yml"));

        registerCommand("SillyEnchant", new SillyEnchantCommand());

        pluginManager.registerEvents(
                new Beheading(EnchantmentUtils.getEnchant("beheading")),
                this
        );
        pluginManager.registerEvents(
                new Shockwave(EnchantmentUtils.getEnchant("shockwave")),
                this
        );
        pluginManager.registerEvents(
                new Harvest(EnchantmentUtils.getEnchant("harvesting"),
                enchantmentConfig.getInt("harvesting.max-blocks-broken")),
                this
        );
        pluginManager.registerEvents(
                new Smelting(EnchantmentUtils.getEnchant("smelting"),
                        Smelting.parseSmeltables(Objects.requireNonNull(enchantmentConfig.getConfigurationSection("smelting.smeltable-items")))),
                this
        );
        pluginManager.registerEvents(
                new Bounce(EnchantmentUtils.getEnchant("bounce")),
                this
        );
        getLogger().info("SillyEnchants successfully loaded!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Bye world!");
    }

    public static SillyEnchants getInstance() {
        return instance;
    }

    public static String getPluginName() {
        return instance.getName().toLowerCase();
    }

    public static YamlConfiguration getEnchantmentConfig() { return enchantmentConfig; }
}
