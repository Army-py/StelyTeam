package fr.army.stelyteam.utils.manager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import fr.army.stelyteam.team.Page;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;

public class CacheManager {
    // {senderName, receiverName, actionName, Team}
    private Set<TemporaryAction> cachedTempAction = new HashSet<TemporaryAction>();

    // {Team, storageId, storageInstance, storageContent}
    private Set<Storage> cachedStorage = new HashSet<Storage>();

    // {playerName}
    private Set<String> cachedPlayerInConversation = new HashSet<String>();

    // {authorName, currentPage, maxElementsPerPage, teams}
    private Set<Page> cachedPages = new HashSet<Page>();

    private Set<Team> cachedTeams = Collections.synchronizedSet(new HashSet<Team>());

    private Set<UUID> cachedTeamPlayers = Collections.synchronizedSet(new HashSet<UUID>());



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
        cachedStorage.add(storage);
    }

    public void removeStorage(Storage storage){
        cachedStorage.remove(storage);
    }

    public Storage getStorage(UUID teamUuid, int storageId){
        for(Storage storage : cachedStorage){
            if(storage.getTeamUuid().equals(teamUuid) && storage.getStorageId() == storageId){
                return storage;
            }
        }
        return null;
    }

    public void replaceStorage(Storage storage){
        for(Storage cachedStorage : cachedStorage){
            if(cachedStorage.getTeamUuid().equals(storage.getTeamUuid()) && cachedStorage.getStorageId() == storage.getStorageId()){
                cachedStorage = storage;
            }
        }
    }

    public Storage replaceStorageContent(UUID teamUuid, int storageId, byte[] content){
        if (!containsStorage(teamUuid, storageId)) return null;
        for(Storage cachedStorage : cachedStorage){
            if(cachedStorage.getTeamUuid().equals(teamUuid) && cachedStorage.getStorageId() == storageId){
                cachedStorage.setStorageContent(content);
                return cachedStorage;
            }
        }
        return null;
    }

    public boolean containsStorage(UUID teamUuid, int storageId){
        if (cachedStorage.isEmpty()) return false;
        for(Storage storage : cachedStorage){
            if(storage.getTeamUuid().equals(teamUuid) && storage.getStorageId() == storageId){
                return true;
            }
        }
        return false;
    }

    public void saveStorage(Storage storage){
        if(containsStorage(storage.getTeamUuid(), storage.getStorageId())){
            replaceStorage(storage);
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
        for(Page cachedPage : cachedPages){
            if(cachedPage.getAuthorName().equals(page.getAuthorName())){
                cachedPage = page;
            }
        }
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
        cachedTeams.add(team);
    }

    public void removeTeam(Team team){
        for(Team cachedTeam : cachedTeams){
            if(cachedTeam.getTeamUuid().equals(team.getTeamUuid())){
                cachedTeams.remove(cachedTeam);
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
