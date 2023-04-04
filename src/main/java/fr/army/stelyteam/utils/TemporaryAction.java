package fr.army.stelyteam.utils;

public class TemporaryAction {
    private String senderName = "";
    private String targetName = "";
    private TemporaryActionNames actionName = TemporaryActionNames.NULL;
    private Team team;

    public TemporaryAction(String senderName, TemporaryActionNames actionName, Team team){
        this.senderName = senderName;
        this.actionName = actionName;
        this.team = team;
    }

    public TemporaryAction(String senderName, String targetName, TemporaryActionNames actionName, Team team){
        this.senderName = senderName;
        this.targetName = targetName;
        this.actionName = actionName;
        this.team = team;
    }

    public TemporaryAction(String senderName, Team team){
        this.senderName = senderName;
        this.team = team;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public String getTargetName() {
        return this.targetName;
    }

    public TemporaryActionNames getActionName() {
        return this.actionName;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setActionName(TemporaryActionNames actionName) {
        this.actionName = actionName;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
