package tk.shanebee.hg.shop;

import com.hakan.core.HCore;
import com.hakan.core.ui.inventory.InventoryGui;
import io.github.bilektugrul.butils.Utils;
import me.despical.commons.compat.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShopGUI extends InventoryGui {

    private final FileConfiguration config;
    private final String id;

    public ShopGUI(FileConfiguration config, String id) {
        super("shop-" + id, Utils.getColoredString(config, "title"), config.getInt("size"), InventoryType.CHEST);

        this.id = id;
        this.config = config;
    }

    @Override
    public void onOpen(@NotNull Player player) {
        for (String key : config.getConfigurationSection("slots").getKeys(false)) {
            String path = "slots." + key + ".";

            int slot = config.getInt(path + "slot");
            String name = Utils.getColoredString(config, path + "display");
            List<String> lore = Utils.colored(Utils.getStringList(config, path + "lore"));
            if (id.equals("deathMessages")) {
                List<String> messages = Utils.getStringList(config, path + "list");
                messages.replaceAll(msg -> {
                    msg = msg.replace("%victim%", player.getName());
                    return msg.replace("%attacker%", "bir katil");
                });
                findAndReplaceMsgs(lore, "%messages%", messages);
            }

            Material item = XMaterial.matchXMaterial(config.getString(path + "item")).get().parseMaterial();

            ItemStack toPut = HCore.itemBuilder(item)
                    .name(name)
                    .lores(lore)
                    .addItemFlags(ItemFlag.values())
                    .build();

            setItem(slot, toPut);
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
