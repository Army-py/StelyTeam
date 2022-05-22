package fr.army.stelyteam.utils;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;


public class Economy {

    private final net.milkbowl.vault.economy.Economy economy;

    public Economy() {
        this.economy = setupEconomy();
        if (economy == null) {
            throw new RuntimeException("Can not setup Vault economy");
        }
    }

    private net.milkbowl.vault.economy.Economy setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return null;
        }
        final RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            return null;
        }
        return rsp.getProvider();
    }

    public boolean checkMoneyPlayer(Player player, double money) {
        return economy.getBalance(player) >= money;
    }

    public boolean removeMoneyPlayer(Player player, int money) {
        final EconomyResponse response = economy.withdrawPlayer(player, money);
        return response.transactionSuccess();
    }
}
