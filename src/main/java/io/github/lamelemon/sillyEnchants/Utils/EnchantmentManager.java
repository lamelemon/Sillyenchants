package io.github.lamelemon.sillyEnchants.Utils;

import io.github.lamelemon.sillyEnchants.SillyEnchants;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


public class EnchantmentManager {
    private static final Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

    // get the enchantment corresponding to the given key
    public static Enchantment getEnchant(@NotNull String key) {
        NamespacedKey namespacedKey = new NamespacedKey(SillyEnchants.getInstance(), key);
        SillyEnchants.getInstance().getLogger().info("returning namespacedkey " + namespacedKey);

        Enchantment enchantment = enchantmentRegistry.getOrThrow(RegistryKey.ENCHANTMENT.typedKey(namespacedKey));
        SillyEnchants.getInstance().getLogger().info("Returning enchant: " + enchantment);
        return enchantment;
    }

    public static void printEnchants(Logger logger) {
        logger.info(enchantmentRegistry.toString());
    }
}
