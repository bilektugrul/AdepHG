package tk.shanebee.hg.listeners;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.event.HologramClickEvent;
import io.github.bilektugrul.butils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.commands.HoloCmd;
import tk.shanebee.hg.data.Leaderboard;
import tk.shanebee.hg.users.User;
import tk.shanebee.hg.users.UserManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {

    private final HG plugin;
    private final UserManager userManager;

    private Map<Player, HoloMode> holoTracker = new HashMap<>();

    public PlayerListener(HG plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        userManager.loadUser(player);

        statHolo(player);
        holo(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws IOException {
        Player player = e.getPlayer();
        User user = userManager.getUser(player);
        user.save();
        userManager.removeUser(user);

        DHAPI.removeHologram("hgstat-" + player.getName());
        DHAPI.removeHologram("hg-" + player.getName());
    }

    public void statHolo(Player player) {
        Location location = Utils.getLocation((YamlConfiguration) HoloCmd.leaderboardFile, "stats-holo-location");
        if (location == null) return;

        List<String> lines = PlaceholderAPI.setPlaceholders(player, Utils.getColoredStringList(HoloCmd.leaderboardFile, "stats-holo"));
        Hologram holo = DHAPI.getHologram("hgstat-" + player.getName());

        if (holo == null) {
            holo = DHAPI.createHologram("hgstat-" + player.getName(), location, lines);
            holo.setDefaultVisibleState(true);
            holo.hideAll();
            holo.removeHidePlayer(player);
            holo.setShowPlayer(player);
        } else {
            DHAPI.setHologramLines(holo, lines);
        }
    }

    public void holo(Player player) {
        Location location = Utils.getLocation((YamlConfiguration) HoloCmd.leaderboardFile, "holo-location");
        if (location == null) return;

        Leaderboard leaderboard = HG.getPlugin().getLeaderboard();
        Hologram holo = DHAPI.getHologram("hg-" + player.getName());
        HoloMode mode = holoTracker.get(player);
        if (mode == null) {
            mode = HoloMode.WINS;
        } else if (mode == HoloMode.WINS) {
            mode = HoloMode.KILLS;
        } else {
            mode = HoloMode.WINS;
        }

        holoTracker.put(player, mode);

        List<String> header, bottom, lines = new ArrayList<>();
        String format;
        if (mode == HoloMode.WINS) {
            header = Utils.getColoredStringList(HoloCmd.leaderboardFile, "wins-header");
            bottom = Utils.getColoredStringList(HoloCmd.leaderboardFile, "wins-bottom");
            format = Utils.listToString(Utils.getColoredStringList(HoloCmd.leaderboardFile, "wins-format"));

            for (int i = 0; i < 10; i++) {
                String name = "...";
                String win = "...";

                if (leaderboard.sorted_players_wins.size() > i) {
                    name = leaderboard.sorted_players_wins.get(i);
                    win = leaderboard.sorted_scores_wins.get(i);
                }

                lines.add(format
                        .replace("%no%", String.valueOf(i + 1))
                        .replace("%player%", name)
                        .replace("%score%", win));
            }

        } else {
            header = Utils.getColoredStringList(HoloCmd.leaderboardFile, "kills-header");
            bottom = Utils.getColoredStringList(HoloCmd.leaderboardFile, "kills-bottom");
            format = Utils.listToString(Utils.getColoredStringList(HoloCmd.leaderboardFile, "wins-format"));

            for (int i = 0; i < 10; i++) {
                String name = "...";
                String win = "...";

                if (leaderboard.sorted_players_kills.size() > i) {
                    name = leaderboard.sorted_players_kills.get(i);
                    win = leaderboard.sorted_scores_kills.get(i);
                }

                lines.add(format
                        .replace("%no%", String.valueOf(i + 1))
                        .replace("%player%", name)
                        .replace("%score%", win));
            }
        }

        List<String> allLines = new ArrayList<>();
        allLines.addAll(header);
        allLines.addAll(lines);
        allLines.add("");
        allLines.addAll(bottom);

        if (holo == null) {
            holo = DHAPI.createHologram("hg-" + player.getName(), location, allLines);
            holo.setDefaultVisibleState(true);
            holo.hideAll();
            holo.removeHidePlayer(player);
            holo.setShowPlayer(player);
        } else {
            DHAPI.setHologramLines(holo, allLines);
        }
    }

    @EventHandler
    public void onHologramClick(HologramClickEvent e) {
        String id = e.getHologram().getId();
        Player p = e.getPlayer();

        if (id.equalsIgnoreCase("hg-" + p.getName())) {
            holo(p);
        }
    }

    public enum HoloMode {
        WINS, KILLS
    }

}
