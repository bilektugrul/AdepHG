package tk.shanebee.hg.shop;

import com.hakan.core.HCore;
import com.hakan.core.item.ItemBuilder;
import com.hakan.core.ui.inventory.InventoryGui;
import io.github.bilektugrul.butils.Utils;
import me.despical.commons.compat.XMaterial;
import me.despical.commons.compat.XSound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.users.User;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ShopGUI extends InventoryGui {

    private final FileConfiguration config;
    private final String id;

    public ShopGUI(String id, FileConfiguration config) {
        super("shop-" + id, Utils.getColoredString(config, "title"), config.getInt("size"), InventoryType.CHEST);

        this.id = id;
        this.config = config;
    }

    @Override
    public void onOpen(@NotNull Player player) {
        for (String key : config.getConfigurationSection("slots").getKeys(false)) {
            User user = HG.getPlugin().getUserManager().getUser(player);

            String path = "slots." + key + ".";

            int slot = config.getInt(path + "slot");
            int price = config.getInt(path + "price");
            String name = Utils.getColoredString(config, path + "display");

            List<String> lore = Utils.colored(config.getStringList(path + "lore"));

            if (config.getString("add-default-lore-to", "bottom").equalsIgnoreCase("bottom")) {
                lore.addAll(Utils.colored(config.getStringList("default-lore")));
            } else if (config.getString("add-default-lore-to") != null) {
                lore.addAll(0, Utils.colored(config.getStringList("default-lore")));
            }

            String selected = user.getSelected(id);
            boolean isSelected = selected != null && selected.equalsIgnoreCase(key);

            lore.replaceAll(str -> str.replace("%durum%", isSelected ? "Kullanımda" : user.isBought(id, key) ? "Satın Alınmış" : "Satın Alınmamış"));
            lore.replaceAll(str -> str.replace("%price%", String.valueOf(price)));

            if (id.equals("deathMessages")) {
                List<String> messages = Utils.getStringList(config, path + "list");
                messages.replaceAll(msg -> {
                    msg = msg.replace("%victim%", player.getName());
                    return msg.replace("%attacker%", "bir katil");
                });
                findAndReplaceMsgs(lore, "%messages%", messages);
            }

            if (id.equals("lastWords")) {
                List<String> messages = Utils.getStringList(config, path + "list");
                findAndReplaceMsgs(lore, "%messages%", messages);
            }

            if (id.equals("joinMessages")) {
                String message = Utils.colored(config.getString(path + "msg").replace("%player%", player.getName()));
                lore.replaceAll(str -> str.replace("%mesaj%", message));
            }

            String materialStr = config.getString(path + "item").toUpperCase(Locale.ENGLISH);
            ItemStack item = XMaterial.matchXMaterial(materialStr).get().parseItem();

            ItemStack toPut = HCore.itemBuilder(item)
                    .name(name)
                    .lores(lore)
                    .addItemFlags(ItemFlag.values())
                    .glow(isSelected)
                    .build();

            setItem(slot, toPut, e -> {
                if (id.equals("deathSounds")) {
                    if (e.getClick().isRightClick() && e.getClick().isShiftClick()) {
                        player.playSound(player.getLocation(), XSound.matchXSound(config.getString(path + "sound")).get().parseSound(), 5, 1);
                    }
                }

                if (e.isRightClick() && !user.isBought(id, key)) {
                    if (!HG.getPlugin().getEconomy().has(player, price)) {
                        player.closeInventory();
                        player.sendMessage("yetersiz para");
                        return;
                    }

                    user.buy(id, key);
                    this.open(player);
                }

                if (e.isLeftClick() && user.isBought(id, key)) {
                    user.setSelectedFeatureItem(id, key);
                    this.open(player);
                    player.sendMessage("selected " + key);
                }
            });

            setItem(config.getInt("back-item-slot"), HCore.itemBuilder(XMaterial.matchXMaterial(config.getString("back-item")).get().parseItem())
                    .name(Utils.colored(config.getString("back-item-name")))
                    .build(), e -> new MainShopGUI(HG.getPlugin().getShop(), HG.getPlugin().getShop().mainShop).open(player));
        }
    }

    public void findAndReplaceMsgs(List<String> replaceIn, String lookFor, List<String> replaceWith) {
        int l = 0;
        for (String line : replaceIn) {
            if (line.contains(lookFor)) {
                break;
            }
            l++;
        }

        replaceIn.remove(l);
        replaceIn.addAll(l, replaceWith);
    }

}
