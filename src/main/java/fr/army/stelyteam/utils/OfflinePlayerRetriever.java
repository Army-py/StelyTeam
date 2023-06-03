package fr.army.stelyteam.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class OfflinePlayerRetriever {

    @SuppressWarnings("deprecation")
    @NotNull
    public static OfflinePlayer getOfflinePlayer(@NotNull String playerName) {
        return Bukkit.getOfflinePlayer(playerName);
    }

}
