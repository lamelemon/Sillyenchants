package io.github.lamelemon.sillyEnchants;

import io.github.lamelemon.sillyEnchants.Commands.SillyEnchantCommand;
import io.github.lamelemon.sillyEnchants.Enchantments.*;
import io.github.lamelemon.sillyEnchants.Utils.EnchantmentUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;

public final class SillyEnchants extends JavaPlugin {
    private static SillyEnchants instance;

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pluginManager = getServer().getPluginManager();
        saveResource("enchantments.yml", true);
        YamlConfiguration enchantmentConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "enchantments.yml"));
        InputStream in = getClass().getResourceAsStream("/internal-enchantments.yml");
        if (in == null) {
            getLogger().info("internal enchants is null");
            return;
        }
        YamlConfiguration internalEnchantmentConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(in));

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
                new Harvesting(EnchantmentUtils.getEnchant("harvesting"),
                enchantmentConfig.getInt("harvesting.max-tree-blocks-broken"),
                enchantmentConfig.getInt("harvesting.max-ore-blocks-broken"),
                Harvesting.parseTrees(Objects.requireNonNull(internalEnchantmentConfig.getConfigurationSection("harvesting.tree-structure"))),
                Harvesting.parseSet(Objects.requireNonNull(internalEnchantmentConfig.getStringList("harvesting.ore-structure")))),
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

        pluginManager.registerEvents(
                new Updraft(EnchantmentUtils.getEnchant("updraft")),
                this
        );

        pluginManager.registerEvents(
                new Gouging(EnchantmentUtils.getEnchant("gouging")),
                this
        );
        pluginManager.registerEvents(
                new Bash(EnchantmentUtils.getEnchant("bash")),
                this
        );

        getLogger().info("SillyEnchants successfully loaded!");
    }

    public static SillyEnchants getInstance() {
        return instance;
    }

    public static String getPluginName() {
        return instance.getName().toLowerCase();
    }
}
