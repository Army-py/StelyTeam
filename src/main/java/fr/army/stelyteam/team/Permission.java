package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.SaveField;

public class Permission {
    
    private final Property<String> name;
    private final Property<Integer> rank;

    public Permission(String permissionName, int teamRank){
        this.name = new Property<>(SaveField.PERMISSION_NAME);
        this.rank = new Property<>(SaveField.PERMISSION_RANK);
    }


    public Property<String> getName() {
        return name;
    }

    public Property<Integer> getTeamRank() {
        return rank;
    }

    public void incrementTeamRank(){
        int value = this.rank.get() == null ? 0 : this.rank.get();
        this.rank.set(value + 1);
    }

    public void decrementTeamRank(){
        int value = this.rank.get() == null ? 0 : this.rank.get();
        this.rank.set(value - 1);
    }
}
