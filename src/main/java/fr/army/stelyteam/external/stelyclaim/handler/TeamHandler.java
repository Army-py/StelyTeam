package fr.army.stelyteam.external.stelyclaim.handler;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.RegionModifier;
import fr.flowsqy.stelyclaim.common.ConfigRegionModifier;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import org.bukkit.Bukkit;

import java.util.UUID;

public class TeamHandler implements ClaimHandler<TeamOwner> {

    public final static String ID = "team";

    private ConfigRegionModifier<TeamOwner> defineModifier;
    private ConfigRegionModifier<TeamOwner> redefineModifier;
    private ConfigurationFormattedMessages messages;


    public TeamHandler(StelyClaimPlugin stelyClaimPlugin) {
        this.defineModifier = new ConfigRegionModifier<>(stelyClaimPlugin.getConfiguration(), stelyClaimPlugin.getMessages(), "define", o -> null);
        this.redefineModifier = new ConfigRegionModifier<>(stelyClaimPlugin.getConfiguration(), stelyClaimPlugin.getMessages(), "redefine", o -> null);
        this.messages = stelyClaimPlugin.getMessages();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public TeamOwner getOwner(String claimIdentifier) {
        return new TeamOwner(Bukkit.getOfflinePlayer(UUID.fromString(claimIdentifier)));
    }
    
    @Override
    public String getIdentifier(TeamOwner owner) {
        return owner.getPlayer().getUniqueId().toString();
    }

    @Override
    public RegionModifier<TeamOwner> getDefineModifier() {
        return defineModifier;
    }

    @Override
    public RegionModifier<TeamOwner> getRedefineModifier() {
        return redefineModifier;
    }

    @Override
    public FormattedMessages getMessages() {
        return messages;
    }

}
