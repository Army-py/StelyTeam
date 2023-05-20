package fr.army.stelyteam.external;

import fr.army.stelyteam.external.stelyclaim.StelyClaimLoader;

public class ExternalManager {

    private final StelyClaimLoader stelyClaimLoader;

    public ExternalManager() {
        stelyClaimLoader = new StelyClaimLoader();
    }
    
    public void load(){
        stelyClaimLoader.load();
    }

    public void unload(){
        stelyClaimLoader.unload();
    }

}
