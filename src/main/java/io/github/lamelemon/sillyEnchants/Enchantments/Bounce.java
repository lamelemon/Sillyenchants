package io.github.lamelemon.sillyEnchants.Enchantments;

import io.github.lamelemon.sillyEnchants.SillyEnchants;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Bounce implements Listener, CustomEnchantment {
    private final Enchantment enchantment;
    private final NamespacedKey arrowKey = new NamespacedKey(SillyEnchants.getInstance(), "bounces");

    public Bounce(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @EventHandler
    public void entityShootBowEvent(EntityShootBowEvent event) {
        ItemStack bow = event.getBow();
        ItemStack arrow = event.getConsumable();
        if (bow != null && bow.containsEnchantment(enchantment) && arrow != null && Tag.ITEMS_ARROWS.isTagged(arrow.getType())) {
            event.getProjectile().getPersistentDataContainer().set(arrowKey,
                    PersistentDataType.INTEGER,
                    bow.getEnchantmentLevel(enchantment)
            );
        }
    }

    @EventHandler
    public void projectileHitEvent(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        int bounces = Objects.requireNonNullElse(projectile.getPersistentDataContainer().get(arrowKey, PersistentDataType.INTEGER), 0);

        if (event.getHitBlockFace() == null || bounces <= 0) return;

        projectile.getPersistentDataContainer().set(arrowKey, PersistentDataType.INTEGER, bounces - 1);
        Vector normal = event.getHitBlockFace().getDirection();
        Vector vel = projectile.getVelocity().clone().subtract(projectile.getVelocity().clone().multiply(normal).multiply(2));
        SillyEnchants.getInstance().getLogger().info("vel " + vel + " velocity is " + projectile.getVelocity() + " hit block face " + event.getHitBlockFace() + " hit block direction " + normal);

        projectile.teleport(normal.multiply(projectile.getWidth() / 2).add(projectile.getLocation().toVector()).toLocation(projectile.getWorld()));

        Bukkit.getScheduler().runTaskLater(SillyEnchants.getInstance(), () -> {
            projectile.setVelocity(vel.multiply(3));
            SillyEnchants.getInstance().getLogger().info("velocity is " + projectile.getVelocity());
        },1);
    }
}