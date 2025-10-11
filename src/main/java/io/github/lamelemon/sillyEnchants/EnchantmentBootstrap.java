package io.github.lamelemon.sillyEnchants;


import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

// IDE may mark this as not used, it does get used as it is the bootstrapper.
public class EnchantmentBootstrap implements PluginBootstrap {
    private boolean bootstrapFailed = false;

    @Override
    public void bootstrap(BootstrapContext context) {

        InputStream in = getClass().getResourceAsStream("/enchantments.yml");
        if (in == null) {
            bootstrapFailed = true;
            context.getLogger().info("Bootstrap has failed! Disabling plugin...");
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(in));

        context.getLifecycleManager().registerEventHandler(
            RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {

                Registry<@NotNull ItemType> itemRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
                String pluginName = context.getPluginMeta().getName().toLowerCase();

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
                                        .weight(configurationSection.getInt("weight", 1))
                                        .anvilCost(configurationSection.getInt("anvil-cost", 1))
                                        .supportedItems(RegistrySet.keySetFromValues(RegistryKey.ITEM,
                                                mapValid(configurationSection.getStringList("supported-items"), item -> itemRegistry.get(Key.key(item)))
                                        ))
                                        .activeSlots(mapValid(configurationSection.getStringList("active-slots"), EquipmentSlotGroup::getByName)
                                        );
                            }
                        );
                    context.getLogger().info("registered: {}:{}", pluginName, id);
                    }
                context.getLogger().info("registered enchants");
                }) // look at all them brackets :O
        );
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        if (bootstrapFailed) {
            return null;
        }
        return new SillyEnchants();
    }

    // Helper function to avoid repeating code when reading config (stay dry gang)
    private static <T> List<T> mapValid(List<String> keys, Function<String, T> mapper) {
        return keys.stream().map(mapper).filter(Objects::nonNull).toList();
    }
}
