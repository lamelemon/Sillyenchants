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

        // check if we hit a block, that we can bounce and if the speed at impact is large enough and not e.g. 0.0001
        if (event.getHitBlockFace() == null || bounces <= 0 || projectile.getVelocity().length() < 0.1) return;

        projectile.getPersistentDataContainer().set(arrowKey, PersistentDataType.INTEGER, bounces - 1);

        // Magnitude of the projectile's velocity when it was shot
        double shootMag = Objects.requireNonNullElse(projectile.getPersistentDataContainer().get(initialVelocityKey, PersistentDataType.DOUBLE), 0d);
        Vector normal = event.getHitBlockFace().getDirection(); // Normal of the block face (which way its pointing)
        Vector startVel = projectile.getVelocity().normalize().multiply(shootMag); // Magnitude of the initial shot with the current direction of travel
        double dot = startVel.dot(normal);
        // reflect = startVel - (normal * 2 * dot) DO NOT CHANGE UNLESS YOU CHANGE THE LINE BELOW, THIS IS JUST FOR CLARITY
        Vector reflect = startVel.subtract(normal.multiply(2 * dot)); // Velocity to get applied onto the arrow

        // Unstick arrow from the hit block
        projectile.teleport(
                reflect.clone().multiply(0.05) // multiply by 0.05 to account for 1 tick of delay
                        .add(projectile.getLocation().toVector())
                        .toLocation(projectile.getWorld())
        );

        // wait for 1 tick so that we can actually change the velocity (why do physics systems have to work like this with teleports)
        Bukkit.getScheduler().runTaskLater(SillyEnchants.getInstance(), () -> {
            projectile.setVelocity(reflect);},
                1
        );
    }
}