package fr.army.stelyteam.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import fr.army.stelyteam.StelyTeamPlugin;
import net.milkbowl.vault.economy.Economy;


public class EconomyManager {
    public static Economy economy = null;

    public boolean checkMoneyPlayer(Player player, int money) {
        return StelyTeamPlugin.economy.getBalance(player) >= ((double) money);
    }

    public void removeMoneyPlayer(Player player, int money) {
        StelyTeamPlugin.economy.withdrawPlayer(player, money);
    }

    public void addMoneyPlayer(Player player, int money) {
        StelyTeamPlugin.economy.depositPlayer(player, money);
    }

    public static boolean setupEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null); 
	}
}
