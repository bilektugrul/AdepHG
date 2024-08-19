package tk.shanebee.hg.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.ChestDrop;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.managers.ChestDropManager;

/**
 * Manager for chest drops
 */
public class ChestDropListener implements Listener {
    private final HG plugin;

    public ChestDropListener(HG plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (plugin.getGames().isEmpty()) return;  //Empty game list, no need to continue
        Inventory inv = event.getInventory();
        if (event.getPlayer() instanceof Player) {
            Game game = plugin.getPlayerManager().getGame((Player) event.getPlayer());
            if (game == null) return;

            InventoryHolder invHolder = inv.getHolder();
            Location location;

            if (invHolder instanceof DoubleChest) {
                location = ((DoubleChest) invHolder).getLocation();
            } else if(invHolder instanceof Chest) {
                location = ((Chest) invHolder).getLocation();
            } else {
                location = null;
            }

            // inv.getLocation() returns null for custom created inventories, so we can use it to check if an inventory is one we created
            // note: DEBUG HERE
            if (inv.getType() == InventoryType.CHEST && location == null) {
                ChestDropManager manager = game.getChestDropManager();
                ChestDrop matchingDrop = null;
                for (ChestDrop cd : manager.getChestDrops())
                    if (cd.getChestInv().equals(inv)) {
                        matchingDrop = cd;
                        break;
                    }

                if (matchingDrop != null) {
                    game.getGameArenaData().getBound().getWorld().playSound(matchingDrop.getChestBlock().getLocation(), Sound.EXPLODE, 1f, 1f);
                    matchingDrop.getChestBlock().setType(Material.AIR);
                }
            }
        }


    }


    @EventHandler
    public void onOpenChestDrop(PlayerInteractEvent event) {
        Game game = plugin.getPlayerManager().getGame(event.getPlayer());
        if (game == null) return;
        Block block = event.getClickedBlock();
        ChestDropManager manager = game.getChestDropManager();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            assert block != null;
            if (block.getType().equals(manager.getChestDropType())) {
                ChestDrop matchingDrop = null;
                for (ChestDrop cd : manager.getChestDrops()) {
                    if (cd.getChestBlock().equals(block)) {
                        matchingDrop = cd;
                        break;
                    }
                }
                if (matchingDrop != null) {
                    event.getPlayer().openInventory(matchingDrop.getChestInv());
                    event.setCancelled(true);
                }
            }
        }

    }

}
