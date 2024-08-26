package tk.shanebee.hg.listeners;

import io.github.bilektugrul.butils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.users.User;
import tk.shanebee.hg.users.UserManager;

import java.io.IOException;
import java.util.Locale;

public class PlayerListener implements Listener {

    private final HG plugin;
    private final UserManager userManager;

    public PlayerListener(HG plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        userManager.loadUser(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws IOException {
        Player player = e.getPlayer();
        User user = userManager.getUser(player);
        user.save();
        userManager.removeUser(user);
    }

}
