package fr.army.stelyteam.external.stelyclaim;

import fr.army.stelyteam.external.stelyclaim.handler.TeamHandler;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;

public class ExternalStelyClaimSession {

    private final StelyClaimPlugin stelyclaimPlugin;
    private TeamHandler teamHandler;

    public ExternalStelyClaimSession(StelyClaimPlugin stelyclaimPlugin) {
        this.stelyclaimPlugin = stelyclaimPlugin;
        teamHandler = null;
    }

    public void enable() {
        teamHandler = new TeamHandler(stelyclaimPlugin);
        stelyclaimPlugin.getProtocolManager().registerHandler(teamHandler);
    }

    public void disable() {
        stelyclaimPlugin.getProtocolManager().unregisterHandler(teamHandler);
    }
}
