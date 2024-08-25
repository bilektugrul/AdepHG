package tk.shanebee.hg.commands;

import tk.shanebee.hg.shop.MainShopGUI;

public class ShopCmd extends BaseCmd {

    public ShopCmd() {
        forcePlayer = true;
        cmdName = "shop";
        argLength = 1;
        forceInRegion = false;
    }

    @Override
    public boolean run() {
        new MainShopGUI(plugin.getShop(), plugin.getShop().mainShop).open(player);
        return true;
    }
}
