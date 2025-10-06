package io.github.lamelemon.sillyEnchants;

import io.github.lamelemon.sillyEnchants.Commands.SillyEnchantCommand;
import io.github.lamelemon.sillyEnchants.Enchantments.EnchantmentBeheading;
import io.github.lamelemon.sillyEnchants.Utils.EnchantmentManager;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
        registerCommand("SillyEnchant", new SillyEnchantCommand());
        EnchantmentManager.printEnchants(getLogger());
        instance = this;
        Registry<@NotNull Enchantment> reg = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        reg.forEach(en -> getLogger().info("Enchantment loaded: " + en.getKey()));
        getLogger().info("SillyEnchants successfully loaded!");
        getServer().getPluginManager().registerEvents(new EnchantmentBeheading(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Bye world!");
    }

    public static SillyEnchants getInstance() {
        return instance;
    }
}
