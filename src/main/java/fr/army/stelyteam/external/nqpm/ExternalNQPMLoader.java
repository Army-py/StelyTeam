package fr.army.stelyteam.external.nqpm;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ExternalNQPMLoader {

    public void load() {
        final Plugin nqpmPlugin = Bukkit.getPluginManager().getPlugin("NoQueuePluginMessage");

        if (nqpmPlugin == null || !nqpmPlugin.isEnabled()) {
            return;
        }
        try {
            Class.forName("fr.flowsqy.noqueuepluginmessage.api.NoQueuePluginMessage");
        } catch (ClassNotFoundException ignored) {
            return;
        }
        enable();
    }

    private void enable() {

    }

}
