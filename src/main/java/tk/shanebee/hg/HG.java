package tk.shanebee.hg;

import com.hakan.core.HCore;
import io.github.bilektugrul.butils.BUtilsLib;
import me.despical.commons.configuration.ConfigUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import tk.shanebee.hg.commands.*;
import tk.shanebee.hg.data.*;
import tk.shanebee.hg.economy.VaultManager;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.listeners.*;
import tk.shanebee.hg.managers.*;
import tk.shanebee.hg.shop.Shop;
import tk.shanebee.hg.users.UserManager;
import tk.shanebee.hg.util.*;

import java.io.IOException;
import java.util.*;

/**
 * <b>Main class for HungerGames</b>
 */
public class HG extends JavaPlugin {

	//Maps
	private Map<String, BaseCmd> cmds;
	private Map<UUID, PlayerSession> playerSession;

	private Map<ItemStack, Integer> itemRarityMap;

	private Map<ItemStack, Integer> itemCostMap;

	private Map<ItemStack, Integer> bonusRarityMap;

	private Map<ItemStack, Integer> bonusCostMap;

	//Lists
	private List<Game> games;

	//Instances
	private static HG plugin;
	private Config config;
	private Manager manager;
	private PlayerManager playerManager;
	private ArenaConfig arenaconfig;
	private KillManager killManager;
	private RandomItems randomItems;
	private Language lang;
	private KitManager kitManager;
	private ItemStackManager itemStackManager;
	private Leaderboard leaderboard;
	private Shop shop;
	private UserManager userManager;
	private VaultManager vaultManager;

	private static Party party = new NoParty();

	//NMS Nbt
	private NBTApi nbtApi;

	@Override
	public void onEnable() {
		HCore.setInstance(this);
		BUtilsLib.setUsingPlugin(this);

        loadPlugin(true);
    }

    public void loadPlugin(boolean load) {
		long start = System.currentTimeMillis();
	    plugin = this;

        if (load) {
            cmds = new HashMap<>();
        }
	    games = new ArrayList<>();
        playerSession = new HashMap<>();
        itemCostMap = new HashMap<>();
		itemRarityMap = new HashMap<>();
		bonusCostMap = new HashMap<>();
		bonusRarityMap = new HashMap<>();

		config = new Config(this);
		Bukkit.getLogger().info("Loading HungerGames by bilektugrul and JT122406");

		//Bukkit.getLogger().info("Your server version is: " + getServer().getVersion() + "     Your Java Version is " + System.getProperty("java.version"));
		String Version = System.getProperty("java.version");
		if (Version.contains("1.8")) {
			Bukkit.getLogger().info("Your Java Version is " + Version + "     This plugin requires Java 11 or higher");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		Version = Version.substring(0, 2);
		double version = Double.parseDouble(Version);

		if (version < 11) {
			Bukkit.getLogger().info("Your Java Version is " + System.getProperty("java.version") + " This plugin requires Java 11 or higher");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		} else if (version >= 11  && version < 17) {
			Bukkit.getLogger().info("Your Java Version is " + System.getProperty("java.version") + " This plugin is compatible with your version but we suggest you update to Java 17 or higher");
		}else if (version >= 17) {
			Bukkit.getLogger().info("Your Java Version is " + System.getProperty("java.version") + " This plugin is compatible with your version");
		}

		//NMS Nbt
		if (Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
			nbtApi = new NBTApi();
			Util.log("&7NBTAPI found, NBTAPI hook &aenabled");
		}

		//MythicMob check
		if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
			//mmMobManager = MythicMobs.inst().getMobManager();
			Util.log("&7MythicMobs found, MythicMobs hook &aenabled");
		} else {
			Util.log("&7MythicMobs not found, MythicMobs hooks have been &cdisabled");
		}
		lang = new Language(this);
		kitManager = new KitManager();
		itemStackManager = new ItemStackManager(this);
		randomItems = new RandomItems(this);
        playerManager = new PlayerManager();
		arenaconfig = new ArenaConfig(this);
		killManager = new KillManager();
		manager = new Manager(this);
		leaderboard = new Leaderboard(this);
		vaultManager = new VaultManager(this);
		userManager = new UserManager(this);
		for (Player player : Bukkit.getOnlinePlayers()) {
			userManager.loadUser(player.getName(), true);
		}

		shop = new Shop(this);
		HoloCmd.leaderboardFile = ConfigUtils.getConfig(this, "features/leaderboardholo");

		//PAPI check
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			new Placeholders(this).register();
			Util.log("&7PAPI found, Placeholders have been &aenabled");
		} else {
			Util.log("&7PAPI not found, Placeholders have been &cdisabled");
		}
		//mcMMO check
		if (Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
		    if (Util.classExists("com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent")) {
                getServer().getPluginManager().registerEvents(new McmmoListeners(this), this);
                Util.log("&7mcMMO found, mcMMO event hooks &aenabled");
            } else {
		        Util.log("&7mcMMO classic found. HungerGames does not support mcMMO classic, mcMMO hooks &cdisabled");
            }
		} else {
			Util.log("&7mcMMO not found, mcMMO event hooks have been &cdisabled");
		}

