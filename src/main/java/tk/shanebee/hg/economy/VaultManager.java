package tk.shanebee.hg.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import tk.shanebee.hg.HG;

public class VaultManager {

    private final HG plugin;
    private Economy economy;

    public VaultManager(HG plugin) {
        this.plugin = plugin;

        setupEconomy();
    }

    public void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();
    }

    public Economy getEconomy() {
        return economy;
    }

}