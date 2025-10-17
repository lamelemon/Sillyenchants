package io.github.lamelemon.sillyEnchants.Enchantments;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.HashMap;
import java.util.Map;


public class Beheading implements CustomEnchantment, Listener {
    private final Enchantment enchantment;

    private final HashMap<EntityType, ItemType> heads = new HashMap<>(Map.of(
            EntityType.SKELETON, ItemType.SKELETON_SKULL,
            EntityType.WITHER_SKELETON, ItemType.WITHER_SKELETON_SKULL,
            EntityType.WITHER, ItemType.WITHER_SKELETON_SKULL,
            EntityType.ZOMBIE, ItemType.ZOMBIE_HEAD,
            EntityType.PLAYER, ItemType.PLAYER_HEAD,
            EntityType.CREEPER, ItemType.CREEPER_HEAD,
            EntityType.PIGLIN, ItemType.PIGLIN_HEAD,
            EntityType.ENDER_DRAGON, ItemType.DRAGON_HEAD
    ));

    public Beheading(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (
                killer != null
                        && heads.containsKey(entity.getType())
                        && killer.getInventory().getItemInMainHand().containsEnchantment(enchantment)) {
            ItemStack item = killer.getInventory().getItemInMainHand();
            float dropChance = (float) item.getEnchantmentLevel(enchantment) / 255;
            if (dropChance >= Math.random()) {
                entity.getWorld().dropItemNaturally(event.getEntity().getLocation(), heads.get(entity.getType()).createItemStack());
            }
        }
    }
}