		//Party support
		if (Config.allowParty)
			if (getServer().getPluginManager().isPluginEnabled("Spigot-Party-API-PAF")){
				getLogger().info("Hook into Spigot Party API for Party and Friends Extended (by Simonsator) support!");
				party = new PAFBungee();
			} else if (getServer().getPluginManager().isPluginEnabled("PartyAndFriends")) {
				getLogger().info("Hook into Party and Friends for Spigot (by Simonsator) support!");
				party = new PAFSpigot();
			} else if (getServer().getPluginManager().isPluginEnabled("Parties")) {
				getLogger().info("Hook into Parties (by AlessioDP) support!");
				party = new Parties();
			}

		getCommand("hg").setExecutor(new CommandListener(this));
		if (load) {
            loadCmds();
        }
		getServer().getPluginManager().registerEvents(new WandListener(this), this);
		getServer().getPluginManager().registerEvents(new CancelListener(this), this);
		getServer().getPluginManager().registerEvents(new GameListener(this), this);
		getServer().getPluginManager().registerEvents(new ChestDropListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

		Util.log("HungerGames has been &aenabled&7 in &b%.2f seconds&7!", (float)(System.currentTimeMillis() - start) / 1000);
	}

	public void reloadPlugin() {
	    unloadPlugin(true);
    }

    private void unloadPlugin(boolean reload) {
        stopAll();

        games = null;
        playerSession = null;
        itemRarityMap = null;
		itemCostMap = null;
        plugin = null;
        config = null;
        nbtApi = null;
        lang = null;
        kitManager = null;
        itemStackManager = null;
        randomItems = null;
        playerManager = null;
        arenaconfig = null;
        killManager = null;
        manager = null;
        leaderboard = null;
		shop = null;
        try {
            userManager.saveUsers();
        } catch (IOException e) {
			throw new RuntimeException(e);
        }
        userManager = null;
        HandlerList.unregisterAll(this);
        if (reload) {
            loadPlugin(false);
        } else {
            cmds = null;
			ConfigUtils.saveConfig(this, HoloCmd.leaderboardFile, "features/leaderboardholo");
        }
    }

    @Override
    public void onDisable() {
        // I know this seems odd, but this method just
        // nulls everything to prevent memory leaks
        unloadPlugin(false);
        Util.log("HungerGames has been disabled!");
    }

	private void loadCmds() {
		cmds.put("team", new TeamCmd());
		cmds.put("addspawn", new AddSpawnCmd());
		cmds.put("create", new CreateCmd());
		cmds.put("join", new JoinCmd());
		cmds.put("leave", new LeaveCmd());
		cmds.put("reload", new ReloadCmd());
		cmds.put("setlobbywall", new SetLobbyWallCmd());
		cmds.put("wand", new WandCmd());
		cmds.put("kit", new KitCmd());
		cmds.put("debug", new DebugCmd());
		cmds.put("list", new ListCmd());
		cmds.put("listgames", new ListGamesCmd());
		cmds.put("forcestart", new StartCmd());
		cmds.put("stop", new StopCmd());
		cmds.put("toggle", new ToggleCmd());
		cmds.put("setexit", new SetExitCmd());
		cmds.put("delete", new DeleteCmd());
		cmds.put("chestrefill", new ChestRefillCmd());
		cmds.put("chestrefillnow", new ChestRefillNowCmd());
		cmds.put("bordersize", new BorderSizeCmd());
		cmds.put("bordercenter", new BorderCenterCmd());
		cmds.put("bordertimer", new BorderTimerCmd());
		cmds.put("shop", new ShopCmd());
		cmds.put("hololoc", new HoloCmd());
		if (Config.spectateEnabled) {
			cmds.put("spectate", new SpectateCmd());
		}
		if (nbtApi != null) {
			cmds.put("nbt", new NBTCmd());
		}

		ArrayList<String> cArray = new ArrayList<>();
		cArray.add("join");
		cArray.add("leave");
		cArray.add("kit");
		cArray.add("listgames");
		cArray.add("list");

		for (String bc : cmds.keySet()) {
			getServer().getPluginManager().addPermission(new Permission("hg." + bc));
			if (cArray.contains(bc))
				getServer().getPluginManager().getPermission("hg." + bc).setDefault(PermissionDefault.TRUE);

		}
	}

