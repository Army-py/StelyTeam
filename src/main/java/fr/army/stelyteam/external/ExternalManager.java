package fr.army.stelyteam.external;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.plugin.Plugin;

import fr.army.stelyteam.external.bungeechatconnect.ExternalBCCLoader;
import fr.army.stelyteam.external.bungeechatconnect.handler.RecipientsHandler;
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
