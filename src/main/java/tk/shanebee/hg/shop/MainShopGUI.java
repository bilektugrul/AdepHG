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

public class MainShopGUI extends InventoryGui {

    private final Shop shop;
    private final FileConfiguration config;

    public MainShopGUI(Shop shop, FileConfiguration config) {
        super("mainshop", Utils.getColoredString(config, "title"), config.getInt("size"), InventoryType.CHEST);

        this.shop = shop;
        this.config = config;
    }

    @Override
    public void onOpen(@NotNull Player player) {
        for (String key : config.getConfigurationSection("slots").getKeys(false)) {
            String path = "slots." + key + ".";

            int slot = config.getInt(path + "slot");
            String name = Utils.getColoredString(config, path + "name");
            List<String> lore = Utils.colored(Utils.getStringList(config, path + "lore"));
            Material item = XMaterial.matchXMaterial(config.getString(path + "item")).get().parseMaterial();
            String next = config.getString(path + "next");

            ItemStack toPut = HCore.itemBuilder(item)
                    .name(name)
                    .lores(lore)
                    .addItemFlags(ItemFlag.values())
                    .build();

            setItem(slot, toPut, e -> new ShopGUI(shop.getConfig(next), next).open(player));
        }
    }
}
