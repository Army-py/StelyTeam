package fr.army.stelyteam.external.stelyclaim;

import fr.army.stelyteam.external.stelyclaim.handler.TeamHandler;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;

public class StelyClaimSession {
    
    private StelyClaimPlugin stelyclaimPlugin;
    private TeamHandler teamHandler;

    public StelyClaimSession(StelyClaimPlugin stelyclaimPlugin){
        this.stelyclaimPlugin = stelyclaimPlugin;
        this.teamHandler = null;
    }


    public void enable(){
        this.teamHandler = new TeamHandler(stelyclaimPlugin);
        stelyclaimPlugin.getProtocolManager().registerHandler(teamHandler);
    }

    public void disable(){
        stelyclaimPlugin.getProtocolManager().unregisterHandler(teamHandler);
    }
}
