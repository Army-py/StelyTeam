package fr.army.stelyteam.utils.manager;

import java.util.ArrayList;

import fr.army.stelyteam.utils.Storage;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;

public class CacheManager {
    // {senderName, receiverName, actionName, teamName, teamPrefix}
    private ArrayList<TemporaryAction> cachedTempAction = new ArrayList<TemporaryAction>();

    // {teamName, storageId, storageInstance, storageContent}
    private ArrayList<Storage> cachedStorage = new ArrayList<Storage>();
    
    // {teamName, teamPrefix, teamDescription, teamMoney, creationDate, improvLvlMembers, teamStorageLvl, unlockedTeamBank, teamOwnerName}
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

    public String getPlayerActionName(String playerName){
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

    public boolean playerHasActionName(String playerName, String actionName){
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
}
