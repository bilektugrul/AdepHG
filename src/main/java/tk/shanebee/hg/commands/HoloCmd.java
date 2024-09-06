package tk.shanebee.hg.commands;

import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.serializer.LocationSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import tk.shanebee.hg.HG;

public class HoloCmd extends BaseCmd {

    public static FileConfiguration leaderboardFile = ConfigUtils.getConfig(HG.getPlugin(), "features/leaderboardholo");

    public HoloCmd() {
        forcePlayer = true;
        cmdName = "shop";
        argLength = 1;
        forceInRegion = false;
    }

    @Override
    public boolean run() {
        if (player.hasPermission("hg.sethololoc")) {

            if (args.length >= 1) {

                String arg = args[0];

                if (arg.equalsIgnoreCase("leaderboard")) {

                    leaderboardFile.set("holo-location", player.getLocation());
                    player.sendMessage("New holo loc: " + LocationSerializer.toString(player.getLocation()));

                } else {

                    leaderboardFile.set("stats-holo-location", player.getLocation());
                    player.sendMessage("New stats holo loc: " + LocationSerializer.toString(player.getLocation()));

                }
            }
        }
        return true;
    }

}
