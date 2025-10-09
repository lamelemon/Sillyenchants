package io.github.lamelemon.sillyEnchants.Enchantments;

import io.github.lamelemon.sillyEnchants.SillyEnchants;
import io.github.lamelemon.sillyEnchants.Utils.EnchantmentUtil;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static java.lang.Math.pow;

public class EnchantmentShockwave implements Listener {
    private final String name = "shockwave";
    private final Enchantment enchantment = EnchantmentUtil.getEnchant(SillyEnchants.getPluginName() + ":" + name);

    @EventHandler
    private void onEntityHit(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getDamageType().equals(DamageType.MACE_SMASH) && event.getDamager().getType().equals(EntityType.PLAYER) && !event.getEntity().isInWater()) {

            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();

            // fall distance above 1.5 due to mace smash attack having the same requirement
            if (item.containsEnchantment(enchantment) && player.getFallDistance() >= 1.5) {
                player.getWorld().createExplosion(player, event.getEntity().getLocation(), (float) pow(item.getEnchantmentLevel(enchantment) * player.getFallDistance(), 1.0/3.0), false, false);
            }
        }
    }
}
