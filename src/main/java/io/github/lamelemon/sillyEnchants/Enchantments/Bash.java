package io.github.lamelemon.sillyEnchants.Enchantments;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Bash implements Listener, CustomEnchantment {
    private final Enchantment enchantment;

    public Bash(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @EventHandler
    public void PlayerArmSwingEvent(PlayerArmSwingEvent event) {
        Player player = event.getPlayer();
        int enchantLevel = player.getInventory().getItemInMainHand().getEnchantmentLevel(enchantment);
        if (enchantLevel <= 0) return;

        if (((Entity) player).isOnGround()) {
            player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(enchantLevel)));
        }
    }
}
