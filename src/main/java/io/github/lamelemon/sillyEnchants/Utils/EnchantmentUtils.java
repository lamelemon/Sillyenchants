package io.github.lamelemon.sillyEnchants.Utils;

import io.github.lamelemon.sillyEnchants.SillyEnchants;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;


public class EnchantmentUtils {
    private static final Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

    // Get the enchantment corresponding to the given key
    public static Enchantment getEnchant(@NotNull String key) {
        return enchantmentRegistry.get(TypedKey.create(RegistryKey.ENCHANTMENT, Key.key(SillyEnchants.getPluginName() + ":" + key)));
    }
}
