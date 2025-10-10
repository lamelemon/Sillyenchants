package io.github.lamelemon.sillyEnchants.Enchantments;

import io.github.lamelemon.sillyEnchants.SillyEnchants;
import io.github.lamelemon.sillyEnchants.Utils.EnchantmentUtil;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import static java.lang.Math.pow;

public class EnchantmentShockwave implements Listener {
    private final String name = "shockwave";
    private final Enchantment enchantment = EnchantmentUtil.getEnchant(name);

    @EventHandler
    private void onEntityHit(EntityDamageByEntityEvent event) {
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
            damager.getWorld().createExplosion(damager,
                    event.getEntity().getLocation(),
                    (float) pow(item.getEnchantmentLevel(enchantment) * damager.getFallDistance(), 1.0/3.0),
                    false,
                    false
            );
        }
    }
}
