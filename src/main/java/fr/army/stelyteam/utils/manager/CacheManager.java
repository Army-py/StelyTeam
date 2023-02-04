package fr.army.stelyteam.utils.manager;

import java.util.ArrayList;

import fr.army.stelyteam.utils.Page;
import fr.army.stelyteam.utils.Storage;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;

public class CacheManager {
    // {senderName, receiverName, actionName, teamName, teamPrefix}
    private ArrayList<TemporaryAction> cachedTempAction = new ArrayList<TemporaryAction>();

    // {teamName, storageId, storageInstance, storageContent}
    private ArrayList<Storage> cachedStorage = new ArrayList<Storage>();

    // {playerName}
    private ArrayList<String> cachedInConversation = new ArrayList<String>();

    // {authorName, currentPage, maxElementsPerPage, teams}
    private ArrayList<Page> cachedPages = new ArrayList<Page>();
    
    // {teamName, teamPrefix, teamDescription, teamMoney, creationDate, improvLvlMembers, teamStorageLvl, unlockedTeamBank, teamOwnerName, teamMembers, teamPermissions, teamAlliances}
    private ArrayList<Team> cachedTeam = new ArrayList<Team>();



    public void addTempAction(TemporaryAction tempAction){
        cachedTempAction.add(tempAction);
    }

    public TemporaryAction getTempAction(String playerName){
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getSenderName().equals(playerName) || tempAction.getReceiverName().equals(playerName)){
                return tempAction;
            }
        }
        return null;
    }

    public TemporaryActionNames getPlayerActionName(String playerName){
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getSenderName().equals(playerName) || tempAction.getReceiverName().equals(playerName)){
                return tempAction.getActionName();
            }
        }
        return null;
    }

    public void removePlayerAction(String playerName){
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getSenderName().equals(playerName) || tempAction.getReceiverName().equals(playerName)){
                cachedTempAction.remove(tempAction);
                return;
            }
        }
    }

    public void removePlayerActionName(String playerName, TemporaryActionNames actionName){
        for(TemporaryAction tempAction : cachedTempAction){
            if(tempAction.getSenderName().equals(playerName) || tempAction.getReceiverName().equals(playerName)){
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
            if(tempAction.getSenderName().equals(playerName) || tempAction.getReceiverName().equals(playerName)){
                return true;
            }
        }
        return false;
    }

    public boolean playerHasActionName(String playerName, TemporaryActionNames actionName){
        if (cachedTempAction.isEmpty()) return false;
        for(TemporaryAction tempAction : cachedTempAction){
            if((tempAction.getSenderName().equals(playerName) || tempAction.getReceiverName().equals(playerName)) && tempAction.getActionName().equals(actionName)){
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

    public Storage getStorage(Team team, int storageId){
        for(Storage storage : cachedStorage){
            if(storage.getTeam().getTeamName().equals(team.getTeamName()) && storage.getStorageId() == storageId){
                return storage;
            }
        }
        return null;
    }

    public void replaceStorage(Storage storage){
        for(Storage cachedStorage : cachedStorage){
            if(cachedStorage.getTeam().getTeamName().equals(storage.getTeam().getTeamName()) && cachedStorage.getStorageId() == storage.getStorageId()){
                cachedStorage = storage;
            }
        }
    }

    public boolean containsStorage(Team team, int storageId){
        for(Storage storage : cachedStorage){
            if(storage.getTeam().getTeamName().equals(team.getTeamName()) && storage.getStorageId() == storageId){
                return true;
            }
        }
        return false;
    }

    public void saveStorage(Storage storage){
        if(containsStorage(storage.getTeam(), storage.getStorageId())){
            replaceStorage(storage);
        } else {
            addStorage(storage);
        }
    }


    public void addInConversation(String playerName){
        cachedInConversation.add(playerName);
    }

    public void removeInConversation(String playerName){
        cachedInConversation.remove(playerName);
    }

    public boolean isInConversation(String playerName){
        return cachedInConversation.contains(playerName);
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
        cachedTeam.add(team);
    }

    public void removeTeam(Team team){
        cachedTeam.remove(team);
    }

    public Team getTeam(String teamName){
        for(Team team : cachedTeam){
            if(team.getTeamName().equals(teamName)){
                return team;
            }
        }
        return null;
    }

    public void replaceTeam(Team team){
        for(Team cachedTeam : cachedTeam){
            if(cachedTeam.getTeamName().equals(team.getTeamName())){
                cachedTeam = team;
            }
        }
    }

    public boolean containsTeam(String teamName){
        for(Team team : cachedTeam){
            if(team.getTeamName().equals(teamName)){
                return true;
            }
        }
        return false;
    }

    public boolean containsTeam(Team team){
        for(Team cachedTeam : cachedTeam){
            if(cachedTeam.getTeamName().equals(team.getTeamName())){
                return true;
            }
        }
        return false;
    }
}