	/**
	 * Stop all games
	 */
	public void stopAll() {
		ArrayList<UUID> ps = new ArrayList<>();
		for (Game g : games) {
			g.cancelTasks();
			g.getGameBlockData().forceRollback();
			ps.addAll(g.getGamePlayerData().getPlayers());
			ps.addAll(g.getGamePlayerData().getSpectators());
		}
		for (UUID u : ps) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
			    p.closeInventory();
				if (playerManager.hasPlayerData(u)) {
                    Objects.requireNonNull(playerManager.getPlayerData(u)).getGame().getGamePlayerData().leave(p, false);
                    playerManager.removePlayerData(u);
                }
				if (playerManager.hasSpectatorData(u)) {
                    Objects.requireNonNull(playerManager.getSpectatorData(u)).getGame().getGamePlayerData().leaveSpectate(p);
                    playerManager.removePlayerData(u);
                }
			}
		}
		games.clear();
	}

	public Economy getEconomy() {
		return vaultManager.getEconomy();
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void saveAllUsers() throws IOException {
		userManager.saveUsers();
	}

	/** Get the instance of this plugin
	 * @return This plugin
	 */
	public static HG getPlugin() {
		return plugin;
	}

	/** Get an instance of the RandomItems manager
	 * @return RandomItems manager
	 */
	public RandomItems getRandomItems() {
		return this.randomItems;
	}

	/** Get an instance of the KillManager
	 * @return KillManager
	 */
	public KillManager getKillManager() {
		return this.killManager;
	}

	/** Get an instance of the plugins main kit manager
	 * @return The kit manager
	 */
	public KitManager getKitManager() {
		return this.kitManager;
	}

	/** Get an instance of the ItemStackManager
	 * @return ItemStackManager
	 */
	public ItemStackManager getItemStackManager() {
		return this.itemStackManager;
	}

	/** Get the instance of the manager
	 * @return The manager
	 */
	public Manager getManager() {
		return this.manager;
	}

    /** Get an instance of the PlayerManager
     * @return PlayerManager
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /** Get an instance of the ArenaConfig
	 * @return ArenaConfig
	 */
	public ArenaConfig getArenaConfig() {
		return this.arenaconfig;
	}

	/** Get an instance of HG's leaderboards
	 * @return Leaderboard
	 */
	public Leaderboard getLeaderboard() {
		return this.leaderboard;
	}

	/** Get a list of all loaded games
	 * @return A list of games
	 */
	public List<Game> getGames() {
		return this.games;
	}

	/** Get player sessions map
	 * @return Player Sessions map
	 */
	public Map<UUID, PlayerSession> getPlayerSessions() {
		return this.playerSession;
	}

	/** Get a map of commands
	 * @return Map of commands
	 */
	public Map<String, BaseCmd> getCommands() {
		return this.cmds;
	}

	/** Get an instance of the language file
	 * @return Language file
	 */
	public Language getLang() {
		return this.lang;
	}

    /**
     * Get an instance of {@link Config}
     *
     * @return Config file
     */
    public Config getHGConfig() {
        return config;
    }

	/** Get the NBT API
	 * @return NBT API
	 */
	public NBTApi getNbtApi() {
		return this.nbtApi;
	}

	public Shop getShop() {
		return shop;
	}

	public static Party getParty(){return party;}

	/**
	 * Get an instance of the MythicMobs MobManager
	 *
	 * @return MythicMobs MobManager
	 */
	public Map<ItemStack, Integer> getItemRarityMap() {
		return itemRarityMap;
	}

	public Map<ItemStack, Integer> getItemCostMap() {
		return itemCostMap;
	}
	public Map<ItemStack, Integer> getBonusRarityMap() {
		return bonusRarityMap;
	}

	public Map<ItemStack, Integer> getBonusCostMap() {
		return bonusCostMap;
	}

}
