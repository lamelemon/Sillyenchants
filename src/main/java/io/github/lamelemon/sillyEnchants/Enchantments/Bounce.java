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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
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
            PersistentDataContainer dataContainer = event.getProjectile().getPersistentDataContainer();
            dataContainer.set(arrowKey,
                    PersistentDataType.INTEGER,
                    bow.getEnchantmentLevel(enchantment)
            );
        }
    }

    @EventHandler
    public void projectileHitEvent(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        int bounces = Objects.requireNonNullElse(projectile.getPersistentDataContainer().get(arrowKey, PersistentDataType.INTEGER), 0);

        if (event.getHitBlockFace() == null || bounces <= 0 || projectile.getVelocity().length() < 0.5) return;

        projectile.getPersistentDataContainer().set(arrowKey, PersistentDataType.INTEGER, bounces - 1);

        Vector normal = event.getHitBlockFace().getDirection();
        Vector startVel = projectile.getVelocity().clone();

        double dot = startVel.dot(normal);
        Vector reflect = startVel.clone().subtract(normal.clone().multiply(2 * dot));

        SillyEnchants.getInstance().getLogger().info("startvel " + startVel + " velocity is " + projectile.getVelocity() + " hit block face " + event.getHitBlockFace() + " hit block direction " + normal);

        projectile.teleport(
                reflect.clone().multiply(0.05)
                        .add(projectile.getLocation().toVector())
                        .toLocation(projectile.getWorld())
        );

        Bukkit.getScheduler().runTaskLater(SillyEnchants.getInstance(), () -> {
            projectile.setVelocity(reflect);
            SillyEnchants.getInstance().getLogger().info("velocity is " + projectile.getVelocity());
        },
                1
        );
    }
}