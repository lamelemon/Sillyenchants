package io.github.lamelemon.sillyEnchants.Enchantments;

import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static java.lang.Math.pow;

public class Updraft implements Listener, CustomEnchantment{
    private final Enchantment enchantment;

    public Updraft(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @EventHandler
    public void EntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if (event.isGliding() && event.getEntity() instanceof Player player) {
            if (player.getPitch() > -45) return;

            ItemStack chestItem = player.getEquipment().getItem(EquipmentSlot.CHEST);
            int enchantLevel = chestItem.getEnchantmentLevel(enchantment);

            if (enchantLevel <= 0) return;
            player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(pow(enchantLevel * 3, 1 / 3.0))));

            if (player.getGameMode() != GameMode.CREATIVE) {
                player.damageItemStack(chestItem, enchantLevel * 3);
            }
        }

    }
}
