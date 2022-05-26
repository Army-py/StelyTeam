package fr.army.stelyteam.utils;

import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;


public class Economy {
    public boolean checkMoneyPlayer(Player player, int money) {
        return StelyTeamPlugin.economy.getBalance(player) >= ((double) money);
    }

    public void removeMoneyPlayer(Player player, int money) {
        StelyTeamPlugin.economy.withdrawPlayer(player, money);
    }

    public void addMoneyPlayer(Player player, int money) {
        StelyTeamPlugin.economy.depositPlayer(player, money);
    }
}
