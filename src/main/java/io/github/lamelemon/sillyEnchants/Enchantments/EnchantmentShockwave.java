package io.github.lamelemon.sillyEnchants.Enchantments;

import io.github.lamelemon.sillyEnchants.SillyEnchants;
import io.github.lamelemon.sillyEnchants.Utils.EnchantmentUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static java.lang.Math.pow;
import static org.joml.Math.*;

public class EnchantmentShockwave implements Listener {
    private final String name = "shockwave";
    private final Enchantment enchantment = EnchantmentUtil.getEnchant(SillyEnchants.getPluginName() + ":" + name);
    private final float maxFallDistance = 250;
    private final float minFallDistance = 1.5f;

    @EventHandler
    private void onEntityHit(EntityDamageByEntityEvent event) {
        SillyEnchants.getInstance().getLogger().info("trying to shockwave!" + enchantment + event.getDamager());
        if (event.getDamager().getType() == EntityType.PLAYER && !event.getEntity().isInWater()) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.containsEnchantment(enchantment) && player.getFallDistance() > minFallDistance) {
                int level = item.getEnchantmentLevel(enchantment);
                player.getWorld().createExplosion(event.getEntity().getLocation(), (float) pow(level * min(player.getFallDistance(), maxFallDistance), 1.0/3.0));
                SillyEnchants.getInstance().getLogger().info("Shockwave");
            }
        }
    }
}
