package io.github.lamelemon.sillyEnchants;

import io.github.lamelemon.sillyEnchants.Commands.SillyEnchantCommand;
import io.github.lamelemon.sillyEnchants.Enchantments.EnchantmentBeheading;
import io.github.lamelemon.sillyEnchants.Enchantments.EnchantmentShockwave;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class SillyEnchants extends JavaPlugin {
    private static SillyEnchants instance;

    @Override
    public void onEnable() {
        File enchantmentConfigFile = new File(getDataFolder(), "enchants.yml");
        if (!enchantmentConfigFile.exists()) {
            getLogger().info("""
                    Attempting to save default enchantments config and then restarting server.
                    Please start your server again if this causes a shutdown.
                    Tip: You can configure the server to restart instead! (A guide on how to do so is in the link)
                    https://gist.github.com/Prof-Bloodstone/6367eb4016eaf9d1646a88772cdbbac5""");
            saveResource("enchants.yml", false);
            getServer().restart();
            return;
        }
        instance = this;
        registerCommand("SillyEnchant", new SillyEnchantCommand());
        getServer().getPluginManager().registerEvents(new EnchantmentBeheading(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentShockwave(), this);
        Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        registry.forEach(e -> getLogger().info("registered: " + e));
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
