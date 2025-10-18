package io.github.lamelemon.sillyEnchants.Enchantments;

import io.github.lamelemon.sillyEnchants.SillyEnchants;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.min;

public class Harvesting implements Listener, CustomEnchantment {
    private final Enchantment enchantment;
    private final int treeBlockBreakCap;
    private final int oreBlockBreakCap;
    private static Player currentHarvester;
    private int blocksBreakable;

    private final HashSet<Material> allowedOres;
    private final HashMap<String, HashSet<Material>> allowedTrees;

    public Harvesting(Enchantment enchantment, int treeBlockBreakCap, int oreBlockBreakCap, HashMap<String, HashSet<Material>> allowedTrees, HashSet<Material> allowedOres) {
        this.enchantment = enchantment;
        this.treeBlockBreakCap = treeBlockBreakCap;
        this.oreBlockBreakCap = oreBlockBreakCap;
        this.allowedTrees = allowedTrees;
        this.allowedOres = allowedOres;
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        ItemStack currentTool = player.getInventory().getItemInMainHand();

        // 1st condition basically checks if the current tool has the enchantment
        if (!currentTool.containsEnchantment(enchantment) || currentHarvester == player) return;
        currentHarvester = player;

        Block block = event.getBlock();
        if (Tag.ITEMS_PICKAXES.isTagged(currentTool.getType()) && allowedOres.contains(block.getType())) {
            blocksBreakable = min(oreBlockBreakCap * currentTool.getEnchantmentLevel(enchantment), 100); // Cap to prevent server lag with goobers using too high of a level
            Harvester(block, block.getType());
        }
        else if (Tag.ITEMS_AXES.isTagged(currentTool.getType())) {
            for (String key : allowedTrees.keySet()) {
                if (allowedTrees.get(key).contains(block.getType())) {
                    blocksBreakable = min(treeBlockBreakCap * currentTool.getEnchantmentLevel(enchantment), 1000); // Cap to prevent server lag with goobers using too high of a level
                    Harvester(block, allowedTrees.get(key));
                    break;
                }
            }
        }
        currentHarvester = null;
    }

    // Recursively finds and breaks blocks that are allowed to currently be broken
    private void Harvester(Block block, Material allowedOre) {
        if (blocksBreakable <= 0 || allowedOre != block.getType()) return;

        handleBlockBreak(block);

        // Changing this logic changes the pattern that gets made.
        // Currently, it prioritizes going up, as that is the usual way you'd cut a tree
        // (we do ordering due to the cap on blocks broken)
        for (int y = -1; y <= 1; y++ ) {
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    Harvester(block.getRelative(x, y, z), allowedOre);
                }
            }
        }
    }

    private void Harvester(Block block, HashSet<Material> allowedBlocks) {
        if (blocksBreakable <= 0 || !allowedBlocks.contains(block.getType())) return;

        handleBlockBreak(block);

        for (int y = -1; y <= 1; y++ ) {
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    Harvester(block.getRelative(x, y, z), allowedBlocks);
                }
            }
        }
    }

    private void handleBlockBreak(Block block) {
        if (!Tag.LEAVES.isTagged(block.getType())) {
            blocksBreakable--;
        }
        currentHarvester.breakBlock(block);
    }

    public static HashSet<Material> parseSet(List<String> config) {
        HashSet<Material> parsedSet = new HashSet<>();
        for (String key : config) {
            parsedSet.add(Material.valueOf(key));
        }
        return parsedSet;
    }

    public static HashMap<String, HashSet<Material>> parseTrees(ConfigurationSection configurationSection) {
        HashMap<String, HashSet<Material>> parsedTrees = new HashMap<>();
        for (String key : configurationSection.getKeys(false)) {
            parsedTrees.put(key, parseSet(Objects.requireNonNull(configurationSection.getStringList(key))));
        }
        return parsedTrees;
    }
}
