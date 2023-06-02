package fr.army.stelyteam.external;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.external.essentialschat.ExternalEssentialsChatLoader;
import fr.army.stelyteam.external.stelyclaim.ExternalStelyClaimLoader;

public class ExternalManager {

    private final ExternalStelyClaimLoader stelyClaimLoader;
    private final ExternalEssentialsChatLoader essentialsChatLoader;

    public ExternalManager() {
        stelyClaimLoader = new ExternalStelyClaimLoader();
        essentialsChatLoader = new ExternalEssentialsChatLoader();
    }

    public void load() {
        // stelyClaimLoader.load();
        essentialsChatLoader.load(StelyTeamPlugin.getPlugin());
    }

    public void unload() {
        // stelyClaimLoader.unload();
    }

}
