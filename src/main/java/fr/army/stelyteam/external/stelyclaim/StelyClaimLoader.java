package fr.army.stelyteam.external.stelyclaim;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;


public class StelyClaimLoader {

    private StelyClaimSession stelyClaimSession;

    public void load(){
        Plugin stelyclaimPlugin = Bukkit.getPluginManager().getPlugin("StelyClaim");

        if (stelyclaimPlugin == null || !stelyclaimPlugin.isEnabled()) return;
        if (!(stelyclaimPlugin instanceof StelyClaimPlugin)) return;

        this.stelyClaimSession = new StelyClaimSession((StelyClaimPlugin) stelyclaimPlugin);
        this.stelyClaimSession.enable();
    }

    public void unload(){
        if (stelyClaimSession == null) return;

        this.stelyClaimSession.disable();
    }
}
