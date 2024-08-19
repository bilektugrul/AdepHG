package tk.shanebee.hg.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.block.Block;
import tk.shanebee.hg.data.Config;

import java.util.List;

public class BlockUtils {

    // Preset to empty lists, just in case
    private static ImmutableSet<Material> BONUS_BLOCK_MATERIALS = ImmutableSet.of();
    private static ImmutableSet<Material> BREAKABLE_BLOCK_MATERIALS = ImmutableSet.of();
    private static boolean BREAKABLE_BLOCK_ALL = false;

    /**
     * Setup the block sets.
     * <p>Should only be used internally</p>
     */
    public static void setupBuilder() {
        BONUS_BLOCK_MATERIALS = setup(Config.bonusBlockTypes).build();

        for (String string : Config.blocks) {
            if (string.equalsIgnoreCase("all")) {
                BREAKABLE_BLOCK_ALL = true;
                return;
            }
        }
            BREAKABLE_BLOCK_MATERIALS = setup(Config.blocks).build();
    }

    /**
     * Check if a block counts as a bonus chest
     *
     * @param block Block to check
     * @return True if block is a bonus chest
     */
    public static boolean isBonusBlock(Block block) {
        Material mat = block.getType();
        // No mater what is put in the config, a chest will never be a bonus block
        return mat != Material.CHEST && BONUS_BLOCK_MATERIALS.contains(mat);
    }

    /**
     * Check if a block is breakable/placeable
     *
     * @param block Block to check
     * @return True if block can be broken/placed
     */
    public static boolean isBreakableBlock(Block block) {
        return BREAKABLE_BLOCK_ALL || BREAKABLE_BLOCK_MATERIALS.contains(block.getType());
    }

    private static ImmutableSet.Builder<Material> setup(List<String> materialStrings) {
        ImmutableSet.Builder<Material> materialBuilder = ImmutableSet.builder();

        for (String materialString : materialStrings) {
            if (!materialString.contains("tag")) {
                for (Material material : Material.values()) {
                    if (materialString.equalsIgnoreCase("ALL") || material.toString().equalsIgnoreCase(materialString)) {
                        materialBuilder.add(material);
                    }

                }
            }
        }

        return materialBuilder;
    }

}
