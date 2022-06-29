package fr.army.stelyteam.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;


public class EconomyManager {
    public static Economy economy = null;

    public boolean checkMoneyPlayer(Player player, Double money) {
        return economy.getBalance(player) >= ((double) money);
    }

    public void removeMoneyPlayer(Player player, Double money) {
        economy.withdrawPlayer(player, money);
        // player.sendMessage("Vous avez payé " + money + "€");
        player.sendMessage(MessageManager.getReplaceMessage("payments.paid", DoubleToString(money)));
    }

    public void addMoneyPlayer(Player player, Double money) {
        economy.depositPlayer(player, money);
        // player.sendMessage("Vous avez reçu " + money + "€");
        player.sendMessage(MessageManager.getReplaceMessage("payments.received", DoubleToString(money)));
    }

    public static boolean setupEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null); 
	}

    private static String DoubleToString(double value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}
