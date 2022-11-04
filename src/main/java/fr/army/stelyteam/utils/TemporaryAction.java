package fr.army.stelyteam.utils;

public class TemporaryAction {
    private String senderName;
    private String receiverName;
    private TemporaryActionNames actionName;
    private Team team;

    public TemporaryAction(String senderName, TemporaryActionNames actionName){
        this.senderName = senderName;
        this.actionName = actionName;
    }

    public TemporaryAction(String senderName, String receiverName, TemporaryActionNames actionName, Team team){
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.actionName = actionName;
        this.team = team;
    }

    public TemporaryAction(String senderName, Team team){
        this.senderName = senderName;
        this.team = team;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public TemporaryActionNames getActionName() {
        return actionName;
    }

    public Team getTeam() {
        return team;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setActionName(TemporaryActionNames actionName) {
        this.actionName = actionName;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
