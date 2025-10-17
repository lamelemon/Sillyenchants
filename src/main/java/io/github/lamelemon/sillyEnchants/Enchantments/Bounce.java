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
    private final NamespacedKey initialVelocityKey = new NamespacedKey(SillyEnchants.getInstance(), "initialVelocity");

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
            dataContainer.set(initialVelocityKey,
                    PersistentDataType.DOUBLE,
                    event.getProjectile().getVelocity().length()
            );
        }
    }

    // Vector math WOOHOO
    @EventHandler
    public void projectileHitEvent(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        PersistentDataContainer dataContainer = projectile.getPersistentDataContainer();
        int bounces = Objects.requireNonNullElse(dataContainer.get(arrowKey, PersistentDataType.INTEGER), 0);

        if (event.getHitBlockFace() == null || bounces <= 0 || projectile.getVelocity().length() < 0.5) return;

        projectile.getPersistentDataContainer().set(arrowKey, PersistentDataType.INTEGER, bounces - 1);

        double shootMag = Objects.requireNonNullElse(projectile.getPersistentDataContainer().get(initialVelocityKey, PersistentDataType.DOUBLE), 0D);
        Vector normal = event.getHitBlockFace().getDirection();
        Vector startVel = projectile.getVelocity().clone().multiply(shootMag);
        double dot = startVel.dot(normal);
        Vector reflect = startVel.clone().subtract(normal.clone().multiply(2 * dot));

        // Unstick arrow from the hit block
        projectile.teleport(
                reflect.clone().multiply(0.05) // multiply by 0.05 to account for 1 tick of delay
                        .add(projectile.getLocation().toVector())
                        .toLocation(projectile.getWorld())
        );

        Bukkit.getScheduler().runTaskLater(SillyEnchants.getInstance(), () -> {
            projectile.setVelocity(reflect);
        },
                1
        );
    }
}