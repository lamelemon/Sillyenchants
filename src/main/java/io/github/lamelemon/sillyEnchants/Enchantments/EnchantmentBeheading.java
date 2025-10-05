package io.github.lamelemon.sillyEnchants.Enchantments;

import io.github.lamelemon.sillyEnchants.Utils.EnchantmentManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class EnchantmentBeheading implements Listener {

    private final HashMap<EntityType, ItemType> heads = new HashMap<>(Map.of(
            EntityType.SKELETON, ItemType.SKELETON_SKULL,
            EntityType.WITHER_SKELETON, ItemType.WITHER_SKELETON_SKULL,
            EntityType.ZOMBIE, ItemType.ZOMBIE_HEAD,
            EntityType.PLAYER, ItemType.PLAYER_HEAD,
            EntityType.CREEPER, ItemType.CREEPER_HEAD,
            EntityType.PIGLIN, ItemType.PIGLIN_HEAD,
            EntityType.ENDER_DRAGON, ItemType.DRAGON_HEAD
    ));
    private final Enchantment enchantment = EnchantmentManager.getEnchant("Beheading");

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (
                killer != null
                && heads.containsKey(entity.getType())
                && killer.getInventory().getItemInMainHand().containsEnchantment(enchantment))
        {
            ItemStack item = killer.getInventory().getItemInMainHand();
            entity.getWorld().dropItemNaturally(event.getEntity().getLocation(), heads.get(entity.getType()).createItemStack());
            if ((double) item.getEnchantmentLevel(enchantment) / 10 >= Math.random()); {
                entity.getWorld().dropItemNaturally(event.getEntity().getLocation(), heads.get(entity.getType()).createItemStack());
            }
        }
    }
}
