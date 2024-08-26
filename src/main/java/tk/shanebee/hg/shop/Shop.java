package tk.shanebee.hg.shop;

import io.github.bilektugrul.butils.Utils;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import tk.shanebee.hg.HG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Shop {

    private final HG plugin;
    public FileConfiguration mainShop, deathMessages, joinMessages, deathSounds, lastWords, rides;
    public Map<String, FileConfiguration> configs = new HashMap<>();

    public Shop(HG plugin) {
        this.plugin = plugin;
        loadShops();
    }

    public void loadShops() {
        mainShop = ConfigUtils.getConfig(plugin, "features/mainShop");
        deathMessages = ConfigUtils.getConfig(plugin, "features/deathMessages");
        joinMessages = ConfigUtils.getConfig(plugin, "features/joinMessages");
        deathSounds = ConfigUtils.getConfig(plugin, "features/deathSounds");
        lastWords = ConfigUtils.getConfig(plugin, "features/lastWords");
        rides = ConfigUtils.getConfig(plugin, "features/rides");

        configs.put("mainShop", mainShop);
        configs.put("deathMessages", deathMessages);
        configs.put("joinMessages", joinMessages);
        configs.put("deathSounds", deathSounds);
        configs.put("lastWords", lastWords);
        configs.put("rides", rides);
    }

    public FileConfiguration getConfig(String name) {
        return configs.get(name);
    }

    public String getRandomOfFeature(String feature, String item) {
        List<String> list = configs.get(feature).getStringList("slots." + item + ".list");
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public String getMessageOf(String feature, String item) {
        return Utils.colored(configs.get(feature).getString("slots." + item + ".msg"));
    }

}