package io.github.lamelemon.sillyEnchants.Commands;

import io.github.lamelemon.sillyEnchants.Utils.EnchantmentUtil;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SillyEnchantCommand implements BasicCommand {


    // Syntax: /SillyEnchant [Enchantment] [level]
    @Override
    public void execute(@NotNull CommandSourceStack source, String[] args) {

        if (args.length == 0) {
            source.getSender().sendRichMessage("<red>Missing arguments!");
            return;
        }

        String enchantName = args[0].toLowerCase();
        int level = 1;

        if (args.length >= 2) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                source.getSender().sendRichMessage("<red>Invalid level! Defaulting.");
            }
        }

        if (source.getExecutor() instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
                source.getSender().sendRichMessage("<red>You must be holding an item!");
                return;
            }

            try {
                Enchantment enchantment = EnchantmentUtil.getEnchant(enchantName);
                item.addUnsafeEnchantment(enchantment, level);
            } catch (IllegalArgumentException e) {
                source.getSender().sendRichMessage("<red>Invalid enchantment! Either the enchantment doesn't exist or it can't go on the item you're holding.");
            }
        }




    }
}
