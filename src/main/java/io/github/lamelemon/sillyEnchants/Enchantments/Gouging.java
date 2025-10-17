package io.github.lamelemon.sillyEnchants.Enchantments;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffectType;


public class Gouging implements Listener, CustomEnchantment {
    private final Enchantment enchantment;

    public Gouging(Enchantment enchantment) {
        this.enchantment = enchantment;
    }
    
    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        LivingEntity damager = (LivingEntity) event.getDamager();
        EntityEquipment equipment = damager.getEquipment();
        if (equipment == null) return;

        int enchantmentLevel = equipment.getItemInMainHand().getEnchantmentLevel(enchantment);
        if (enchantmentLevel > 0) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            entity.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(enchantmentLevel / 2, 1));
        }
    }
}
