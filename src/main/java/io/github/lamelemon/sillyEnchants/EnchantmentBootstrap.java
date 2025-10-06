package io.github.lamelemon.sillyEnchants;


import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

// IDE may mark this as not used, it does get used as it is the bootstrapper.
public class EnchantmentBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {

        File enchantmentConfigFile = new File(context.getDataDirectory().toFile(), "enchants.yml");

        if (!enchantmentConfigFile.exists()) {
            context.getLogger().info("Config file not found, skipping bootstrap");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(enchantmentConfigFile);
        String pluginName = context.getPluginMeta().getName().toLowerCase();

        context.getLifecycleManager().registerEventHandler(
            RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {

                Registry<ItemType> itemRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);

                for (String id : config.getKeys(false)) {
                    ConfigurationSection configurationSection = config.getConfigurationSection(id);

                    // Register selected enchantment in the registry (order of methods doesn't matter)
                    // This section doesn't really need changing unless we want to add more configuration to the enchantment
                    // (trust me there is loads of config for these)
                    // also don't go changing these anyway because it screws up configuration
                    event.registry().register(
                            EnchantmentKeys.create(Key.key(pluginName + ":" + id)),
                            b -> {
                                b.description(Component.text(configurationSection.getString("description", id)))
                                        .maxLevel(configurationSection.getInt("max-level", 1))
                                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(
                                                configurationSection.getInt("minimum-cost.base-cost", 1),
                                                configurationSection.getInt("minimum-cost.additional-cost", 1)
                                        ))
                                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(
                                                configurationSection.getInt("maximum-cost.base-cost", 1),
                                                configurationSection.getInt("maximum-cost.additional-cost", 1)
                                        ))
                                        .weight(configurationSection.getInt("weight", 0))
                                        .anvilCost(configurationSection.getInt("anvil-cost", 1))
                                        .supportedItems(RegistrySet.keySetFromValues(RegistryKey.ITEM,
                                                mapValid(configurationSection.getStringList("supported-items"), item -> itemRegistry.get(Key.key(item)))
                                        ))
                                        .activeSlots(mapValid(configurationSection.getStringList("active-slots"), EquipmentSlotGroup::getByName)
                                        );
                            }
                        );
                    }
                }) // look at all them brackets :O
        );
    }

    // Helper function to avoid repeating code when reading config (stay dry gang)
    private static <T> List<T> mapValid(List<String> keys, Function<String, T> mapper) {
        return keys.stream().map(mapper).filter(Objects::nonNull).toList();
    }
}
