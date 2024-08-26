package tk.shanebee.hg.users;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tk.shanebee.hg.HG;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class User {

    private static final HG plugin = JavaPlugin.getPlugin(HG.class);

    private YamlConfiguration data;

    private final String name;
    private final Map<String, List<String>> boughtStuff = new HashMap<>();

    public User(String name) {
        this.name = name;
        this.data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/players/" + name + ".yml"));

        if (!data.isConfigurationSection("bought-stuff")) return;

        for (String feature : data.getConfigurationSection("bought-stuff").getKeys(false)) {
            List<String> include = data.getStringList("bought-stuff." + feature);
            boughtStuff.put(feature, include);
        }
    }

    public void buy(String feature, String item) {
        List<String> current = boughtStuff.getOrDefault(feature, new ArrayList<>());
        current.add(item);
        boughtStuff.put(feature, current);
    }

    public boolean isBought(String feature, String item) {
        List<String> current = boughtStuff.getOrDefault(feature, new ArrayList<>());
        return current.contains(item);
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(name);
    }

    public YamlConfiguration getData() {
        return data;
    }

    public void save() throws IOException {
        _save();
    }

    private void _save() throws IOException {
        for (String feature : boughtStuff.keySet()) {
            data.set("bought-stuff." + feature, boughtStuff.get(feature));
        }
        data.save(new File(plugin.getDataFolder() + "/players/" + name + ".yml"));
    }

}