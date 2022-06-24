package fr.army.stelyteam.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;


public class EconomyManager {
    public static Economy economy = null;

    public boolean checkMoneyPlayer(Player player, int money) {
        return economy.getBalance(player) >= ((double) money);
    }

    public void removeMoneyPlayer(Player player, int money) {
        economy.withdrawPlayer(player, money);
        player.sendMessage("Vous avez payé " + money + "€");
    }

    public void addMoneyPlayer(Player player, int money) {
        economy.depositPlayer(player, money);
        player.sendMessage("Vous avez reçu " + money + "€");
    }

    public static boolean setupEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null); 
	}
}
