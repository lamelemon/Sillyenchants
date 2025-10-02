package io.github.lamelemon.sillyEnchants;


import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class EnchantmentBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        InputStream in = getClass().getResourceAsStream("/enchants.yml");
        if (in == null) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(in));

        context.getLifecycleManager().registerEventHandler(
            RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
                    for (String id : Objects.requireNonNull(config.getConfigurationSection("enchants")).getKeys(false)) {
                        String key = context.getPluginMeta().getName().toLowerCase() + ":" + id;
                        FileConfiguration enchantConfig = (FileConfiguration) config.getConfigurationSection("enchants." + id);

                        event.registry().register(
                                EnchantmentKeys.create(Key.key(key)),
                                b -> {
                                    b.description(Component.text(enchantConfig.getString("description", id)))
                                            .maxLevel(enchantConfig.getInt("max-level", 1))
                                            .weight(enchantConfig.getInt("weight", 10))
                                            .anvilCost(enchantConfig.getInt("anvil-cost", 1))
                                            .activeSlots(EquipmentSlotGroup.getByName(enchantConfig.getStringList("active-slots").get(0)));

                                    List<String> items = enchantConfig.getStringList("supported-items");
                                    for (String item : items) {
                                        b.supportedItems(event.getOrCreateTag(TagKey.create(RegistryKey.ITEM, item)));
                                    }
                                }
                        );
                    }
                })
        );
    }
}
