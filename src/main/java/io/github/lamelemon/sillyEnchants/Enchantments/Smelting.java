package io.github.lamelemon.sillyEnchants.Enchantments;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Smelting implements Listener {
    private final Enchantment enchantment;
    private final HashMap<Material, Material> smeltables;

    public Smelting(Enchantment enchantment, HashMap<Material, Material> smeltables) {
        this.enchantment = enchantment;
        this.smeltables = smeltables;
    }

    @EventHandler
    public void onBlockDropItemEvent(BlockDropItemEvent event) {
        if (!event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(enchantment)) {
            return;
        }

        for (Item item : event.getItems()) {
            ItemStack itemStack = item.getItemStack();
            // Check if item drop can be smelted
            if (smeltables.containsKey(itemStack.getType())) {
                item.setItemStack(new ItemStack(smeltables.get(itemStack.getType()), itemStack.getAmount())); // Convert to smelted item
            }
        }
    }

    // Parses the items into a hashmap that may be smelted by the enchantment
    public static HashMap<Material, Material> parseSmeltables(ConfigurationSection smeltableItems) {
        HashMap<Material, Material> returnable = new HashMap<>();
        for (String key : smeltableItems.getKeys(false)) {
            returnable.put(Material.valueOf(key), Material.valueOf(smeltableItems.getString(key)));
        }
        return returnable;
    }
}
