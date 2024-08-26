package tk.shanebee.hg.users;

import org.bukkit.scheduler.BukkitRunnable;
import tk.shanebee.hg.HG;

import java.io.IOException;

public class StatisticSaveProcess extends BukkitRunnable {

    private final HG plugin;

    public StatisticSaveProcess(HG plugin) {
        this.plugin = plugin;
    }

    public void start() {
        plugin.getLogger().info("Starting automatic user data save...");
        int i = plugin.getConfig().getInt("database.auto-save-interval");
        long time = (i * 60L) * 20;
        runTaskTimerAsynchronously(plugin, time, time);
    }

    @Override
    public void run() {
        try {
            plugin.saveAllUsers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}