package fr.army.stelyteam.external.stelyclaim;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ExternalStelyClaimLoader {

    private ExternalStelyClaimSession stelyClaimSession;

    public void load() {
        Plugin stelyclaimPlugin = Bukkit.getPluginManager().getPlugin("StelyClaim");

        if (stelyclaimPlugin == null || !stelyclaimPlugin.isEnabled()) {
            return;
        }
        if (!(stelyclaimPlugin instanceof StelyClaimPlugin)) {
            return;
        }

        stelyClaimSession = new ExternalStelyClaimSession((StelyClaimPlugin) stelyclaimPlugin);
        stelyClaimSession.enable();
    }

    public void unload() {
        if (stelyClaimSession == null) {
            return;
        }

        stelyClaimSession.disable();
    }

}
