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
                context.getLogger().info("registering enchants of " + config );
                context.getLogger().info("getting keys aswell " + config.getKeys(false));
                Registry<ItemType> itemRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);

                for (String id : config.getKeys(false)) {
                    ConfigurationSection configurationSection = config.getConfigurationSection(id);

                    if (configurationSection.getBoolean("disabled")){
                        continue;
                    }

                    context.getLogger().info("registering enchant " + id);

                    List<ItemType> itemTypes = mapValid(configurationSection.getStringList("supported-items"), item -> itemRegistry.get(Key.key(item)));
                    for (String item : configurationSection.getStringList("supported-items")) {
                        ItemType itemType = itemRegistry.get(Key.key(item));
                        if (itemType != null) {
                            itemTypes.add(itemType);
                        }
                    }

                    List<EquipmentSlotGroup> equipmentSlotGroups = mapValid(configurationSection.getStringList("active-slots"), EquipmentSlotGroup::getByName);


                    event.registry().register(
                            EnchantmentKeys.create(Key.key(pluginName + ":" + id)),
                            b -> {
                                b.description(Component.text(configurationSection.getString("description", id)))
                                        .maxLevel(configurationSection.getInt("max-level", 1))
                                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                                        .weight(configurationSection.getInt("weight", 10))
                                        .anvilCost(configurationSection.getInt("anvil-cost", 1))
                                        .supportedItems(RegistrySet.keySetFromValues(RegistryKey.ITEM, itemTypes))
                                        .activeSlots(equipmentSlotGroups);
                            }
                        );

                    context.getLogger().info("Registered enchantment " + id);
                    }
                context.getLogger().info("Registered enchants");

                })
        );
    }

    private static <T> List<T> mapValid(List<String> keys, Function<String, T> mapper) {
        return keys.stream().map(mapper).filter(Objects::nonNull).toList();
    }
}
