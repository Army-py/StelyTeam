package fr.army.stelyteam.utils.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Page;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.manager.serializer.ItemStackSerializer;

public class CacheManager {

    private final ItemStackSerializer serializeManager;

    // {senderName, receiverName, actionName, Team}
    private Set<TemporaryAction> cachedTempAction = new HashSet<TemporaryAction>();

    // {Team, storageId, storageInstance, storageContent}
    private Set<Storage> cachedStorage = new HashSet<Storage>();

    // {playerName}
    private Set<String> cachedPlayerInConversation = new HashSet<String>();

    // {authorName, currentPage, maxElementsPerPage, teams}
    private Set<Page> cachedPages = new HashSet<Page>();

    // private Set<Team> cachedTeams = Collections.synchronizedSet(new HashSet<Team>());
    private Set<Team> cachedTeams = new HashSet<Team>();

    // private Set<UUID> cachedTeamPlayers = Collections.synchronizedSet(new HashSet<UUID>());
    private Set<UUID> cachedTeamPlayers = new HashSet<UUID>();


    public CacheManager(StelyTeamPlugin plugin){
        this.serializeManager = plugin.getSerializeManager();
    }



    public void addTempAction(TemporaryAction tempAction){
        cachedTempAction.add(tempAction);
    }

    public TemporaryAction getTempAction(String playerName){
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getSenderName().equals(playerName) || tempAction.getTargetName().equals(playerName)){
                return tempAction;
            }
        }
        return null;
    }

    public TemporaryActionNames getPlayerActionName(String playerName){
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getSenderName().equals(playerName) || tempAction.getTargetName().equals(playerName)){
                return tempAction.getActionName();
            }
        }
        return null;
    }

    public void removePlayerAction(String playerName){
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getSenderName().equals(playerName) || tempAction.getTargetName().equals(playerName)){
                cachedTempAction.remove(tempAction);
                return;
            }
        }
    }

    public void removePlayerActionName(String playerName, TemporaryActionNames actionName){
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getSenderName().equals(playerName) || tempAction.getTargetName().equals(playerName)){
                if(tempAction.getActionName().equals(actionName)){
                    cachedTempAction.remove(tempAction);
                    return;
                }
            }
        }
    }

    public boolean playerHasAction(String playerName){
        if (cachedTempAction.isEmpty()) return false;
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getSenderName().equals(playerName) || tempAction.getTargetName().equals(playerName)){
                return true;
            }
        }
        return false;
    }

    public boolean playerHasActionName(String playerName, TemporaryActionNames actionName){
        if (cachedTempAction.isEmpty()) return false;
        for(TemporaryAction tempAction : cachedTempAction){
            if((tempAction.getSenderName().equals(playerName) || tempAction.getTargetName().equals(playerName)) && tempAction.getActionName().equals(actionName)){
                return true;
            }
        }
        return false;
    }

    public boolean containsActionName(TemporaryActionNames actionName){
        if (cachedTempAction.isEmpty()) return false;
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getActionName().equals(actionName)){
                return true;
            }
        }
        return false;
    }

    public void setActionTeamPrefix(String playerName, String teamPrefix){
        TemporaryAction action = getTempAction(playerName);
        Team team = action.getTeam();
        team.setTeamPrefix(teamPrefix);
        action.setTeam(team);
    }


    public void addStorage(Storage storage){
        if (storage == null) return;
        cachedStorage.add(storage);
    }

    public void removeStorage(Storage storage){
        cachedStorage.removeIf(cStorage -> cStorage.getTeamUuid().equals(storage.getTeamUuid()) && cStorage.getStorageId() == storage.getStorageId());
    }

    public Storage getStorage(UUID teamUuid, int storageId){
        return cachedStorage.stream().filter(storage -> storage.getTeamUuid().equals(teamUuid) && storage.getStorageId() == storageId).findFirst().orElse(null);
    }

    // TODO : Régler l'erreur
    public void replaceStorage(Storage storage, boolean updateInstanceContent){
        for (Storage cStorage : cachedStorage){
            final UUID teamUuid = cStorage.getTeamUuid();
            final int storageId = cStorage.getStorageId();
            if (teamUuid.equals(storage.getTeamUuid()) && storageId == storage.getStorageId()){
                cStorage.setOpenedServerName(storage.getOpenedServerName());
                cStorage.setStorageContent(storage.getStorageContent());
                if (updateInstanceContent) cStorage.setStorageInstanceContent(serializeManager.deserializeFromByte(storage.getStorageContent()));
            }
        }
    }

    public boolean containsStorage(UUID teamUuid, int storageId){
        return cachedStorage.stream().anyMatch(storage -> storage.getTeamUuid().equals(teamUuid) && storage.getStorageId() == storageId);
    }

    public void saveStorage(Storage storage, boolean updateInstanceContent){
        if(containsStorage(storage.getTeamUuid(), storage.getStorageId())){
            replaceStorage(storage, updateInstanceContent);
        } else {
            addStorage(storage);
        }
    }


    public void addInConversation(String playerName){
        cachedPlayerInConversation.add(playerName);
    }

    public void removeInConversation(String playerName){
        cachedPlayerInConversation.remove(playerName);
    }

    public boolean isInConversation(String playerName){
        return cachedPlayerInConversation.contains(playerName);
    }


    public void addPage(Page page){
        cachedPages.add(page);
    }

    public void removePage(Page page){
        cachedPages.remove(page);
    }

    public Page getPage(String authorName){
        for(Page page : cachedPages){
            if(page.getAuthorName().equals(authorName)){
                return page;
            }
        }
        return null;
    }

    public void replacePage(Page page){
        cachedPages.removeIf(cachedPage -> cachedPage.getAuthorName().equals(page.getAuthorName()));
        cachedPages.add(page);
    }

    public boolean containsPage(String authorName){
        for(Page page : cachedPages){
            if(page.getAuthorName().equals(authorName)){
                return true;
            }
        }
        return false;
    }


    public void addTeam(Team team){
        if (team == null) return;
        cachedTeams.removeIf(cachedTeam -> cachedTeam.getTeamUuid().equals(team.getTeamUuid()));
        cachedTeams.add(team);
    }

    public void removeTeam(Team team){
        for(Team cachedTeam : cachedTeams){
            if(cachedTeam.getTeamUuid().equals(team.getTeamUuid())){
                cachedTeams.remove(cachedTeam);
            }
        }
    }

    public void replaceTeam(Team team){
        for(Team cachedTeam : cachedTeams){
            if(cachedTeam.equals(team)){
                cachedTeam = team;
            }
        }
    }

    public Team getTeamByUuid(UUID teamUuid){
        for(Team team : cachedTeams){
            if(team.getTeamUuid().equals(teamUuid)){
                return team;
            }
        }
        return null;
    }

    public Team getTeamByPlayerName(String playerName){
        for(Team team : cachedTeams){
            if (team.isTeamMember(playerName)){
                return team;
            }
        }
        return null;
    }

    public Team getTeamByPlayerUuid(UUID playerUuid){
        for(Team team : cachedTeams){
            if (team.isTeamMember(playerUuid)){
                return team;
            }
        }
        return null;
    }

    public Team getTeamByName(String teamName){
        for(Team team : cachedTeams){
            if(team.getTeamName().equals(teamName)){
                return team;
            }
        }
        return null;
    }


    public void addTeamPlayer(UUID playerUuid){
        cachedTeamPlayers.add(playerUuid);
    }

    public boolean removeTeamPlayer(UUID playerUuid){
        return cachedTeamPlayers.remove(playerUuid);
    }

    public boolean containsTeamPlayer(UUID playerUuid){
        return cachedTeamPlayers.contains(playerUuid);
    }
}
