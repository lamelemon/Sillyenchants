package io.github.lamelemon.sillyEnchants.Enchantments;

import org.bukkit.Location;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import static java.lang.Math.pow;


public class Shockwave implements Listener, CustomEnchantment {
    private final Enchantment enchantment;

    public Shockwave(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getDamageType() != DamageType.MACE_SMASH) {
            return;
        }
        LivingEntity damager = (LivingEntity) event.getDamager();
        EntityEquipment entityEquipment = damager.getEquipment();

        if (entityEquipment == null) {
            return;
        }

        ItemStack item = entityEquipment.getItemInMainHand();
        if (item.containsEnchantment(enchantment)) {

            Location explosionPoint = event.getDamager().getLocation().toVector().midpoint(event.getEntity().getLocation().toVector()).toLocation(event.getDamager().getWorld());
            int level = item.getEnchantmentLevel(enchantment);

            damager.getWorld().createExplosion(damager,
                    explosionPoint,
                    (float) pow(level * damager.getFallDistance(), 1 / Math.E),  // Don't question it
                    false,
                    false
            );
        }
    }
}
