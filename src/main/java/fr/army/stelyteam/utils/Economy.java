package fr.army.stelyteam.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import net.craftersland.data.bridge.PD;


public class Economy {
    public boolean checkMoneyPlayer(Player player, int money) {
        return PD.api.getDatabaseMoney(player.getName()) >= ((double) money);
    }

    public void removeMoneyPlayer(Player player, int money) {
        StelyTeamPlugin.economy.withdrawPlayer(player, money);
    }
}
