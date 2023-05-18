package fr.army.stelyteam.utils.manager;

import java.util.ArrayList;

import fr.army.stelyteam.utils.Page;
import fr.army.stelyteam.utils.Storage;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;

public class CacheManager {
    // {senderName, receiverName, actionName, Team}
    private ArrayList<TemporaryAction> cachedTempAction = new ArrayList<TemporaryAction>();

    // {Team, storageId, storageInstance, storageContent}
    private ArrayList<Storage> cachedStorage = new ArrayList<Storage>();

    // {playerName}
    private ArrayList<String> cachedPlayerInConversation = new ArrayList<String>();

    // {authorName, currentPage, maxElementsPerPage, teams}
    private ArrayList<Page> cachedPages = new ArrayList<Page>();



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

    public Storage getStorage(String teamName, int storageId){
        for(Storage storage : cachedStorage){
            if(storage.getTeam().getTeamName().equals(teamName) && storage.getStorageId() == storageId){
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

    public Storage replaceStorageContent(String teamName, int storageId, byte[] content){
        if (!containsStorage(teamName, storageId)) return null;
        for(Storage cachedStorage : cachedStorage){
            if(cachedStorage.getTeam().getTeamName().equals(teamName) && cachedStorage.getStorageId() == storageId){
                cachedStorage.setStorageContent(content);
                return cachedStorage;
            }
        }
        return null;
    }

    public void replaceStorageTeam(String teamName, Team team){
        if (cachedStorage.isEmpty()) return;
        for(Storage cachedStorage : cachedStorage){
            if(cachedStorage.getTeam().getTeamName().equals(teamName)){
                cachedStorage.setTeam(team);
            }
        }
    }

    public boolean containsStorage(String teamName, int storageId){
        if (cachedStorage.isEmpty()) return false;
        for(Storage storage : cachedStorage){
            if(storage.getTeam().getTeamName().equals(teamName) && storage.getStorageId() == storageId){
                return true;
            }
        }
        return false;
    }

    public void saveStorage(Storage storage){
        if(containsStorage(storage.getTeam().getTeamName(), storage.getStorageId())){
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
}
