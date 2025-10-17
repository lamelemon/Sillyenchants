package io.github.lamelemon.sillyEnchants.Enchantments;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.min;

public class Harvest implements Listener, CustomEnchantment {
    private final Enchantment enchantment;
    private final int blockBreakCap;
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

    public Harvest(Enchantment enchantment, int blockBreakCap) {
        this.enchantment = enchantment;
        this.blockBreakCap = blockBreakCap;
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        currentTool = player.getInventory().getItemInMainHand();
        blocksBroken = min(blockBreakCap * currentTool.getEnchantmentLevel(enchantment), 1000); // Cap to prevent server lag with goobers using too high of a level

        // 1st condition basically checks if the current tool has the enchantment
        if (blocksBroken == 0 || currentHarvester == player) return;
        currentHarvester = player;

        Object allowedTags = null;
        if (Tag.ITEMS_AXES.isTagged(currentTool.getType())) {
            allowedTags = allowedAxeTags;
        } else if (Tag.ITEMS_PICKAXES.isTagged(currentTool.getType())) {
            for (Tag<Material> tag : allowedPickaxeTags) {
                if (tag.isTagged(event.getBlock().getType())) {
                    allowedTags = tag;
                    break;
                }
            }
        } else return;

        Harvester(event.getBlock(), allowedTags);

        currentHarvester = null;
    }

    // Recursively finds and breaks blocks that are allowed to currently be broken
    private void Harvester(Block block, Object allowedMaterials) {
        // A bunch of checks to stop overt recursion
        // isTagged() can be how it is due to the first condition.
        if ((block.getDrops(currentTool).isEmpty() && !Tag.LEAVES.isTagged(block.getType()))
                || blocksBroken <= 0
        ) return;

        boolean allowed = false;

        if (allowedMaterials instanceof Tag<?> tag) {
            allowed = isTagged(block.getType(), (Tag<Material>) tag);
        } else if (allowedMaterials instanceof HashSet<?> set) {
            allowed = isTagged(block.getType(), (HashSet<Tag<Material>>) set);
        }

        if (!allowed) return;

        // Break block and damage tool
        blocksBroken--;
        currentHarvester.breakBlock(block);
        //if (takeDamage) currentHarvester.damageItemStack(currentTool, 1);

        // Changing this logic changes the pattern made.
        // Currently, it prioritizes going up, as that is the usual way you'd cut a tree
        // (we do ordering due to the cap on blocks broken)
        Harvester(block.getRelative(0, 1, 0), allowedMaterials); // check above
        Harvester(block.getRelative(0, -1, 0), allowedMaterials);
        for (int y = -1; y <= 1; y++ ) {
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    Harvester(block.getRelative(x, y, z), allowedMaterials);
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

    private boolean isTagged(Material material, Tag<Material> allowedMaterial) {
        return allowedMaterial.isTagged(material);
    }
}
