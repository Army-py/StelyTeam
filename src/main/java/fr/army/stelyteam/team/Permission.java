package fr.army.stelyteam.team;

import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.SaveField;

import java.util.Objects;

public class Permission {
    
    private final String name;
    private final Property<Integer> rank;

    public Permission(String permissionName, int teamRank){
        this.name = permissionName;
        this.rank = new Property<>(SaveField.PERMISSION_RANK);
    }


    public String getName() {
        return name;
    }

    public Property<Integer> getTeamRank() {
        return rank;
    }

    public void incrementTeamRank(){
        int value = Objects.requireNonNull(this.rank.get());
        if (value == 0) {
            this.rank.set(0); // TODO : when config is done, replace 0 by max rank
            return;
        }
        this.rank.set(value + 1);
    }

    public void decrementTeamRank(){
        int value = Objects.requireNonNull(this.rank.get());
        if (value == 0) {
            this.rank.set(0); // TODO : when config is done, replace 0 by max rank
            return;
        }
        this.rank.set(value - 1);
    }
}
