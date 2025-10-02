package io.github.lamelemon.sillyEnchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;


public class EnchantmentManager {
    private static Registry<Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

    public static Enchantment getEnchant(String key) {
        return enchantmentRegistry.getOrThrow(RegistryKey.ENCHANTMENT.typedKey(Key.key(key)));
    }
}
