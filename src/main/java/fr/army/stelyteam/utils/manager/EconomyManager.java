package fr.army.stelyteam.utils.manager;

import java.text.NumberFormat;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import fr.army.stelyteam.StelyTeamPlugin;
import net.milkbowl.vault.economy.Economy;


public class EconomyManager {
    private static Economy economy = null;

    public EconomyManager(){
        setupEconomy();
    }

    public static boolean checkMoneyPlayer(Player player, Double money) {
        return economy.getBalance(player) >= ((double) money);
    }

    public static void removeMoneyPlayer(Player player, double money) {
        economy.withdrawPlayer(player, money);
//        player.sendRawMessage(messageManager.getReplaceMessage("payments.paid", DoubleToString(money)));
        // TODO : Fix this with new config
    }

    public static void addMoneyPlayer(Player player, double money) {
        economy.depositPlayer(player, money);
//        player.sendRawMessage(messageManager.getReplaceMessage("payments.received", DoubleToString(money)));
        // TODO : Fix this with new config
    }

    public void setupEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
    }

    private String DoubleToString(double value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}
