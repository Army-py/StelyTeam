package fr.army.stelyteam.external;

import fr.army.stelyteam.external.stelyclaim.StelyClaimLoader;

public class ExternalManager {
    
    public void load(){
        StelyClaimLoader stelyClaimLoader = new StelyClaimLoader();
        stelyClaimLoader.load();
    }

    public void unload(){
        StelyClaimLoader stelyClaimLoader = new StelyClaimLoader();
        stelyClaimLoader.unload();
    }
}
