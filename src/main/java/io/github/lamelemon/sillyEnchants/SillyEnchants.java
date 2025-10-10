package io.github.lamelemon.sillyEnchants;

import io.github.lamelemon.sillyEnchants.Commands.SillyEnchantCommand;
import io.github.lamelemon.sillyEnchants.Enchantments.EnchantmentBeheading;
import io.github.lamelemon.sillyEnchants.Enchantments.EnchantmentHarvest;
import io.github.lamelemon.sillyEnchants.Enchantments.EnchantmentShockwave;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public final class SillyEnchants extends JavaPlugin {
    private static SillyEnchants instance;

    @Override
    public void onEnable() {
        instance = this;
        registerCommand("SillyEnchant", new SillyEnchantCommand());
        getServer().getPluginManager().registerEvents(new EnchantmentBeheading(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentShockwave(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentHarvest(), this);
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
}
