package tk.shanebee.hg.users;

import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UserManager {

    private final HG plugin;
    private final Set<User> userList = new HashSet<>();

    private StatisticSaveProcess statisticSaveProcess;

    public UserManager(HG plugin) {
        this.plugin = plugin;

        prepareSaveProcess();
    }

    public void prepareSaveProcess() {
        if (statisticSaveProcess != null) {
            statisticSaveProcess.cancel();
        }
        statisticSaveProcess = new StatisticSaveProcess(plugin);
        statisticSaveProcess.start();
    }

    public User loadUser(Player p) {
        return loadUser(p.getName(), true);
    }

    public User loadUser(String name, boolean keep) {
        User user = new User(name);
        if (keep) userList.add(user);

        return user;
    }

    public User getUser(Player p) {
        String name = p.getName();
        return getUser(name);
    }

    public User getUser(String name) {
        for (User user : userList) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }

        return null;
    }

    public boolean isLoaded(String name) {
        return getUser(name) != null;
    }

    public void removeUser(User user) {
        userList.remove(user);
    }

    public Set<User> getUserList() {
        return new HashSet<>(userList);
    }

    public void saveUsers() throws IOException {
        for (User user : userList) {
            user.save();
        }
    }

}