package fr.army.stelyteam.external;

import fr.army.stelyteam.external.stelyclaim.ExternalStelyClaimLoader;

public class ExternalManager {

    private final ExternalStelyClaimLoader stelyClaimLoader;

    public ExternalManager() {
        stelyClaimLoader = new ExternalStelyClaimLoader();
    }
    
    public void load(){
        stelyClaimLoader.load();
    }

    public void unload(){
        stelyClaimLoader.unload();
    }

}
