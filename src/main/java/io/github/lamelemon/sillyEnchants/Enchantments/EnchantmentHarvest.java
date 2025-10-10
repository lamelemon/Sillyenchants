package io.github.lamelemon.sillyEnchants.Enchantments;

import io.github.lamelemon.sillyEnchants.SillyEnchants;
import io.github.lamelemon.sillyEnchants.Utils.EnchantmentUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.min;

public class EnchantmentHarvest implements Listener {
    private static final Enchantment enchantment = EnchantmentUtil.getEnchant("harvest");
    private static final int blockBreakCap = 100;
    private static Player currentHarvester;
    private static ItemStack currentTool;
    private int blocksBroken;
    private static final HashSet<Tag<Material>> allowedAxeTags = new HashSet<>(Set.of(
            Tag.LOGS,
            Tag.LEAVES
    ));
    private static final HashSet<Tag<Material>> allowedPickaxeTags = new HashSet<>(Set.of(
        Tag.COAL_ORES,
        Tag.COPPER_ORES,
        Tag.DIAMOND_ORES,
        Tag.GOLD_ORES,
        Tag.EMERALD_ORES,
        Tag.IRON_ORES,
        Tag.LAPIS_ORES,
        Tag.REDSTONE_ORES
    ));

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        currentTool = player.getInventory().getItemInMainHand();
        blocksBroken = min(blockBreakCap * currentTool.getEnchantmentLevel(enchantment), 1000); // Cap to prevent server lag with goobers using too high of a level

        // 1st condition basically checks if the current tool has the enchantment
        if (blocksBroken == 0 || currentHarvester == player) return;
        currentHarvester = player;

        HashSet<Tag<Material>> allowedTags = new HashSet<>();
        if (Tag.ITEMS_AXES.isTagged(currentTool.getType())) {
            allowedTags = allowedAxeTags;
        } else if (Tag.ITEMS_PICKAXES.isTagged(currentTool.getType())) {
            allowedTags = allowedPickaxeTags;
        }

        Harvester(event.getBlock(), player.getGameMode() != GameMode.CREATIVE, allowedTags);

        currentHarvester = null;
    }

    // Recursively finds and breaks blocks that are allowed to currently be broken
    private void Harvester(Block block, boolean takeDamage, HashSet<Tag<Material>> allowedMaterials) {
        // A bunch of checks to stop overt recursion
        // isTagged() can be how it is due to the first condition.
        if ((block.getDrops(currentTool).isEmpty() && !Tag.LEAVES.isTagged(block.getType())) || blocksBroken <= 0 || !isTagged(block.getType(), allowedMaterials)) return;

        // Break block and damage tool
        blocksBroken--;
        block.breakNaturally(currentTool);
        if (takeDamage) currentHarvester.damageItemStack(currentTool, 1);

        // Changing this logic changes the pattern made.
        // Currently, it prioritizes going up, as that is the usual way you'd cut a tree
        // (we do ordering due to the cap on blocks broken)
        Harvester(block.getRelative(0, 1, 0), takeDamage, allowedMaterials); // check above
        Harvester(block.getRelative(0, -1, 0), takeDamage, allowedMaterials);
        for (int y = -1; y <= 1; y++ ) {
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    Harvester(block.getRelative(x, y, z), takeDamage, allowedMaterials);
                }
            }
        }
    }

    // Helper for detecting if the broken block is acceptable
    private boolean isTagged(Material material, HashSet<Tag<Material>> allowedMaterials) {
        for(Tag<Material> tag : allowedMaterials) {
            if (tag.isTagged(material)) {
                return true;
            }
        }
        return false;
    }
}
